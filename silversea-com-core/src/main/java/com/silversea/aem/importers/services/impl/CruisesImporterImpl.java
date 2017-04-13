package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
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
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesImporter;

import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.api.LandsApi;
import io.swagger.client.api.PricesApi;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.Itinerary;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.Price;
import io.swagger.client.model.ShorexItinerary;
import io.swagger.client.model.Voyage;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyagePriceMarket;


@Service
@Component(label = "Silversea.com - Cruises importer")
public class CruisesImporterImpl extends BaseImporter implements CruisesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(CruisesImporterImpl.class);
	private static final int PER_PAGE = 100;
	private static final String VOYAGE_API_URL="/api/v1/voyages";
	private static final String LAND_ADVENTURES_API_URL ="/api/v1/landAdventures/Itinerary";
	private static final String CUISE_TEMPLATE = "/apps/silversea/silversea-com/templates/cruise";
	private static final String HOTEL_ITINERARY_API_URL = "/api/v1/hotels/Itinerary";
	private static final String SHORE_EXCURSIONS_API_URL ="/api/v1/shoreExcursions/Itinerary";
	private static final String PRICE_API_URL = "/api/v1/prices";
	private static final String GEOTAGGING_TAG_PREFIX = "geotagging:";

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	private ResourceResolver resourceResolver ;
	private PageManager pageManager;
	private Session session;

	@Override
	public void importCruises() throws IOException {
		final String authorizationHeader = getAuthorizationHeader(VOYAGE_API_URL);

		VoyagesApi voyageApi = new VoyagesApi();
		voyageApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
		try {
			resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			pageManager = resourceResolver.adaptTo(PageManager.class);
			session = resourceResolver.adaptTo(Session.class);

			List<Voyage> voyages;
			Iterator<Resource> resources;
			Page cruisePage;
			int i = 1;

			do {

				voyages = voyageApi.voyagesGet(null, null, null, null, null, i, PER_PAGE, null, null);

				int j = 0;

				for (Voyage voyage : voyages) {

					//TODO retrieve cruises root page dynamically
					Page cruisesRootPage = pageManager.getPage(ImportersConstants.BASEPATH_CRUISES);

					//Retrieve and create or update cruise page
					resources = findResourceById(NameConstants.NT_PAGE,"cruiseId",Objects.toString(voyage.getVoyageId()));
					cruisePage = adaptOrCreatePage(resources,CUISE_TEMPLATE,cruisesRootPage,voyage.getVoyageName());
					updateCruisePage(cruisePage,voyage);

					//Retrieve or create itineraries node
					Node itinerairesNode = findOrCreateNode(cruisePage.adaptTo(Node.class),"itineraries");
					saveSession(session,false);
					List<Itinerary> itinerairesCruise = getCruiseIteneraries(voyage.getItineraryUrl(),voyage.getVoyageId());
					

					for (Itinerary itinerary : itinerairesCruise) {

						//Retrieve and create or update itenirary node
						resources = findResourceById(NameConstants.NT_PAGE, "itineraryId",Objects.toString(itinerary.getItineraryId()));
						Node itineraryNode = adaptOrCreateNode(resources,itinerairesNode,Objects.toString(itinerary.getItineraryId()));
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
					saveSession(session,false);;
					//Create or update suites nodes
					List<VoyagePriceComplete> voyagePrices = getVoyagePrices(PRICE_API_URL);
					VoyagePriceComplete voyagePrice = getVoyagePriceById(voyagePrices,voyage.getVoyageId());
					Node suitesNode = findOrCreateNode(cruisePage.adaptTo(Node.class),"suites");
					saveSession(session,false);
					buildOrUpdateSuiteNodes(voyagePrice,suitesNode,voyage);

					j++;

					saveSession(session,false);
				}

				i++;
			} while (voyages.size() > 0);

			saveSession(session,false);

		} catch (ApiException | WCMException | LoginException  | RepositoryException e) {
			LOGGER.error("Exception importing cruises", e);
		}
		finally{
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
				resourceResolver = null;
			}
		}
	}



	/**
	 * Save session
	 * @param session : session to save
	 * @param isRefresh: boolean indicates if we refresh session
	 * @throws RepositoryException: throw a repository exception
	 */
	private void saveSession(Session session,boolean isRefresh) throws RepositoryException{
		try {
			if (session.hasPendingChanges()) {
				session.save();
			}
		} catch (RepositoryException e) {
			session.refresh(isRefresh);
		}
	}

	private Iterator<Resource> findResourceById(String type,String property, String id){

		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("type", type);
		queryMap.put("property", property);
		queryMap.put("id", id);
		String queryTemplate = "//element(*,${type})[jcr:content/${property}=${id}]";
		StrSubstitutor substitutor = new StrSubstitutor(queryMap);

		//Execute query
		Iterator<Resource>resources = resourceResolver.findResources(substitutor.replace(queryTemplate), "xpath");

		return resources;
	}

	private Page adaptOrCreatePage(Iterator<Resource> resources,String template,Page RootPage,String title) throws WCMException, RepositoryException{

		Page page = null;
		if (resources.hasNext()) {
			page = resources.next().adaptTo(Page.class);
		} else {
			page = pageManager.create(RootPage.getPath(),
					JcrUtil.createValidChildName(RootPage.adaptTo(Node.class),title),
					template, title, false);
		}

		return page;
	}

	private Node adaptOrCreateNode(Iterator<Resource> resources,Node rootNode,String nodeName) throws RepositoryException{
		Node node = null;
		if (resources.hasNext()) {
			node = resources.next().adaptTo(Node.class);
		} else {
			node = rootNode.addNode(nodeName);
			node.setPrimaryType(JcrConstants.NT_UNSTRUCTURED);
		}

		return node;
	}

	private Node findOrCreateNode(Node element,String name)throws RepositoryException{
		Node node = null;

		if (element.hasNode(name)) {
			node = element.getNode(name);
		} else {
			node = element.addNode(name);
			node.setPrimaryType(JcrConstants.NT_UNSTRUCTURED);
		}
		return node;
	}

	private void updateCruisePage(Page cruisePage, Voyage voyage) throws RepositoryException{
		Node cruisePageContentNode = cruisePage.getContentResource().adaptTo(Node.class);

		cruisePageContentNode.setProperty(JcrConstants.JCR_TITLE, voyage.getVoyageName());
		cruisePageContentNode.setProperty("startDate", voyage.getArriveDate().toString());
		// TODO  voyageHighlights ?
		cruisePageContentNode.setProperty("endDate", voyage.getDepartDate().toString());
		cruisePageContentNode.setProperty("duration", voyage.getDays());
		cruisePageContentNode.setProperty("shipReference", findReference("shipId",Objects.toString(voyage.getShipId())));
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

		final String authorizationlandProgramHeader = getAuthorizationHeader(apiUrl);

		LandsApi landProgramApi = new LandsApi();
		landProgramApi.getApiClient().addDefaultHeader("Authorization", authorizationlandProgramHeader);
		List<LandItinerary> landsProgram = landProgramApi.landsGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(),
				null, null, null, null);

		return landsProgram;
	}

	private void updateItineraryNode(Node itineraryNode ,Itinerary itinerary)throws RepositoryException{
		itineraryNode.setProperty("portReference", findReference("cityId", Objects.toString(itinerary.getCityId())));
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


			Node landsNode = findOrCreateNode(itineraryNode,"land-programs");

			for (LandItinerary land : landProgramList) {

				Iterator<Resource> resources = findResourceById(JcrConstants.NT_UNSTRUCTURED, "landItineraryId",Objects.toString(land.getLandItineraryId()));
				Node landNode =adaptOrCreateNode(resources,landsNode, Objects.toString(land.getLandItineraryId()));
				landNode.setProperty("landProgramReference", findReference("landProgramId", Objects.toString(land.getLandItineraryId())));
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

			Node HotelsNode = findOrCreateNode(itineraryNode,"hotels");

			for (HotelItinerary hotel : hotels) {

				Iterator<Resource> resources = findResourceById(JcrConstants.NT_UNSTRUCTURED, "hotelItineraryId",Objects.toString(hotel.getHotelItineraryId()));
				Node hotelNode = adaptOrCreateNode(resources,HotelsNode,Objects.toString(hotel.getHotelItineraryId()));

				hotelNode.setProperty("hotelReference", findReference("hotelId", Objects.toString(hotel.getHotelItineraryId())));
				hotelNode.setProperty("hotelItineraryId", hotel.getHotelItineraryId());
				hotelNode.setProperty("cityId", hotel.getCityId());

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

			Node excursionsNode = findOrCreateNode(itineraryNode,"excursions");

			for (ShorexItinerary excursion : excursions) {

				Iterator<Resource> resources = findResourceById(JcrConstants.NT_UNSTRUCTURED, "shorexItineraryId",Objects.toString(excursion.getShorexItineraryId()));
				Node excursionNode = adaptOrCreateNode(resources,excursionsNode,Objects.toString(excursion.getShorexItineraryId()));

				excursionNode.setProperty("excursionReference", findReference("shorexId", Objects.toString(excursion.getShorexId())));
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

		List<VoyagePriceComplete> voyagePricesComplete;
		final String authorizationHeader = getAuthorizationHeader(apiUrl);
		PricesApi pricesApi = new PricesApi();
		pricesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
		voyagePricesComplete = pricesApi.pricesGet3(null, null, null);

		return voyagePricesComplete;
	}

	private VoyagePriceComplete getVoyagePriceById(List<VoyagePriceComplete> voyagePrices,Integer code){
		for(VoyagePriceComplete voyagePrice : voyagePrices)
		{
			if(voyagePrice.getVoyageCod().equals(code)){
				return voyagePrice;
			}
		}
		return null;
	}

	private void buildOrUpdateSuiteNodes(VoyagePriceComplete voyagePrice,Node suitesNode,Voyage voyage)throws RepositoryException{
		if(voyagePrice != null && !voyagePrice.getMarketCurrency().isEmpty()){

			for(Price price : voyagePrice.getMarketCurrency().get(0).getCruiseOnlyPrices()){

				Iterator<Resource> res = findResourceById(JcrConstants.NT_UNSTRUCTURED, "suiteCategoryCod",price.getSuiteCategoryCod());
				Node suiteNode = adaptOrCreateNode(res,suitesNode,price.getSuiteCategoryCod());

				Resource suite = findSuiteReference(voyage.getShipId(),price.getSuiteCategoryCod());
				String suiteReference ="";
				if(suite!=null){
					ValueMap properties = suite.getValueMap();
					suiteReference = properties.get("suiteReference",String.class);
				}

				suiteNode.setProperty("suiteReference", suiteReference);
				suiteNode.setProperty("suiteCategoryCod", price.getSuiteCategoryCod());
				saveSession(session,false);
				//Create variationNode
				buildOrUpdateVariationNodes(voyagePrice.getMarketCurrency(),suiteNode,price.getSuiteCategoryCod(),voyage.getVoyageCod());
				
			}

		}
	}
	private void buildOrUpdateVariationNodes(List<VoyagePriceMarket> voyagePriceMarketList,Node suiteNode,String suiteCategoryCod,String voyageCode)throws RepositoryException{
		for(VoyagePriceMarket voyagePriceMarket : voyagePriceMarketList){
			for(Price price : voyagePriceMarket.getCruiseOnlyPrices()){
				if(suiteCategoryCod.equals(price.getSuiteCategoryCod())){

					//TODO variation id
					String variationId = voyageCode  + suiteCategoryCod + voyagePriceMarket.getMarketCod();
					Iterator<Resource> resources = findResourceById(JcrConstants.NT_UNSTRUCTURED, "variationId",variationId);
					Node variationNode = adaptOrCreateNode(resources,suiteNode,voyagePriceMarket.getMarketCod());
					String[] geotaggingTags = {GEOTAGGING_TAG_PREFIX + voyagePriceMarket.getMarketCod().toLowerCase()};

					variationNode.setProperty("price", price.getCruiseOnlyFare());
					//TODO deckNumber
					variationNode.setProperty("deckNumber", "");
					variationNode.setProperty("currency", price.getCurrencyCod());
					variationNode.setProperty("availability",price.getSuiteAvailability());
					variationNode.setProperty(NameConstants.PN_TAGS, geotaggingTags);
					variationNode.setProperty("variationId", variationId);
					break;
				}
			}
		}

	}
	
	private String findReference(String property, String id){
		
		Iterator<Resource> res = findResourceById(NameConstants.NT_PAGE, property, id);
		String reference = res.hasNext() ? res.next().getPath() : "";

		return reference;
	}

	private Resource findSuiteReference(Integer shipId,String suiteCategoryCode){

		Iterator<Resource> resources = findResourceById(NameConstants.NT_PAGE, "shipId", Objects.toString(shipId));
		if(resources.hasNext()){
			Page ship = resources.next().adaptTo(Page.class);
			Resource suitesNode = ship.getContentResource("suistes");
			Iterator<Resource> suites = suitesNode.getChildren().iterator();

			if(suites.hasNext()){
				Resource suite = suites.next();
				String[] categories = suites.next().getValueMap().get("suiteCategoryCod",String[].class);

				if(categories != null && Arrays.asList(categories).contains(suiteCategoryCode)){
					return suite;
				}
			}
		}
		return null;
	}

}
