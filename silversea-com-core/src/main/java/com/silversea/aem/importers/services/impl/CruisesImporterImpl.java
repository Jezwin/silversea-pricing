package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesImporter;

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
import io.swagger.client.model.Voyage;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyagePriceMarket;
import io.swagger.client.model.VoyageSpecialOffer;


@Service
@Component(label = "Silversea.com - Cruises importer")
public class CruisesImporterImpl extends BaseImporter implements CruisesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(CruisesImporterImpl.class);
	private static final int PER_PAGE = 10;
	private static final String VOYAGE_API_URL="/api/v1/voyages";
	private static final String LAND_ADVENTURES_API_URL ="/api/v1/landAdventures/Itinerary";
	private static final String CUISE_TEMPLATE = "/apps/silversea/silversea-com/templates/cruise";
	private static final String SPECIAL_OFFERS_API_URL = "/api/v1/voyageSpecialOffers";
	private static final String HOTEL_ITINERARY_API_URL = "/api/v1/hotels/Itinerary";
	private static final String SHORE_EXCURSIONS_API_URL ="/api/v1/shoreExcursions/Itinerary";
	private static final String PRICE_API_URL = "/api/v1/prices";
	private static final String GEOTAGGING_TAG_PREFIX = "geotagging:";
	private static final String PRICE_WAITLIST = "Waitlist:";
	
	enum PriceVariations {
        EU_EUR,UK_GBP,AS_AUD,FT_USD;  
    }

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	private ResourceResolver resourceResolver ;
	private PageManager pageManager;
	private Session session;
	//TODO: to change
	private List<VoyagePriceComplete> voyagePricesComplete;
	private Map<String,Integer> lowestPrices;
	
	private void init() {
		try {
			resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			pageManager = resourceResolver.adaptTo(PageManager.class);
			session = resourceResolver.adaptTo(Session.class);
		} catch (LoginException e) {
			LOGGER.debug("Cruise importer login exception ", e);
		} 
	}

	@Override
	public void importCruises() throws IOException {
		try {
			
			init();
			
			final String authorizationHeader = getAuthorizationHeader(VOYAGE_API_URL);
			VoyagesApi voyageApi = new VoyagesApi();
			voyageApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
			voyagePricesComplete = new ArrayList<VoyagePriceComplete>();
	
			List<Voyage>  voyages;
			int i = 1;
			int j = 0;

			do {

				voyages = voyageApi.voyagesGet(null, null, null, null, null, i, PER_PAGE, null, null);

				for (Voyage voyage : voyages) {
  
				    //Instantiate new hashMap which will contains 
				    //lowest prices for the cruise
				    lowestPrices = new HashMap<String,Integer>();
					//TODO retrieve cruises root page dynamically
					//String destinationPath = getDestinationPath(voyage.getVoyageId());
					Page cruisesRootPage = pageManager.getPage(ImportersConstants.BASEPATH_CRUISES);

					//Retrieve and create or update cruise page
					Iterator<Resource> resources = ImporterUtils.findResourceById(NameConstants.NT_PAGE,"cruiseId",Objects.toString(voyage.getVoyageId()),resourceResolver);
					Page cruisePage = ImporterUtils.adaptOrCreatePage(resources,CUISE_TEMPLATE,cruisesRootPage,voyage.getVoyageName(),pageManager);
					updateCruisePage(cruisePage,voyage);

					//build itineraries nodes
					buildOrUpdateIteneraries(cruisePage,voyage);
					
					//Create or update suites nodes
					buildOrUpdateSuiteNodes(cruisePage,voyage);
					
					//Create or update lowest prices
					buildLowestPrices(cruisePage,lowestPrices);

					j++;

					ImporterUtils.saveSession(session,false);
				}

				i++;
			} while (!voyages.isEmpty());

			ImporterUtils.saveSession(session,false);

		} catch (ApiException | WCMException   | RepositoryException e) {
			LOGGER.error("Exception importing cruises", e);
		}
		finally{
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
				resourceResolver = null;
			}
		}
	}

    private void buildOrUpdateIteneraries(Page cruisePage,Voyage voyage) throws RepositoryException, IOException, ApiException{
    	
    	//Retrieve or create itineraries node
		Node itinerairesNode = ImporterUtils.findOrCreateNode(cruisePage.adaptTo(Node.class),"itineraries");
		ImporterUtils.saveSession(session,false);
		List<Itinerary> itinerairesCruise = getCruiseIteneraries(voyage.getItineraryUrl(),voyage.getVoyageId());


		for (Itinerary itinerary : itinerairesCruise) {

			//Retrieve and create or update itinerary node
			Iterator<Resource> resources = ImporterUtils.findResourceById(NameConstants.NT_PAGE, "itineraryId",Objects.toString(itinerary.getItineraryId()),resourceResolver);
			Node itineraryNode = ImporterUtils.adaptOrCreateNode(resources,itinerairesNode,Objects.toString(itinerary.getItineraryId()));
			updateItineraryNode(itineraryNode,itinerary);


			//Retrieve and update or create land programs
			List<LandItinerary> landProgramList = getLandsProgram(LAND_ADVENTURES_API_URL,itinerary);
			updateLandNodes(landProgramList,itineraryNode);

			//Retrieve and update or create hotels
			List<HotelItinerary> hotels =getHotels(HOTEL_ITINERARY_API_URL,itinerary);
			updateHotelNodes(hotels,itineraryNode);

			//Retrieve and update or create excursions
			List<ShorexItinerary> excursions = getExcursions(SHORE_EXCURSIONS_API_URL,itinerary);
			updateExcursionsNode(excursions,itinerairesNode);

		}
		ImporterUtils.saveSession(session,false);
    }
	

	private void updateCruisePage(Page cruisePage, Voyage voyage) throws RepositoryException, IOException, ApiException{
		
		List<VoyageSpecialOffer> voyageSpecialOffers = getVoyageSpecialOffers(SPECIAL_OFFERS_API_URL,voyage.getVoyageId());
		Node cruisePageContentNode = cruisePage.getContentResource().adaptTo(Node.class);

		cruisePageContentNode.setProperty(JcrConstants.JCR_TITLE, voyage.getVoyageName());
		// TODO  voyageHighlights ?
		cruisePageContentNode.setProperty("voyageHighlights","");
		cruisePageContentNode.setProperty("exclusiveOffers",findSpecialOffersReferences(voyageSpecialOffers));
		cruisePageContentNode.setProperty("startDate", voyage.getArriveDate().toString());
		cruisePageContentNode.setProperty("endDate", voyage.getDepartDate().toString());
		cruisePageContentNode.setProperty("duration", voyage.getDays());
		cruisePageContentNode.setProperty("shipReference", ImporterUtils.findReference("shipId",Objects.toString(voyage.getShipId()),resourceResolver));
		cruisePageContentNode.setProperty("cruiseCode", voyage.getVoyageCod());
		cruisePageContentNode.setProperty("cruiseId", voyage.getVoyageId());
		cruisePageContentNode.setProperty("itinerary", voyage.getMapUrl());
	}

	private List<Itinerary> getCruiseIteneraries(String apiUrl,Integer voyageId) throws IOException,ApiException{
		List<Itinerary> itineraireCruise = null;

		final String authorizationHeader = getAuthorizationHeader("/api"+StringUtils.substringAfter(apiUrl, "/api"));
		VoyagesApi voyageItiniraryApi = new VoyagesApi();
		voyageItiniraryApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
		itineraireCruise = voyageItiniraryApi.voyagesGetItinerary(voyageId, null, null);
		return itineraireCruise;
	}

	private List<LandItinerary> getLandsProgram(String apiUrl,Itinerary itinerary)throws IOException,ApiException{

		LandsApi landProgramApi = new LandsApi();
		final String authorizationlandProgramHeader = getAuthorizationHeader(apiUrl);
		landProgramApi.getApiClient().addDefaultHeader("Authorization", authorizationlandProgramHeader);
		List<LandItinerary> landsProgram = landProgramApi.landsGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(),
				null, null, null, null);

		return landsProgram;
	}

	private void updateItineraryNode(Node itineraryNode ,Itinerary itinerary)throws RepositoryException{
		itineraryNode.setProperty("portReference", ImporterUtils.findReference("cityId", Objects.toString(itinerary.getCityId()),resourceResolver));
		itineraryNode.setProperty("voyageId", itinerary.getVoyageId());
		itineraryNode.setProperty("cityId", itinerary.getCityId());
		itineraryNode.setProperty("date", itinerary.getItineraryDate().toString());
		itineraryNode.setProperty("arriveTime", itinerary.getArriveTime());
		itineraryNode.setProperty("arriveAmPm", itinerary.getArriveTimeAmpm());
		itineraryNode.setProperty("departTime", itinerary.getDepartTime());
		itineraryNode.setProperty("departAmPm", itinerary.getDepartTimeAmpm());
		itineraryNode.setProperty("overnight", itinerary.getIsOvernight());
	}

	private void updateLandNodes(List<LandItinerary> landProgramList,Node itineraryNode)throws RepositoryException{
		if (landProgramList != null && !landProgramList.isEmpty()) {


			Node landsNode = ImporterUtils.findOrCreateNode(itineraryNode,"land-programs");

			for (LandItinerary land : landProgramList) {

				Iterator<Resource> resources = ImporterUtils.findResourceById(JcrConstants.NT_UNSTRUCTURED, "landItineraryId",Objects.toString(land.getLandItineraryId()),resourceResolver);
				Node landNode = ImporterUtils.adaptOrCreateNode(resources,landsNode, Objects.toString(land.getLandItineraryId()));
				landNode.setProperty("landProgramReference", ImporterUtils.findReference("landProgramId", Objects.toString(land.getLandItineraryId()),resourceResolver));
				landNode.setProperty("date", land.getDate().toString());
				landNode.setProperty("cityId", land.getCityId());
				landNode.setProperty("landItineraryId", land.getLandItineraryId());

			}
		}
	}

	private List<HotelItinerary> getHotels(String apiUrl,Itinerary itinerary)  throws IOException,ApiException{

		List<HotelItinerary> hotels;
		final String authorizationHotelHeader = getAuthorizationHeader(apiUrl);
		HotelsApi hotelsApi = new HotelsApi();
		hotelsApi.getApiClient().addDefaultHeader("Authorization", authorizationHotelHeader);
		hotels = hotelsApi.hotelsGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(), null, null, null, null);

		return hotels;
	}

	private void updateHotelNodes(List<HotelItinerary> hotels,Node itineraryNode)throws RepositoryException{


		if (hotels!=null && !hotels.isEmpty()) {

			Node HotelsNode = ImporterUtils.findOrCreateNode(itineraryNode,"hotels");

			for (HotelItinerary hotel : hotels) {

				Iterator<Resource> resources = ImporterUtils.findResourceById(JcrConstants.NT_UNSTRUCTURED, "hotelItineraryId",Objects.toString(hotel.getHotelItineraryId()),resourceResolver);
				Node hotelNode = ImporterUtils.adaptOrCreateNode(resources,HotelsNode,Objects.toString(hotel.getHotelItineraryId()));

				hotelNode.setProperty("hotelReference", ImporterUtils.findReference("hotelId", Objects.toString(hotel.getHotelItineraryId()),resourceResolver));
				hotelNode.setProperty("hotelItineraryId", hotel.getHotelItineraryId());
				hotelNode.setProperty("cityId", hotel.getCityId());
				//TODO : availableFrom
				//TODO : availableTo
			}
		}
	}

	private List<ShorexItinerary> getExcursions(String apiUrl,Itinerary itinerary)throws IOException,ApiException{

		List<ShorexItinerary> shorex;
		final String authorizationShorexHeader = getAuthorizationHeader(apiUrl);
		ShorexesApi shorexApi = new ShorexesApi();
		shorexApi.getApiClient().addDefaultHeader("Authorization", authorizationShorexHeader);
		shorex = shorexApi.shorexesGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(), null,
				null, null, null);

		return shorex;
	}

	private void updateExcursionsNode(List<ShorexItinerary> excursions,Node itineraryNode)throws RepositoryException{
		if (excursions != null && !excursions.isEmpty()) {

			Node excursionsNode = ImporterUtils.findOrCreateNode(itineraryNode,"excursions");

			for (ShorexItinerary excursion : excursions) {

				Iterator<Resource> resources = ImporterUtils.findResourceById(JcrConstants.NT_UNSTRUCTURED, "shorexItineraryId",Objects.toString(excursion.getShorexItineraryId()),resourceResolver);
				Node excursionNode = ImporterUtils.adaptOrCreateNode(resources,excursionsNode,Objects.toString(excursion.getShorexItineraryId()));

				excursionNode.setProperty("excursionReference", ImporterUtils.findReference("shorexId", Objects.toString(excursion.getShorexId()),resourceResolver));
				excursionNode.setProperty("shorexItineraryId", excursion.getShorexItineraryId());
				excursionNode.setProperty("cityId", excursion.getCityId());
				excursionNode.setProperty("voyageId", excursion.getVoyageId());
				excursionNode.setProperty("date", excursion.getDate().toString());
				excursionNode.setProperty("plannedDepartureTime", excursion.getPlannedDepartureTime());
				excursionNode.setProperty("generalDepartureTime", excursion.getGeneralDepartureTime());
				excursionNode.setProperty("duration", excursion.getDuration());

			}
		}

	}

	private List<VoyagePriceComplete> getVoyagePrices(String apiUrl)throws IOException,ApiException{
		if(voyagePricesComplete.isEmpty()){
			List<VoyagePriceComplete> result = new ArrayList<VoyagePriceComplete>();
			final String authorizationHeader = getAuthorizationHeader(apiUrl);
			PricesApi pricesApi = new PricesApi();
			pricesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
			int i = 1;
			do{
				result = pricesApi.pricesGet3(i, PER_PAGE, null);
				voyagePricesComplete.addAll(result);
				i++;
			}while(!result.isEmpty());

		}
		return voyagePricesComplete;
	}

	private VoyagePriceComplete getVoyagePriceById(List<VoyagePriceComplete> voyagePrices,Integer voyageId){
		VoyagePriceComplete voyagePriceComplete = null ;
		if(voyagePrices != null && !voyagePrices.isEmpty()){

			voyagePriceComplete= voyagePrices
					.stream()
					.filter(item -> voyageId.equals(item.getVoyageId()))
					.findAny()
					.orElse(null);
		}
		return voyagePriceComplete;
	}

	private void buildOrUpdateSuiteNodes(Page cruisePage, Voyage voyage)throws RepositoryException,IOException,ApiException{
		
		//Create or update suites nodes
		List<VoyagePriceComplete> voyagePrices = getVoyagePrices(PRICE_API_URL);
		VoyagePriceComplete voyagePrice = getVoyagePriceById(voyagePrices,voyage.getVoyageId());
		Node suitesNode = ImporterUtils.findOrCreateNode(cruisePage.adaptTo(Node.class),"suites");
		ImporterUtils.saveSession(session,false);
		
		if(voyagePrice != null && !voyagePrice.getMarketCurrency().isEmpty()){

			for(Price price : voyagePrice.getMarketCurrency().get(0).getCruiseOnlyPrices()){

				Iterator<Resource> res = ImporterUtils.findResourceById(JcrConstants.NT_UNSTRUCTURED, "suiteCategoryCod",price.getSuiteCategoryCod(),resourceResolver);
				Node suiteNode = ImporterUtils.adaptOrCreateNode(res,suitesNode,price.getSuiteCategoryCod());

				Resource suite = findSuiteReference(voyage.getShipId(),price.getSuiteCategoryCod());
				String suiteReference ="";
				if(suite!=null){
					ValueMap properties = suite.getValueMap();
					suiteReference = properties.get("suiteReference",String.class);
				}

				suiteNode.setProperty("suiteReference", suiteReference);
				suiteNode.setProperty("suiteCategoryCod", price.getSuiteCategoryCod());
				ImporterUtils.saveSession(session,false);
				//Create variationNode
				buildOrUpdateVariationNodes(voyagePrice.getMarketCurrency(),suiteNode,price.getSuiteCategoryCod(),voyage.getVoyageCod());
				ImporterUtils.saveSession(session,false);
			}

		}
	}
	
	private void buildOrUpdateVariationNodes(List<VoyagePriceMarket> voyagePriceMarketList,Node suiteNode,String suiteCategoryCode,String voyageCode)throws RepositoryException{
		for(VoyagePriceMarket voyagePriceMarket : voyagePriceMarketList){

			Price price = voyagePriceMarket.getCruiseOnlyPrices()
					.stream()
					.filter(item -> suiteCategoryCode.equals(item.getSuiteCategoryCod()))
					.findAny()
					.orElse(null);

			if(price != null){
				//TODO variation id
				String variationId = voyageCode  + suiteCategoryCode + voyagePriceMarket.getMarketCod() + voyagePriceMarket.getCurrencyCod();
				Iterator<Resource> resources = ImporterUtils.findResourceById(JcrConstants.NT_UNSTRUCTURED, "variationId",variationId,resourceResolver);
				Node variationNode = ImporterUtils.adaptOrCreateNode(resources,suiteNode,voyagePriceMarket.getMarketCod()+"_"+voyagePriceMarket.getCurrencyCod());
				String[] geotaggingTags = {GEOTAGGING_TAG_PREFIX + voyagePriceMarket.getMarketCod().toLowerCase()};

				variationNode.setProperty("price", price.getCruiseOnlyFare());
				//TODO deckNumber
				variationNode.setProperty("deckNumber", "");
				variationNode.setProperty("currency", price.getCurrencyCod());
				variationNode.setProperty("availability",price.getSuiteAvailability());
				//TODO Review tags
				variationNode.setProperty(NameConstants.PN_TAGS, geotaggingTags);
				variationNode.setProperty("variationId", variationId);
				//Calculate the lowest price
				calculateLowestPrice(price,voyagePriceMarket.getMarketCod());
			}
		}
	}

	private Resource findSuiteReference(Integer shipId,String suiteCategoryCode){

		Iterator<Resource> resources = ImporterUtils.findResourceById(NameConstants.NT_PAGE, "shipId", Objects.toString(shipId),resourceResolver);
		if(resources.hasNext()){
			Page ship = resources.next().adaptTo(Page.class);
			Resource suitesNode = ship.getContentResource("suites");
			if(suitesNode != null){
				Iterator<Resource> suites = suitesNode.getChildren().iterator();

				if(suites.hasNext()){
					Resource suite = suites.next();
					String[] categories = suites.next().getValueMap().get("suiteCategoryCod",String[].class);

					if(categories != null && Arrays.asList(categories).contains(suiteCategoryCode)){
						return suite;
					}
				}
			}

		}
		return null;
	}

	private String getDestinationPath(Integer destinationId){
		String path = null;
		Iterator<Resource> resources = ImporterUtils.findResourceById(NameConstants.NT_PAGE, "destination_id",Objects.toString(destinationId),resourceResolver);
		if(resources.hasNext()){
			path = StringUtils.substringAfterLast(resources.next().getPath(), "/");
		}
		return path;
	}
	
	private List<VoyageSpecialOffer> getVoyageSpecialOffers(String apiUrl,Integer voyageId)throws IOException,ApiException{
		
		final String authorizationHeader = getAuthorizationHeader(apiUrl);

		List<VoyageSpecialOffer> voyageSpecialOffers;
		VoyageSpecialOffersApi voyageSpecialOffersApi = new VoyageSpecialOffersApi();
		voyageSpecialOffersApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
		voyageSpecialOffers = voyageSpecialOffersApi.voyageSpecialOffersGet(null, voyageId, null, null, null);
		
		return voyageSpecialOffers;
			
	}
	
	private String[] findSpecialOffersReferences(List<VoyageSpecialOffer> voyageSpecialOffers){
		
		List<String> references = new ArrayList<String>();
		
		if(voyageSpecialOffers!=null && !voyageSpecialOffers.isEmpty()){
			voyageSpecialOffers
			 .get(0).getSpecialOffers()
			 .stream()
		     .distinct()
		     .map(SpecialOfferByMarket::getVoyageSpecialOfferId)
		     .collect(Collectors.toList())
		     .forEach(id ->{
					references.add(ImporterUtils.findReference("exclusiveOfferId", Objects.toString(id),resourceResolver));
		     });
		}
		
		return references.stream().toArray(String[]::new);
	}
	
	private void calculateLowestPrice(Price price, String marketCode){   
	    if(price != null && !StringUtils.equals(PRICE_WAITLIST, Objects.toString(price.getCruiseOnlyFare()))){
	        String variation = marketCode+"_"+price.getCurrencyCod();
	        if(Arrays.stream(PriceVariations.values())
	                .anyMatch(e -> e.name().equals(variation)) &&
	            (!lowestPrices.containsKey(variation) || 
	            price.getCruiseOnlyFare()< lowestPrices.get(variation))){
	            lowestPrices.put(variation, price.getCruiseOnlyFare());
	        }
	    }
	}
	
	private void buildLowestPrices(Page rootPage,Map<String,Integer> prices){
	    if(prices != null && !prices.isEmpty()){
	        prices.forEach((key,value)->{
                try {
                    Node node = ImporterUtils.findOrCreateNode( rootPage.adaptTo(Node.class),key);
                    node.setProperty("lowestPrice", value);   
                } catch (RepositoryException e) {
                  LOGGER.error("Exception while importing lowest prices",e);
                }
           
	        });
	    }
	    
	}
}
