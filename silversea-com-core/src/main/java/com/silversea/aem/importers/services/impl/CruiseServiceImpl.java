package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.TagManager.FindResults;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.components.beans.LowestPrice;
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.enums.CruiseType;
import com.silversea.aem.enums.PriceVariations;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruiseService;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.Itinerary;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.Price;
import io.swagger.client.model.ShorexItinerary;
import io.swagger.client.model.SpecialOfferByMarket;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyagePriceMarket;
import io.swagger.client.model.VoyageSpecialOffer;

@Service
@Component(label = "Silversea.com - Cruises service")
public class CruiseServiceImpl implements CruiseService{

    static final private Logger LOGGER = LoggerFactory.getLogger(CruiseServiceImpl.class);

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private ApiCallService apiCallService;

    @Reference
    Replicator replicator;
    
    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    private TagManager tagManager;
    private AssetManager assetManager;
    private Session session;

    public void init() {
        try {      
            Map<String, Object> authenticationPrams = new HashMap<String, Object>();
            authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
            pageManager = resourceResolver.adaptTo(PageManager.class);
            tagManager = resourceResolver.adaptTo(TagManager.class);
            assetManager = resourceResolver.adaptTo(AssetManager.class);
            session = resourceResolver.adaptTo(Session.class);
        } catch (LoginException e) {
            LOGGER.debug("Cruise importer login exception ", e);
        }
    }

    public Page getCruisePage(Page destinationPage,Integer voyageId,String voyageName) throws WCMException, RepositoryException{

        // Retrieve and create or update cruise page
        Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH,
                NameConstants.NT_PAGE, "cruiseId", Objects.toString(voyageId),
                resourceResolver);
        Page cruisePage = ImporterUtils.adaptOrCreatePage(resources, ImportersConstants.CUISE_TEMPLATE, destinationPage,
                (voyageName + " " +voyageId), pageManager);

        return cruisePage;
    }

    public void setCruiseTags(List<Integer> features,Integer voyageId,boolean isExpedition, Page page) throws RepositoryException {

        List<Tag> tags = new ArrayList<Tag>();
        if (features != null && !features.isEmpty()) {
            features.forEach(item -> {
                Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_TAGS_PATH, "cq:Tag", "featureId",
                        Objects.toString(item), resourceResolver);
                if(resources.hasNext()){
                    Resource resource = resources.next();
                    if (resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)) {
                        tags.add(resource.adaptTo(Tag.class));
                    } 
                }
            });
        }
        else{
            LOGGER.debug("Cruise importer -- No feature found for cruise with id", voyageId);
        }

        CruiseType cruiseType = isExpedition ? CruiseType.SILVERSEA_CTUISE
                : CruiseType.SILVERSEA_EXPEDITION_CTUISE;

        FindResults findResults = tagManager.findByTitle(cruiseType.getValue());
        if (findResults != null && !ArrayUtils.isEmpty(findResults.tags)) {
            tags.addAll(Arrays.asList(tagManager.findByTitle(cruiseType.getValue()).tags));
        }
        if (!tags.isEmpty() && page != null) {
            ImporterUtils.addMixin(page,"cq:Taggable");
            tagManager.setTags(page.getContentResource(), tags.stream().toArray(Tag[]::new));
        }
    }

    public void buildOrUpdateIteneraries(Page cruisePage,Integer voyageId,String url)
            throws RepositoryException, IOException, ApiException {

        // Retrieve or create itineraries node
        Node itinerairesNode = ImporterUtils.findOrCreateNode(cruisePage.adaptTo(Node.class), ImportersConstants.ITINERARIES_NODE);
        ImporterUtils.saveSession(session, false);
        List<Itinerary> itinerairesCruise = apiCallService.getCruiseIteneraries(url, voyageId);
        if(itinerairesCruise != null && !itinerairesCruise.isEmpty()){

            LOGGER.debug("Cruise importer -- Start update iteniraries for voyage with id {}", voyageId);

            for (Itinerary itinerary : itinerairesCruise) {

                // Retrieve and create or update itinerary node
                Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH,
                        JcrConstants.NT_UNSTRUCTURED, "itineraryId", Objects.toString(itinerary.getItineraryId()),
                        resourceResolver);
                Node itineraryNode = ImporterUtils.adaptOrCreateNode(resources, itinerairesNode,
                        Objects.toString(itinerary.getItineraryId()));
                updateItineraryNode(itineraryNode, itinerary);

                // Retrieve and update or create land programs
                List<LandItinerary> landProgramList = apiCallService.getLandsProgram(itinerary);
                updateLandNodes(landProgramList, itineraryNode, itinerary);

                // Retrieve and update or create hotels
                List<HotelItinerary> hotels = apiCallService.getHotels(itinerary);
                updateHotelNodes(hotels, itineraryNode, itinerary);

                // Retrieve and update or create excursions
                List<ShorexItinerary> excursions = apiCallService.getExcursions(itinerary);
                updateExcursionsNode(excursions, itineraryNode, itinerary);
            }
            ImporterUtils.saveSession(session, false);

            LOGGER.debug("Cruise importer -- Updating iteniraries for voyage with id {} finished", voyageId);
        }
        else{
            LOGGER.warn("Cruise importer  -- List iteniraries is empty for cruise id {}", voyageId);
        }
    }

    public void replicateResource(String path) throws RepositoryException{
        try{
            if(path != null && !path.isEmpty()){
                LOGGER.debug("Cruise importer -- Replicate resource with path {}", path);
                replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
            }
        } catch (ReplicationException e) {
            LOGGER.error("Replication error",e);
        }
    }

    public void replicatePageWithChildren(Page page)throws RepositoryException{

        if(page != null && page.listChildren()!=null){
            replicateResource(page.getPath());
            page.listChildren().forEachRemaining(child -> {
                try {
                    replicateResource(child.getPath());
                } catch (RepositoryException e) {
                    LOGGER.error("Replication error",e);
                }
            });
        }
    }

    public void updateItineraryNode(Node itineraryNode, Itinerary itinerary) throws RepositoryException {
        itineraryNode.setProperty("portReference", ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "cityId",
                Objects.toString(itinerary.getCityId()), resourceResolver));
        itineraryNode.setProperty("voyageId", itinerary.getVoyageId());
        itineraryNode.setProperty("itineraryId", itinerary.getItineraryId());
        itineraryNode.setProperty("cityId", itinerary.getCityId());
        itineraryNode.setProperty("date", itinerary.getItineraryDate().toString());
        itineraryNode.setProperty("arriveTime", itinerary.getArriveTime());
        itineraryNode.setProperty("arriveAmPm", itinerary.getArriveTimeAmpm());
        itineraryNode.setProperty("departTime", itinerary.getDepartTime());
        itineraryNode.setProperty("departAmPm", itinerary.getDepartTimeAmpm());
        itineraryNode.setProperty("overnight", itinerary.getIsOvernight());
    }

    public void updateLandNodes(List<LandItinerary> landProgramList, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException {
        if (landProgramList != null && !landProgramList.isEmpty()) {
            LOGGER.debug("Cruise importer -- Start updating land programs for itenirary with id {}", itinerary.getItineraryId());
            Node landsNode = ImporterUtils.findOrCreateNode(itineraryNode, "land-programs");

            for (LandItinerary land : landProgramList) {
                if(land!=null){
                    Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_JCR_ROOT_PATH + itineraryNode.getParent().getPath(),
                            JcrConstants.NT_UNSTRUCTURED, "landItineraryId", Objects.toString(land.getLandItineraryId()),
                            resourceResolver);
                    String  landProgramReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH,
                            "landProgramId", Objects.toString(land.getLandItineraryId()), resourceResolver);
                    Node landNode = ImporterUtils.adaptOrCreateNode(resources, landsNode, Objects.toString(land.getLandItineraryId()));
                    landNode.setProperty("landProgramReference", landProgramReference);
                    landNode.setProperty("date", land.getDate().toString());
                    landNode.setProperty("cityId", land.getCityId());
                    landNode.setProperty("landItineraryId", land.getLandItineraryId());
                }

            }
            LOGGER.debug("Cruise importer -- Updating land programs for itenirary with id {} finished", itinerary.getItineraryId());
        } else {
            LOGGER.debug("Cruise importer -- No land program found for the itinerary {}", itinerary.getItineraryId());
        }
    }

    public void updateHotelNodes(List<HotelItinerary> hotels, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException {

        if (hotels != null && !hotels.isEmpty()) {
            LOGGER.debug("Cruise importer -- Start updating hotels for itenirary with id {}", itinerary.getItineraryId());
            Node HotelsNode = ImporterUtils.findOrCreateNode(itineraryNode, "hotels");

            for (HotelItinerary hotel : hotels) {
                if(hotel != null){
                    Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_JCR_ROOT_PATH +itineraryNode.getParent().getPath(),
                            JcrConstants.NT_UNSTRUCTURED, "hotelItineraryId", Objects.toString(hotel.getHotelItineraryId()),
                            resourceResolver);
                    Node hotelNode = ImporterUtils.adaptOrCreateNode(resources, HotelsNode,
                            Objects.toString(hotel.getHotelItineraryId()));
                    String hotelReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "hotelId",
                            Objects.toString(hotel.getHotelItineraryId()), resourceResolver);
                    hotelNode.setProperty("hotelReference", hotelReference);
                    hotelNode.setProperty("hotelItineraryId", hotel.getHotelItineraryId());
                    hotelNode.setProperty("cityId", hotel.getCityId());
                }
            }
            LOGGER.debug("Cruise importer -- Updating hotels for itenirary with id {} finished", itinerary.getItineraryId());
        } else {
            LOGGER.debug("Cruise importer -- No hotel found for the itinerary {}", itinerary.getItineraryId());
        }
    }

    public void updateExcursionsNode(List<ShorexItinerary> excursions, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException {
        if (excursions != null && !excursions.isEmpty()) {
            LOGGER.debug("Cruise importer -- Start updating excursions for itenirary with id {}", itinerary.getItineraryId());
            Node excursionsNode = ImporterUtils.findOrCreateNode(itineraryNode, "excursions");

            for (ShorexItinerary excursion : excursions) {
                if(excursion != null){
                    Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_JCR_ROOT_PATH + itineraryNode.getParent().getPath(),
                            JcrConstants.NT_UNSTRUCTURED, "shorexItineraryId",
                            Objects.toString(excursion.getShorexItineraryId()), resourceResolver);
                    Node excursionNode = ImporterUtils.adaptOrCreateNode(resources, excursionsNode,
                            Objects.toString(excursion.getShorexItineraryId()));
                    String excursionReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH,
                            "shorexId", Objects.toString(excursion.getShorexId()), resourceResolver);

                    excursionNode.setProperty("excursionReference", excursionReference);
                    excursionNode.setProperty("shorexItineraryId", excursion.getShorexItineraryId());
                    excursionNode.setProperty("cityId", excursion.getCityId());
                    excursionNode.setProperty("voyageId", excursion.getVoyageId());
                    excursionNode.setProperty("date", excursion.getDate().toString());
                    excursionNode.setProperty("plannedDepartureTime", excursion.getPlannedDepartureTime());
                    excursionNode.setProperty("generalDepartureTime", excursion.getGeneralDepartureTime());
                    excursionNode.setProperty("duration", excursion.getDuration());
                }
            }
            LOGGER.debug("Cruise importer -- Updating excursions for itenirary with id {} finished", itinerary.getItineraryId());
        } else {
            LOGGER.debug("Cruise importer -- No excursion found for the itinerary {}", itinerary.getItineraryId());
        }
    }

    public VoyagePriceComplete getVoyagePriceById(List<VoyagePriceComplete> voyagePrices, Integer voyageId) {
        VoyagePriceComplete voyagePriceComplete = null;
        if (voyagePrices != null && !voyagePrices.isEmpty()) {
            voyagePriceComplete = voyagePrices.stream()
                    .filter(item -> voyageId.equals(item.getVoyageId()))
                    .findAny()
                    .orElse(null);
        }
        return voyagePriceComplete;
    }

    public void buildOrUpdateSuiteNodes(LowestPrice lowestPrice,Page cruisePage,Integer voyageId,Integer shipId,List<VoyagePriceComplete> voyagePricesComplete)
            throws RepositoryException, IOException, ApiException {

        // Create or update suites nodes
        List<VoyagePriceComplete> voyagePrices = apiCallService.getVoyagePrices(voyagePricesComplete);
        VoyagePriceComplete voyagePrice = getVoyagePriceById(voyagePrices, voyageId);
        Node suitesNode = ImporterUtils.findOrCreateNode(cruisePage.adaptTo(Node.class), ImportersConstants.SUITES_NODE);
        ImporterUtils.saveSession(session, false);
        lowestPrice.initVariationPrices();
        if (voyagePrice != null && !voyagePrice.getMarketCurrency().isEmpty()) {
            LOGGER.debug("Cruise importer -- Start updating suites variations and prices for voyage with id {}", voyageId);
            for (Price price : voyagePrice.getMarketCurrency().get(0).getCruiseOnlyPrices()) {
                Page suiteReference = findSuiteReference(shipId, price.getSuiteCategoryCod());
                if (suiteReference != null) {
                    buildSuitesGrouping(lowestPrice,suitesNode, suiteReference, price, voyagePrice);
                }
            }
            //Build variation lowest prices
            buildVariationsLowestPrices(suitesNode, lowestPrice);
            LOGGER.debug("Cruise importer -- Updating suites variations and prices for voyage with id {} finished", voyageId);
        }
        else{
            LOGGER.debug("Cruise importer -- No price found for cruise with id {}", voyageId);
        }
    }

    public void buildSuitesGrouping(LowestPrice lowestPrice,Node rootNode, Page suiteRef, Price price, VoyagePriceComplete voyagePrice)
            throws RepositoryException {

        Node suiteGroupingNode = ImporterUtils.findOrCreateNode(rootNode, suiteRef.getName());
        if(suiteGroupingNode != null){
            lowestPrice.addVariation(suiteRef.getName());
            suiteGroupingNode.setProperty("suiteReference", suiteRef.getPath());

            Iterator<Resource> res = ImporterUtils.findResourceById(ImportersConstants.QUERY_JCR_ROOT_PATH + rootNode.getPath(), JcrConstants.NT_UNSTRUCTURED,
                    "suiteCategoryCod", price.getSuiteCategoryCod(), resourceResolver);
            Node suiteNode = ImporterUtils.adaptOrCreateNode(res, suiteGroupingNode, price.getSuiteCategoryCod());
            if(suiteNode != null){
                suiteNode.setProperty("suiteCategoryCod", price.getSuiteCategoryCod());
                ImporterUtils.saveSession(session, false);
                // Create variationNode
                buildOrUpdateVariationNodes(suiteRef.getName(),lowestPrice,voyagePrice.getMarketCurrency(), suiteNode, price.getSuiteCategoryCod(),
                        voyagePrice.getVoyageCod());
            }
        }
    }
    
    /**
     * Build suite for combo cruises
     * @param cruisePage
     * @param voyageId
     * @param shipId
     * @param voyagesPriceMarket
     * @throws RepositoryException
     * @throws IOException
     * @throws ApiException
     */
    public void buildOrUpdateSuiteNodes(LowestPrice lowestPrice,Page cruisePage,String voyageId,Integer shipId,List<VoyagePriceMarket>  voyagesPriceMarket)
            throws RepositoryException, IOException, ApiException {

        if (voyagesPriceMarket != null 
                && voyagesPriceMarket.get(0) !=null 
                && !voyagesPriceMarket.get(0).getCruiseOnlyPrices().isEmpty()) {

            lowestPrice.initVariationPrices();
            List<Price> prices = voyagesPriceMarket.get(0).getCruiseOnlyPrices();
            Node suitesNode = ImporterUtils.findOrCreateNode(cruisePage.adaptTo(Node.class), ImportersConstants.SUITES_NODE);
            ImporterUtils.saveSession(session, false);


            LOGGER.debug("Combo cruise -- Start updating suites variations and prices for voyage with id {}", voyageId);
            for (Price price : prices) {
                Page suiteReference = findSuiteReference(shipId, price.getSuiteCategoryCod());
                if (suiteReference != null) {
                    buildSuitesGrouping(lowestPrice,suitesNode, suiteReference, price, voyageId, voyagesPriceMarket);
                }
            }
            //Build variation lowest prices
            buildVariationsLowestPrices(suitesNode,lowestPrice);
            LOGGER.debug("Combo cruise importer -- Updating suites variations and prices for voyage with id {} finished", voyageId);
        }
        else{
            LOGGER.debug("Combo cruise -- No price found for cruise with id {}", voyageId);
        }
    }

    /**
     * Build suite groups for combo cruises
     * @param rootNode
     * @param suiteRef
     * @param price
     * @param voyageId
     * @param voyagesPriceMarket
     * @throws RepositoryException
     */
    public void buildSuitesGrouping(LowestPrice lowestPrice,Node rootNode, Page suiteRef, Price price,String voyageId, List<VoyagePriceMarket> voyagesPriceMarket)
            throws RepositoryException {

        Node suiteGroupingNode = ImporterUtils.findOrCreateNode(rootNode, suiteRef.getName());

        if(suiteGroupingNode != null){

            lowestPrice.addVariation(suiteRef.getName());
            suiteGroupingNode.setProperty("suiteReference", suiteRef.getPath());

            Iterator<Resource> res = ImporterUtils.findResourceById(ImportersConstants.QUERY_JCR_ROOT_PATH + rootNode.getPath(), JcrConstants.NT_UNSTRUCTURED,
                    "suiteCategoryCod", price.getSuiteCategoryCod(), resourceResolver);
            Node suiteNode = ImporterUtils.adaptOrCreateNode(res, suiteGroupingNode, price.getSuiteCategoryCod());
            if(suiteNode != null){
                suiteNode.setProperty("suiteCategoryCod", price.getSuiteCategoryCod());
                ImporterUtils.saveSession(session, false);
                // Create variationNode
               buildOrUpdateVariationNodes(suiteRef.getName(), lowestPrice,voyagesPriceMarket, suiteNode, price.getSuiteCategoryCod(),
                        voyageId);
            }
        }
    }

    public void buildOrUpdateVariationNodes(String suiteRef,LowestPrice lowestPrice,List<VoyagePriceMarket> voyagePriceMarketList, Node suiteNode,
            String suiteCategoryCode, String voyageCode) throws RepositoryException {
        if(voyagePriceMarketList != null && !voyagePriceMarketList.isEmpty()){
            for (VoyagePriceMarket voyagePriceMarket : voyagePriceMarketList) {

                Price price = voyagePriceMarket.getCruiseOnlyPrices().stream()
                        .filter(item -> suiteCategoryCode.equals(item.getSuiteCategoryCod()))
                        .findAny()
                        .orElse(null);

                if (price != null) {
                    String variationId = voyageCode + suiteCategoryCode + voyagePriceMarket.getMarketCod()
                    + voyagePriceMarket.getCurrencyCod();
                    Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH,
                            JcrConstants.NT_UNSTRUCTURED, "variationId", variationId, resourceResolver);
                    Node variationNode = ImporterUtils.adaptOrCreateNode(resources, suiteNode,
                            voyagePriceMarket.getMarketCod() + "_" + voyagePriceMarket.getCurrencyCod());
                    String[] geotaggingTags = { ImportersConstants.GEOTAGGING_TAG_PREFIX + voyagePriceMarket.getMarketCod().toLowerCase() };
                    if(variationNode != null){
                        variationNode.setProperty("price", price.getCruiseOnlyFare());
                        variationNode.setProperty("currency", price.getCurrencyCod());
                        variationNode.setProperty("availability", price.getSuiteAvailability());
                        // TODO Review tags
                        variationNode.setProperty(NameConstants.PN_TAGS, geotaggingTags);
                        variationNode.setProperty("variationId", variationId);
                        // Calculate the global lowest price
                        calculateLowestPrice(lowestPrice.getGlobalPrices(), price, voyagePriceMarket.getMarketCod());
                        // Calculation variation lowest price
                        calculateLowestPrice(lowestPrice.getVariationPrices().get(suiteRef).getVariationPrices(), price, voyagePriceMarket.getMarketCod());
                    }
                }
            }
        }
        else{
            LOGGER.debug("Cruise importer -- No price found for cruise with code {}", voyageCode);
        }
    }

    public Page findSuiteReference(Integer shipId, String suiteCategoryCode) throws RepositoryException {

        Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH, NameConstants.NT_PAGE,
                "shipId", Objects.toString(shipId), resourceResolver);
        if (resources != null && resources.hasNext()) {
            Resource resource = resources.next();
            if(resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)){
                Resource suitesParent = resource.getChild(ImportersConstants.SUITES_NODE);
                if (suitesParent != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(suitesParent)) {
                    Iterator<Page> suites = suitesParent.adaptTo(Page.class).listChildren();
                    while (suites != null && suites.hasNext()) {
                        Page suite = suites.next();
                        String[] categories = suite.getProperties().get("suiteCategoryCode", String[].class);
                        if (categories != null && Arrays.asList(categories).contains(suiteCategoryCode)) {
                            return suite;
                        }
                    }
                }
            }
        }
        LOGGER.error("Cruise importer -- Suite reference with suiteCategoryCode {} in the ship {} not found", suiteCategoryCode,shipId);
        return null;
    }

    public Page getDestination(Integer destinationId) {
        Page destinationPage = null;
        Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH, NameConstants.NT_PAGE,
                "destinationId", Objects.toString(destinationId), resourceResolver);
        if (resources != null && resources.hasNext()) {
            Resource resource = resources.next();
            if(resource!=null){
                destinationPage = pageManager.getPage(resource.getPath());
            }

        }
        return destinationPage;
    }

    public String[] findSpecialOffersReferences(List<VoyageSpecialOffer> voyageSpecialOffers, Integer voyageId) {

        List<String> references = new ArrayList<String>();

        if (voyageSpecialOffers != null && !voyageSpecialOffers.isEmpty()) {
            voyageSpecialOffers.get(0).getSpecialOffers()
            .stream()
            .filter(distinctByKey(e -> e.getVoyageSpecialOfferId()))
            .map(SpecialOfferByMarket::getVoyageSpecialOfferId)
            .collect(Collectors.toList()).forEach(id -> {
                references.add(ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "exclusiveOfferId",
                        Objects.toString(id), resourceResolver));
            });
        } else {
            LOGGER.debug("Cruise importer -- No special offer found for voyage id {}", voyageId);
        }

        return references.stream().toArray(String[]::new);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public String downloadAndSaveAsset(String path, String imageName) {
        String imagePath = null;
        if (path != null && !path.isEmpty() && imageName != null && !imageName.isEmpty()) {
            try {
                LOGGER.debug("Cruise importer -- Start download image with name {}", imageName);
                String fileDest = ImportersConstants.CRUISES_DAM_PATH + imageName.replaceAll("\\s","_");
                URL url = new URL(path);
                InputStream is = url.openStream();
                Asset asset = assetManager.createAsset(fileDest, is, "image/jpeg", true);
                imagePath = asset.getPath();
                //Replicate image
                replicateResource(imagePath);
                LOGGER.debug("Cruise importer -- Downloading image with name {} finished", imageName);
            } catch (Exception e) {
                LOGGER.error("Error while downloading cruise image", e);
            }
        }
        return imagePath;
    }

    public void calculateLowestPrice(Map<String, PriceData> lowestPrices, Price price, String marketCode) {
        LOGGER.debug("Cruise importer -- Start calculate lowest prices");
        if (price != null && !StringUtils.equals(ImportersConstants.PRICE_WAITLIST, Objects.toString(price.getCruiseOnlyFare()))) {
            String variation = marketCode + "_" + price.getCurrencyCod();
            if (Arrays.stream(PriceVariations.values()).anyMatch(e -> e.name().equals(variation))
                    && (!lowestPrices.containsKey(variation)
                            || (lowestPrices.get(variation) != null && 
                            price.getCruiseOnlyFare() < Integer.parseInt(lowestPrices.get(variation).getValue())))) {
                PriceData priceData = new PriceData();
                priceData.setCurrency(price.getCurrencyCod());
                priceData.setMarketCode(marketCode);
                priceData.setValue(Objects.toString(price.getCruiseOnlyFare()));
                lowestPrices.put(variation, priceData);
            }
        }
        LOGGER.debug("Cruise importer -- Finish price calculation");
    }

    public void buildLowestPrices(Node rootNode, Map<String, PriceData> prices) throws RepositoryException {
        Node node = ImporterUtils.findOrCreateNode(rootNode, ImportersConstants.LOWEST_PRICES_NODE);
        if (prices != null && !prices.isEmpty()) {
            prices.forEach((key, value) -> {
                try {
                    Node priceNode = ImporterUtils.findOrCreateNode(node, key);
                    priceNode.setProperty("price", value.getValue());
                    priceNode.setProperty("priceCurrencyCode", value.getCurrency());
                    priceNode.setProperty("priceMarketCode", value.getMarketCode());
                } catch (RepositoryException e) {
                    LOGGER.error("Cruise importer -- Exception while importing lowest prices", e);
                }

            });
        }

    }

    public void buildVariationsLowestPrices(Node suitesNode, LowestPrice lowestPrice) throws RepositoryException{
        if(suitesNode!=null){
            NodeIterator nodes = suitesNode.getNodes();
            while(nodes.hasNext()){
                Node node = nodes.nextNode();
                buildLowestPrices(node, lowestPrice.getVariation(node.getName()));
            }
        }
    }

    public void updateReplicationStatus(Boolean isDeleted, Boolean isVisible,Page page){
        try{
            if(page != null){
                if(isDeleted){
                    LOGGER.debug("Cruise importer -- Unpublish resource with path {}", page.getPath());
                    replicator.replicate(session, ReplicationActionType.DEACTIVATE, page.getPath());
                }
                else if(isVisible){
                    LOGGER.debug("Cruise importer -- Replicate resource with path {}", page.getPath());
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, page.getPath());
                }
            }
        } catch (ReplicationException e) {
            LOGGER.error("Replication error",e);
        }
    }  
    
    public List<Page> getPagesByResourceType(String resourceType) throws RepositoryException{
        List<Page> pages = null ;
        Map<String, String> map = new HashMap<String, String>();
        
            //Build query
            map.put("type", NameConstants.NT_PAGE);
            map.put("property", ImportersConstants.SLIN_RESOURCE_TYPE); 
            map.put("property.value", resourceType); 
     
            //Create builder
            Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
            //fetch pages
            SearchResult result = query.getResult();
            if(result != null && result.getHits() != null && !result.getHits().isEmpty()){
                pages = new ArrayList<Page>();
                for(Hit hit : result.getHits()){
                    if(hit != null && hit.getResource() != null){
                        Page page = hit.getResource().adaptTo(Page.class);
                        if(page!=null){
                            pages.add(page);
                        }       
                    }
                }
            }
        return pages;
    }
}
