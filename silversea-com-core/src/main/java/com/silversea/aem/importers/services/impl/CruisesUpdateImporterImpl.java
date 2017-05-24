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

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

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
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.TagManager.FindResults;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.enums.CruiseType;
import com.silversea.aem.enums.PriceVariations;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.services.CruisesUpdateImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.api.LandsApi;
import io.swagger.client.api.PricesApi;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.api.VoyageSpecialOffersApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.Itinerary;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.Price;
import io.swagger.client.model.ShorexItinerary;
import io.swagger.client.model.SpecialOfferByMarket;
import io.swagger.client.model.Voyage77;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyagePriceMarket;
import io.swagger.client.model.VoyageSpecialOffer;

@Service
@Component(label = "Silversea.com - Cruises importer")
public class CruisesUpdateImporterImpl extends BaseImporter implements CruisesUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesUpdateImporterImpl.class);
    private static final int PER_PAGE = 10;
    // Api urls
    private static final String VOYAGE_API_URL = "/api/v1/voyages";
    private static final String LAND_ADVENTURES_API_URL = "/api/v1/landAdventures/Itinerary";
    private static final String SPECIAL_OFFERS_API_URL = "/api/v1/voyageSpecialOffers";
    private static final String HOTEL_ITINERARY_API_URL = "/api/v1/hotels/Itinerary";
    private static final String SHORE_EXCURSIONS_API_URL = "/api/v1/shoreExcursions/Itinerary";
    private static final String PRICE_API_URL = "/api/v1/prices";
    // Templates
    private static final String CUISE_TEMPLATE = "/apps/silversea/silversea-com/templates/cruise";
    // Tags path
    private static final String GEOTAGGING_TAG_PREFIX = "geotagging:";
    // Constants
    private static final String PRICE_WAITLIST = "Waitlist:";

    private static final String DAM_PATH = "/content/dam/siversea-com/api-provided/";
    private static final String QUERY_JCR_ROOT_PATH = "/jcr:root";
    private static final String QUERY_CONTENT_PATH = "/jcr:root/content/silversea-com/en";
    private static final String QUERY_TAGS_PATH = "/jcr:root/etc/tags";

    private static final String ITINERARIES_NODE = "itineraries";
    private static final String SUITES_NODE = "suites";
    private static final String LOWEST_PRICES_NODE ="lowest-prices";
    private static final String CRUISES_DESTINATIONS_URL_KEY ="cruisesUrl";


    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    Replicator replicator;

    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    private TagManager tagManager;
    private AssetManager assetManager;
    private Session session;

    private List<VoyagePriceComplete> voyagePricesComplete;
    private Map<String, PriceData> lowestPrices;
    private Map<String, PriceData> variationLowestPrices;

    private void init() {
        try {
            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            pageManager = resourceResolver.adaptTo(PageManager.class);
            tagManager = resourceResolver.adaptTo(TagManager.class);
            assetManager = resourceResolver.adaptTo(AssetManager.class);
            session = resourceResolver.adaptTo(Session.class);
        } catch (LoginException e) {
            LOGGER.debug("Cruise importer login exception ", e);
        }
    }

    @Override
    public void loadData() throws IOException {
        try {
            voyagePricesComplete = new ArrayList<VoyagePriceComplete>();
            List<Voyage77> voyages;
            int i = 1;

            init();

            final String authorizationHeader = getAuthorizationHeader(VOYAGE_API_URL);
            VoyagesApi voyageApi = new VoyagesApi();
            voyageApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
            Page destinationPage = pageManager
                    .getPage(apiConfig.apiRootPath(CRUISES_DESTINATIONS_URL_KEY));
            String lastModificationDate = ImporterUtils.getLastModificationDate(destinationPage);
            LOGGER.debug("Cruise importer -- Start import data");
            do {
                voyages = voyageApi.voyagesGetChanges(lastModificationDate, i, PER_PAGE, null, null);
                processData(voyages); 
                i++;
            } while (!voyages.isEmpty());

            //Save date of last modification
            ImporterUtils.saveUpdateDate(destinationPage);
            ImporterUtils.saveSession(session, false);

            LOGGER.debug("Cruise importer -- Importing data finished");

        } catch (ApiException | WCMException | RepositoryException e) {
            LOGGER.error("Exception importing cruises", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
                resourceResolver = null;
            }
        }
    }

    private void processData(List<Voyage77> voyages) throws WCMException, RepositoryException, IOException, ApiException{
        if(voyages != null && !voyages.isEmpty()){
            for (Voyage77 voyage : voyages) {
                if(voyage != null){
                    LOGGER.debug("Cruise importer -- Start import cruise with id {}",voyage.getVoyageId());
                    // retrieve cruises root page dynamically
                    String destination = getDestination(voyage.getDestinationId());

                    if (destination != null) {
                        // Instantiate new hashMap which will contains
                        // lowest prices for the cruise
                        lowestPrices = new HashMap<String, PriceData>();

                        Page destinationPage = pageManager
                                .getPage(apiConfig.apiRootPath(CRUISES_DESTINATIONS_URL_KEY).concat(destination));

                        Page cruisePage = getCruisePage(destinationPage,voyage);

                        updateCruisePage(cruisePage, voyage);

                        // build itineraries nodes
                        clean(cruisePage,ITINERARIES_NODE);
                        buildOrUpdateIteneraries(cruisePage, voyage);
                        // Create or update suites nodes
                        clean(cruisePage,SUITES_NODE);
                        buildOrUpdateSuiteNodes(cruisePage, voyage);
                        // Create or update lowest prices
                        clean(cruisePage,LOWEST_PRICES_NODE);
                        buildLowestPrices(cruisePage.adaptTo(Node.class), lowestPrices);
                        //Persist data
                        ImporterUtils.saveSession(session, false);
                        //Replication management
                        updateReplicationStatus(voyage.getIsDeleted(), voyage.getIsVisible(), cruisePage);

                        LOGGER.debug("Cruise importer -- Import cruise with id {} finished",voyage.getVoyageId());
                    } else {
                        LOGGER.error("Cruise importer -- Error destination with id {} not found", voyage.getDestinationId());
                    }
                }
            }
        }
        else {
            LOGGER.error("Cruise importer -- List cruises is empty");
        }
    }

    private Page getCruisePage(Page destinationPage,Voyage77 voyage) throws WCMException, RepositoryException{

        // Retrieve and create or update cruise page
        Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_CONTENT_PATH,
                NameConstants.NT_PAGE, "cruiseId", Objects.toString(voyage.getVoyageId()),
                resourceResolver);
        Page cruisePage = ImporterUtils.adaptOrCreatePage(resources, CUISE_TEMPLATE, destinationPage,
                voyage.getVoyageName(), pageManager);

        return cruisePage;
    }

    private void updateCruisePage(Page cruisePage, Voyage77 voyage)
            throws RepositoryException, IOException, ApiException {

        List<VoyageSpecialOffer> voyageSpecialOffers = getVoyageSpecialOffers(SPECIAL_OFFERS_API_URL,
                voyage.getVoyageId());
        Node cruisePageContentNode = cruisePage.getContentResource().adaptTo(Node.class);
        cruisePageContentNode.setProperty(JcrConstants.JCR_TITLE, voyage.getVoyageName());

        setCruiseTags(voyage, cruisePage);
        // TODO voyageHighlights ?
        cruisePageContentNode.setProperty("voyageHighlights", "");
        cruisePageContentNode.setProperty("exclusiveOffers",
                findSpecialOffersReferences(voyageSpecialOffers, voyage.getVoyageId()));
        cruisePageContentNode.setProperty("startDate", voyage.getArriveDate().toString());
        cruisePageContentNode.setProperty("endDate", voyage.getDepartDate().toString());
        cruisePageContentNode.setProperty("duration", voyage.getDays());
        cruisePageContentNode.setProperty("shipReference", ImporterUtils.findReference(QUERY_CONTENT_PATH, "shipId",
                Objects.toString(voyage.getShipId()), resourceResolver));
        cruisePageContentNode.setProperty("cruiseCode", voyage.getVoyageCod());
        cruisePageContentNode.setProperty("cruiseId", voyage.getVoyageId());
        cruisePageContentNode.setProperty("itinerary",
                downloadAndSaveAsset(voyage.getMapUrl(), voyage.getVoyageName()));

    }

    private void clean(Page page,String nodeName) throws  RepositoryException{
        if(page != null){
            Resource resource = page.adaptTo(Resource.class);
            if(resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)){
                Resource child = resource.getChild(nodeName);
                if(child != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(child)){
                    LOGGER.debug("Cruise importer -- Remove node {}", page.getPath());
                    Node node = child.adaptTo(Node.class);
                    node.remove();
                    //Persist data
                    ImporterUtils.saveSession(session, false);
                }
            }
        }
    }
    private void setCruiseTags(Voyage77 voyage, Page page) throws RepositoryException {

        List<Tag> tags = new ArrayList<Tag>();
        if (voyage.getFeatures() != null && !voyage.getFeatures().isEmpty()) {
            voyage.getFeatures().forEach(item -> {
                Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_TAGS_PATH, "cq:Tag", "featureId",
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
            LOGGER.error("Cruise importer -- No feature found for cruise with id", voyage.getVoyageId());
        }

        CruiseType cruiseType = voyage.getIsExpedition() ? CruiseType.SILVERSEA_CTUISE
                : CruiseType.SILVERSEA_EXPEDITION_CTUISE;

        FindResults findResults = tagManager.findByTitle(cruiseType.getValue());
        if (findResults != null && !ArrayUtils.isEmpty(findResults.tags)) {
            tags.addAll(Arrays.asList(tagManager.findByTitle(cruiseType.getValue()).tags));
        }
        if (!tags.isEmpty() && page != null) {
            page.adaptTo(Node.class).addMixin("cq:Taggable");
            tagManager.setTags(page.getContentResource(), tags.stream().toArray(Tag[]::new));
        }
    }

    private void updateReplicationStatus(Boolean isDeleted, Boolean isVisible,Page page) throws RepositoryException{
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
    private void buildOrUpdateIteneraries(Page cruisePage, Voyage77 voyage)
            throws RepositoryException, IOException, ApiException {

        // Retrieve or create itineraries node
        Node itinerairesNode = ImporterUtils.findOrCreateNode(cruisePage.adaptTo(Node.class), ITINERARIES_NODE);
        ImporterUtils.saveSession(session, false);
        List<Itinerary> itinerairesCruise = getCruiseIteneraries(voyage.getItineraryUrl(), voyage.getVoyageId());
        if(itinerairesCruise != null && !itinerairesCruise.isEmpty()){

            LOGGER.debug("Cruise importer -- Start update iteniraries for voyage with id {}", voyage.getVoyageId());
            for (Itinerary itinerary : itinerairesCruise) {

                // Retrieve and create or update itinerary node
                Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_CONTENT_PATH,
                        JcrConstants.NT_UNSTRUCTURED, "itineraryId", Objects.toString(itinerary.getItineraryId()),
                        resourceResolver);
                Node itineraryNode = ImporterUtils.adaptOrCreateNode(resources, itinerairesNode,
                        Objects.toString(itinerary.getItineraryId()));
                updateItineraryNode(itineraryNode, itinerary);

                // Retrieve and update or create land programs
                List<LandItinerary> landProgramList = getLandsProgram(LAND_ADVENTURES_API_URL, itinerary);
                updateLandNodes(landProgramList, itineraryNode, itinerary);

                // Retrieve and update or create hotels
                List<HotelItinerary> hotels = getHotels(HOTEL_ITINERARY_API_URL, itinerary);
                updateHotelNodes(hotels, itineraryNode, itinerary);

                // Retrieve and update or create excursions
                List<ShorexItinerary> excursions = getExcursions(SHORE_EXCURSIONS_API_URL, itinerary);
                updateExcursionsNode(excursions, itineraryNode, itinerary);
            }
            ImporterUtils.saveSession(session, false);

            LOGGER.debug("Cruise importer -- Updating iteniraries for voyage with id {} finished", voyage.getVoyageId());
        }
        else{
            LOGGER.error("Cruise importer  -- List iteniraries is empty for cruise id {}", voyage.getVoyageId());
        }
    }

    private List<Itinerary> getCruiseIteneraries(String apiUrl, Integer voyageId) throws IOException, ApiException {
        List<Itinerary> itineraireCruise = null;

        final String authorizationHeader = getAuthorizationHeader("/api" + StringUtils.substringAfter(apiUrl, "/api"));
        VoyagesApi voyageItiniraryApi = new VoyagesApi();
        voyageItiniraryApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        itineraireCruise = voyageItiniraryApi.voyagesGetItinerary(voyageId, null, null);
        return itineraireCruise;
    }

    private List<LandItinerary> getLandsProgram(String apiUrl, Itinerary itinerary) throws IOException, ApiException {

        LandsApi landProgramApi = new LandsApi();
        final String authorizationlandProgramHeader = getAuthorizationHeader(apiUrl);
        landProgramApi.getApiClient().addDefaultHeader("Authorization", authorizationlandProgramHeader);
        List<LandItinerary> landsProgram = landProgramApi.landsGetItinerary(itinerary.getCityId(),
                itinerary.getVoyageId(), null, null, null, null);

        return landsProgram;
    }

    private void updateItineraryNode(Node itineraryNode, Itinerary itinerary) throws RepositoryException {
        itineraryNode.setProperty("portReference", ImporterUtils.findReference(QUERY_CONTENT_PATH, "cityId",
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

    private void updateLandNodes(List<LandItinerary> landProgramList, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException {
        if (landProgramList != null && !landProgramList.isEmpty()) {
            LOGGER.debug("Cruise importer -- Start updating land programs for itenirary with id {}", itinerary.getItineraryId());
            Node landsNode = ImporterUtils.findOrCreateNode(itineraryNode, "land-programs");

            for (LandItinerary land : landProgramList) {
                if(land!=null){
                    Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_JCR_ROOT_PATH + itineraryNode.getParent().getPath(),
                            JcrConstants.NT_UNSTRUCTURED, "landItineraryId", Objects.toString(land.getLandItineraryId()),
                            resourceResolver);
                    Node landNode = ImporterUtils.adaptOrCreateNode(resources, landsNode,
                            Objects.toString(land.getLandItineraryId()));
                    landNode.setProperty("landProgramReference", ImporterUtils.findReference(QUERY_CONTENT_PATH,
                            "landProgramId", Objects.toString(land.getLandItineraryId()), resourceResolver));
                    landNode.setProperty("date", land.getDate().toString());
                    landNode.setProperty("cityId", land.getCityId());
                    landNode.setProperty("landItineraryId", land.getLandItineraryId());
                }

            }
            LOGGER.debug("Cruise importer -- Updating land programs for itenirary with id {} finished", itinerary.getItineraryId());
        } else {
            LOGGER.error("No land program found for the itinerary {}", itinerary.getItineraryId());
        }
    }

    private List<HotelItinerary> getHotels(String apiUrl, Itinerary itinerary) throws IOException, ApiException {

        List<HotelItinerary> hotels;
        final String authorizationHotelHeader = getAuthorizationHeader(apiUrl);
        HotelsApi hotelsApi = new HotelsApi();
        hotelsApi.getApiClient().addDefaultHeader("Authorization", authorizationHotelHeader);
        hotels = hotelsApi.hotelsGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(), null, null, null, null);

        return hotels;
    }

    private void updateHotelNodes(List<HotelItinerary> hotels, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException {

        if (hotels != null && !hotels.isEmpty()) {
            LOGGER.debug("Cruise importer -- Start updating hotels for itenirary with id {}", itinerary.getItineraryId());
            Node HotelsNode = ImporterUtils.findOrCreateNode(itineraryNode, "hotels");

            for (HotelItinerary hotel : hotels) {
                if(hotel != null){
                    Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_JCR_ROOT_PATH + itineraryNode.getParent().getPath(),
                            JcrConstants.NT_UNSTRUCTURED, "hotelItineraryId", Objects.toString(hotel.getHotelItineraryId()),
                            resourceResolver);
                    Node hotelNode = ImporterUtils.adaptOrCreateNode(resources, HotelsNode,
                            Objects.toString(hotel.getHotelItineraryId()));

                    hotelNode.setProperty("hotelReference", ImporterUtils.findReference(QUERY_CONTENT_PATH, "hotelId",
                            Objects.toString(hotel.getHotelItineraryId()), resourceResolver));
                    hotelNode.setProperty("hotelItineraryId", hotel.getHotelItineraryId());
                    hotelNode.setProperty("cityId", hotel.getCityId());
                    // TODO : availableFrom
                    // TODO : availableTo
                }
            }
            LOGGER.debug("Cruise importer -- Updating hotels for itenirary with id {} finished", itinerary.getItineraryId());
        } else {
            LOGGER.error("Cruise importer -- No hotel found for the itinerary {}", itinerary.getItineraryId());
        }
    }

    private List<ShorexItinerary> getExcursions(String apiUrl, Itinerary itinerary) throws IOException, ApiException {

        List<ShorexItinerary> shorex;
        final String authorizationShorexHeader = getAuthorizationHeader(apiUrl);
        ShorexesApi shorexApi = new ShorexesApi();
        shorexApi.getApiClient().addDefaultHeader("Authorization", authorizationShorexHeader);
        shorex = shorexApi.shorexesGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(), null, null, null, null);

        return shorex;
    }

    private void updateExcursionsNode(List<ShorexItinerary> excursions, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException {
        if (excursions != null && !excursions.isEmpty()) {
            LOGGER.debug("Cruise importer -- Start updating excursions for itenirary with id {}", itinerary.getItineraryId());
            Node excursionsNode = ImporterUtils.findOrCreateNode(itineraryNode, "excursions");

            for (ShorexItinerary excursion : excursions) {
                if(excursion != null){
                    Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_JCR_ROOT_PATH + itineraryNode.getParent().getPath(),
                            JcrConstants.NT_UNSTRUCTURED, "shorexItineraryId",
                            Objects.toString(excursion.getShorexItineraryId()), resourceResolver);
                    Node excursionNode = ImporterUtils.adaptOrCreateNode(resources, excursionsNode,
                            Objects.toString(excursion.getShorexItineraryId()));

                    excursionNode.setProperty("excursionReference", ImporterUtils.findReference(QUERY_CONTENT_PATH,
                            "shorexId", Objects.toString(excursion.getShorexId()), resourceResolver));
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
            LOGGER.error("Cruise importer -- No excursion found for the itinerary {}", itinerary.getItineraryId());
        }
    }

    private List<VoyagePriceComplete> getVoyagePrices(String apiUrl) throws IOException, ApiException {
        if (voyagePricesComplete.isEmpty()) {
            List<VoyagePriceComplete> result = new ArrayList<VoyagePriceComplete>();
            final String authorizationHeader = getAuthorizationHeader(apiUrl);
            PricesApi pricesApi = new PricesApi();
            pricesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
            int i = 1;
            do {
                result = pricesApi.pricesGet3(i, PER_PAGE, null);
                voyagePricesComplete.addAll(result);
                i++;
            } while (!result.isEmpty());

        }
        return voyagePricesComplete;
    }

    private VoyagePriceComplete getVoyagePriceById(List<VoyagePriceComplete> voyagePrices, Integer voyageId) {
        VoyagePriceComplete voyagePriceComplete = null;
        if (voyagePrices != null && !voyagePrices.isEmpty()) {

            voyagePriceComplete = voyagePrices.stream().filter(item -> voyageId.equals(item.getVoyageId())).findAny()
                    .orElse(null);
        }
        return voyagePriceComplete;
    }

    private void buildOrUpdateSuiteNodes(Page cruisePage, Voyage77 voyage)
            throws RepositoryException, IOException, ApiException {

        // Create or update suites nodes
        List<VoyagePriceComplete> voyagePrices = getVoyagePrices(PRICE_API_URL);
        VoyagePriceComplete voyagePrice = getVoyagePriceById(voyagePrices, voyage.getVoyageId());
        Node suitesNode = ImporterUtils.findOrCreateNode(cruisePage.adaptTo(Node.class), SUITES_NODE);
        ImporterUtils.saveSession(session, false);

        if (voyagePrice != null && !voyagePrice.getMarketCurrency().isEmpty()) {
            LOGGER.debug("Cruise importer -- Start updating suites variations and prices for voyage with id {}", voyage.getVoyageId());
            for (Price price : voyagePrice.getMarketCurrency().get(0).getCruiseOnlyPrices()) {
                Page suiteReference = findSuiteReference(voyage.getShipId(), price.getSuiteCategoryCod());
                if (suiteReference != null) {
                    buildSuitesGrouping(suitesNode, suiteReference, price, voyagePrice);
                }
            }
            LOGGER.debug("Cruise importer -- Updating suites variations and prices for voyage with id {} finished", voyage.getVoyageId());
        }
        else{
            LOGGER.error("Cruise importer -- No price found for cruise with id {}", voyage.getVoyageId());
        }
    }

    private void buildSuitesGrouping(Node rootNode, Page suiteRef, Price price, VoyagePriceComplete voyagePrice)
            throws RepositoryException {
        variationLowestPrices = new HashMap<String, PriceData>();
        Node suiteGroupingNode = ImporterUtils.findOrCreateNode(rootNode, suiteRef.getName());
        if(suiteGroupingNode != null){
            suiteGroupingNode.setProperty("suiteReference", suiteRef.getPath());

            Iterator<Resource> res = ImporterUtils.findResourceById(QUERY_JCR_ROOT_PATH + rootNode.getPath(), JcrConstants.NT_UNSTRUCTURED,
                    "suiteCategoryCod", price.getSuiteCategoryCod(), resourceResolver);
            Node suiteNode = ImporterUtils.adaptOrCreateNode(res, suiteGroupingNode, price.getSuiteCategoryCod());
            if(suiteNode != null){
                suiteNode.setProperty("suiteCategoryCod", price.getSuiteCategoryCod());
                ImporterUtils.saveSession(session, false);
                // Create variationNode
                buildOrUpdateVariationNodes(voyagePrice.getMarketCurrency(), suiteNode, price.getSuiteCategoryCod(),
                        voyagePrice.getVoyageCod());
                // build variations lowest prices node
                buildLowestPrices(suiteGroupingNode, variationLowestPrices);
                ImporterUtils.saveSession(session, false);
            }
        }

    }

    private void buildOrUpdateVariationNodes(List<VoyagePriceMarket> voyagePriceMarketList, Node suiteNode,
            String suiteCategoryCode, String voyageCode) throws RepositoryException {
        if(voyagePriceMarketList != null && !voyagePriceMarketList.isEmpty()){
            for (VoyagePriceMarket voyagePriceMarket : voyagePriceMarketList) {

                Price price = voyagePriceMarket.getCruiseOnlyPrices().stream()
                        .filter(item -> suiteCategoryCode.equals(item.getSuiteCategoryCod())).findAny().orElse(null);

                if (price != null) {
                    String variationId = voyageCode + suiteCategoryCode + voyagePriceMarket.getMarketCod()
                    + voyagePriceMarket.getCurrencyCod();
                    Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_CONTENT_PATH,
                            JcrConstants.NT_UNSTRUCTURED, "variationId", variationId, resourceResolver);
                    Node variationNode = ImporterUtils.adaptOrCreateNode(resources, suiteNode,
                            voyagePriceMarket.getMarketCod() + "_" + voyagePriceMarket.getCurrencyCod());
                    String[] geotaggingTags = { GEOTAGGING_TAG_PREFIX + voyagePriceMarket.getMarketCod().toLowerCase() };
                    if(variationNode != null){
                        variationNode.setProperty("price", price.getCruiseOnlyFare());
                        variationNode.setProperty("currency", price.getCurrencyCod());
                        variationNode.setProperty("availability", price.getSuiteAvailability());
                        // TODO Review tags
                        variationNode.setProperty(NameConstants.PN_TAGS, geotaggingTags);
                        variationNode.setProperty("variationId", variationId);
                        // Calculate the global lowest price
                        calculateLowestPrice(lowestPrices, price, voyagePriceMarket.getMarketCod());
                        // Calculation variation lowest price
                        calculateLowestPrice(variationLowestPrices, price, voyagePriceMarket.getMarketCod());
                    }
                }
            }
        }
        else{
            LOGGER.error("Cruise importer -- No price found for cruise with code {}", voyageCode);
        }
    }

    private Page findSuiteReference(Integer shipId, String suiteCategoryCode) throws RepositoryException {

        Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_CONTENT_PATH, NameConstants.NT_PAGE,
                "shipId", Objects.toString(shipId), resourceResolver);
        if (resources != null && resources.hasNext()) {
            Resource resource = resources.next();
            if(resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)){
                Resource suitesParent = resource.getChild(SUITES_NODE);
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
        LOGGER.error("Cruise importer -- Suite reference with suiteCategoryCode {} not found", suiteCategoryCode);
        return null;
    }

    private String getDestination(Integer destinationId) {
        String destination = null;
        Iterator<Resource> resources = ImporterUtils.findResourceById(QUERY_CONTENT_PATH, NameConstants.NT_PAGE,
                "destinationId", Objects.toString(destinationId), resourceResolver);
        if (resources.hasNext()) {
            destination = StringUtils.substringAfterLast(resources.next().getPath(), "/");
        }
        return destination;
    }

    private List<VoyageSpecialOffer> getVoyageSpecialOffers(String apiUrl, Integer voyageId)
            throws IOException, ApiException {

        final String authorizationHeader = getAuthorizationHeader(apiUrl);

        List<VoyageSpecialOffer> voyageSpecialOffers;
        VoyageSpecialOffersApi voyageSpecialOffersApi = new VoyageSpecialOffersApi();
        voyageSpecialOffersApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        voyageSpecialOffers = voyageSpecialOffersApi.voyageSpecialOffersGet(null, voyageId, null, null, null);

        return voyageSpecialOffers;

    }

    private String[] findSpecialOffersReferences(List<VoyageSpecialOffer> voyageSpecialOffers, Integer voyageId) {

        List<String> references = new ArrayList<String>();

        if (voyageSpecialOffers != null && !voyageSpecialOffers.isEmpty()) {
            voyageSpecialOffers.get(0).getSpecialOffers().stream().filter(distinctByKey(e -> e.getVoyageSpecialOfferId())).distinct()
            .map(SpecialOfferByMarket::getVoyageSpecialOfferId).collect(Collectors.toList()).forEach(id -> {
                references.add(ImporterUtils.findReference(QUERY_CONTENT_PATH, "exclusiveOfferId",
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

    private String downloadAndSaveAsset(String path, String imageName) {
        String imagePath = null;
        if (path != null && !path.isEmpty() && imageName != null && !imageName.isEmpty()) {
            try {
                LOGGER.debug("Cruise importer -- Start download image with name {}", imageName);
                String fileDest = DAM_PATH + imageName.replaceAll("\\s","_");
                URL url = new URL(path);
                InputStream is = url.openStream();
                Asset asset = assetManager.createAsset(fileDest, is, "image/jpeg", true);
                imagePath = asset.getPath();
                LOGGER.debug("Cruise importer -- Downloading image with name {} finished", imageName);
            } catch (Exception e) {
                LOGGER.error("Error while downloading cruise image", e);
            }
        }
        return imagePath;
    }

    // TODO:Store WAITLIST if prices not exits
    private void calculateLowestPrice(Map<String, PriceData> lowestPrices, Price price, String marketCode) {
        if (price != null && !StringUtils.equals(PRICE_WAITLIST, Objects.toString(price.getCruiseOnlyFare()))) {
            LOGGER.debug("Cruise importer -- Start calculate lowest prices");
            String variation = marketCode + "_" + price.getCurrencyCod();
            if (Arrays.stream(PriceVariations.values()).anyMatch(e -> e.name().equals(variation))
                    && (!lowestPrices.containsKey(variation)
                            || price.getCruiseOnlyFare() < Integer.parseInt(lowestPrices.get(variation).getValue()))) {
                PriceData priceData = new PriceData();
                priceData.setCurrency(price.getCurrencyCod());
                priceData.setMarketCode(marketCode);
                priceData.setValue(Objects.toString(price.getCruiseOnlyFare()));
                lowestPrices.put(variation, priceData);
                LOGGER.debug("Cruise importer -- Price calculation finished");
            }
        }
    }

    private void buildLowestPrices(Node rootNode, Map<String, PriceData> prices) throws RepositoryException {
        Node node = ImporterUtils.findOrCreateNode(rootNode, LOWEST_PRICES_NODE);
        if (prices != null && !prices.isEmpty()) {
            prices.forEach((key, value) -> {
                try {
                    Node priceNode = ImporterUtils.findOrCreateNode(node, key);
                    priceNode.setProperty("price", value.getValue());
                    priceNode.setProperty("priceCurrencyCode", value.getCurrency());
                    priceNode.setProperty("priceMarketCode", value.getMarketCode());
                } catch (RepositoryException e) {
                    LOGGER.error("Exception while importing lowest prices", e);
                }

            });
        }

    }
}
