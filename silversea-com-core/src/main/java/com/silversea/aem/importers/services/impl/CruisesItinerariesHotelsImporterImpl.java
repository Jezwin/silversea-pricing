package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesHotelsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.HotelItinerary77;
import io.swagger.client.model.Voyage77;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.*;

@Service
@Component
public class CruisesItinerariesHotelsImporterImpl implements CruisesItinerariesHotelsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesItinerariesHotelsImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    private boolean importRunning;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Activate
    protected void activate(final ComponentContext context) {
        if (apiConfig.getSessionRefresh() != 0) {
            sessionRefresh = apiConfig.getSessionRefresh();
        }

        if (apiConfig.getPageSize() != 0) {
            pageSize = apiConfig.getPageSize();
        }
    }

    @Override
    public ImportResult importAllItems(final boolean update) throws ImporterException {
        if (importRunning) {
            throw new ImporterException("Import is already running");
        }

        LOGGER.debug("Starting hotels import");
        importRunning = true;

        final ImportResult importResult = new ImportResult();
        int apiPage = 1;
        int apiPageDiff = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final HotelsApi hotelsApi = new HotelsApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            if (!update) {
                // Existing hotels deletion
                LOGGER.debug("Cleaning already imported hotels");

                ImportersUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                        + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary/hotel\"]");
            }

            // getting last import date
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage, "lastModificationDateCruisesItinerariesHotels");

            // init modified voyages cruises
            final Set<Integer> modifiedCruises = new HashSet<>();
            final VoyagesApi voyagesApi = new VoyagesApi(ImportersUtils.getApiClient(apiConfig));
            List<Voyage77> cruises;
            do {
                cruises = voyagesApi.voyagesGetChanges(lastModificationDate, apiPage, pageSize, null, null);

                for (Voyage77 voyage : cruises) {
                    modifiedCruises.add(voyage.getVoyageId());
                }

                apiPage++;
            } while (cruises.size() > 0);

            // Initializing elements necessary to import hotels
            
            // cruises mapping
            final Map<Integer, Map<String, String>> cruisesMapping = ImportersUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
                    "cruiseId");
            
            // itineraries
            final List<ItineraryModel> itinerariesMapping = ImportersUtils.getItineraries(resourceResolver);

            // hotels
            final Map<String, Map<String, String>> hotelsMapping = ImportersUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/hotel\"]",
                    "hotelId", "cityId");

            // Importing hotels
            List<HotelItinerary> hotels;
            int itemsWritten = 0;
            apiPage = 1;

            do {
                hotels = hotelsApi.hotelsGetItinerary(null, null, null, apiPage, pageSize, null);

                // Iterating over hotels received from API
                for (HotelItinerary hotel : hotels) {

                    // Trying to deal with one hotel
                    try {
                        if (update && !modifiedCruises.contains(hotel.getVoyageId())) {
                            throw new ImporterException("Cruise " + hotel.getVoyageId() + " is not modified");
                        }

                        final Integer hotelId = hotel.getHotelId();
                        boolean imported = false;



                        // Iterating over itineraries in cache to write hotel
                        for (final ItineraryModel itineraryModel : itinerariesMapping) {

                            // Checking if the itinerary correspond to hotel informations
                            if (itineraryModel.isItinerary(hotel.getVoyageId(), hotel.getDate().toGregorianCalendar(), hotel.getCityId()) && hotel.getCityId().equals(itineraryModel.getPortId())) {

                                // Trying to write hotel data on itinerary
                                try {
                                    if (!hotelsMapping.containsKey(hotelId+"-"+itineraryModel.getPortId())) {
                                        throw new ImporterException("Hotel " + hotelId + " is not present in hotels cache");
                                    }
                                    final Resource itineraryResource = itineraryModel.getResource();

                                    LOGGER.trace("importing hotel {} in itinerary {}", hotelId, itineraryResource.getPath());

                                    final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                    final Node hotelsNode = JcrUtils.getOrAddNode(itineraryNode, "hotels", "nt:unstructured");

                                    // TODO to check : getHotelItineraryId() is not unique over API
                                    if (!hotelsNode.hasNode(String.valueOf(hotel.getHotelItineraryId()))) {
                                        final Node hotelNode = hotelsNode.addNode(JcrUtil.createValidChildName(hotelsNode,
                                                String.valueOf(hotel.getHotelItineraryId())));
                                        final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                                        // associating port page
                                        if (hotelsMapping.get(hotelId+"-"+itineraryModel.getPortId()).containsKey(lang)) {
                                            hotelNode.setProperty("hotelReference", hotelsMapping.get(hotelId+"-"+itineraryModel.getPortId()).get(lang));
                                        }

                                        hotelNode.setProperty("hotelId", hotelId);
                                        hotelNode.setProperty("hotelItineraryId", hotel.getHotelItineraryId());
                                        hotelNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/hotel");

                                        importResult.incrementSuccessNumber();
                                        itemsWritten++;

                                        imported = true;

                                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                            try {
                                                session.save();

                                                LOGGER.info("{} hotels imported, saving session", +itemsWritten);
                                            } catch (RepositoryException e) {
                                                session.refresh(true);
                                            }
                                        }
                                    }
                                }  catch (ImporterException e) {
                                    LOGGER.error("Cannot find excursion in cache {}", hotel.getHotelId() + "-" + itineraryModel.getPortId());

                                    importResult.incrementErrorNumber();
                                }catch (Exception e) {
                                    LOGGER.error("Cannot write hotel {}", hotel.getHotelId(), e);

                                    importResult.incrementErrorNumber();
                                }
                            }
                        }

                        LOGGER.trace("Hotel {} voyage id: {} city id: {} imported: {}", hotel.getHotelId(), hotel.getVoyageId(), hotel.getCityId(), imported);
                    } catch (Exception e) {
                        LOGGER.warn("Cannot deal with hotel {} - {}", hotel.getHotelId(), e.getMessage());

                        importResult.incrementErrorNumber();
                    }
                }

                apiPage++;
            } while (hotels.size() > 0);
            
            
            
            
         // ImportingDiff hotels
            List<HotelItinerary77> hotelsDiff;
            int itemsWrittenDiff = 0;
            apiPageDiff = 1;
            LOGGER.info("Launching itineraries excursions diff import");
            do {
            	hotelsDiff = hotelsApi.hotelsGetItinerary2(lastModificationDate, apiPageDiff, pageSize, null);

                // Iterating over hotels received from API
                for (HotelItinerary77 hotelDiff : hotelsDiff) {

                    // Trying to deal with one hotel
                    try {
                      
                        final Integer hotelId = hotelDiff.getHotelId();
                        boolean imported = false;



                        // Iterating over itineraries in cache to write hotel
                        for (final ItineraryModel itineraryModel : itinerariesMapping) {

                            // Checking if the itinerary correspond to hotel informations
                            if (itineraryModel.isItinerary(hotelDiff.getVoyageId(), hotelDiff.getDate().toGregorianCalendar(), hotelDiff.getCityId()) && hotelDiff.getCityId().equals(itineraryModel.getPortId())) {

                                // Trying to write hotel data on itinerary
                                try {
                                    if (!hotelsMapping.containsKey(hotelId+"-"+itineraryModel.getPortId())) {
                                        throw new ImporterException("Hotel " + hotelId + " is not present in hotels cache");
                                    }
                                    final Resource itineraryResource = itineraryModel.getResource();

                                    LOGGER.trace("importing hotel {} in itinerary {}", hotelId, itineraryResource.getPath());

                                    final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                    final Node hotelsNode = JcrUtils.getOrAddNode(itineraryNode, "hotels", "nt:unstructured");

                                    if(!BooleanUtils.isTrue(hotelDiff.getIsDeleted())){
                                    // TODO to check : getHotelItineraryId() is not unique over API
                                    if (!hotelsNode.hasNode(String.valueOf(hotelDiff.getHotelItineraryId()))) {
                                        final Node hotelNode = hotelsNode.addNode(JcrUtil.createValidChildName(hotelsNode,
                                                String.valueOf(hotelDiff.getHotelItineraryId())));
                                        final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                                        // associating port page
                                        if (hotelsMapping.get(hotelId+"-"+itineraryModel.getPortId()).containsKey(lang)) {
                                            hotelNode.setProperty("hotelReference", hotelsMapping.get(hotelId+"-"+itineraryModel.getPortId()).get(lang));
                                        }

                                        hotelNode.setProperty("hotelId", hotelId);
                                        hotelNode.setProperty("hotelItineraryId", hotelDiff.getHotelItineraryId());
                                        hotelNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/hotel");

                                       
                                    }else {
                                    	//update the current hotel
                                    	 final Node hotelNodeToUpdate = hotelsNode.getNode(String.valueOf(hotelDiff.getHotelItineraryId()));
                                    	 hotelNodeToUpdate.setProperty("hotelId", hotelId);
                                    	 hotelNodeToUpdate.setProperty("hotelItineraryId", hotelDiff.getHotelItineraryId());
                                    }
                                    }else {
                                    	//Delete the current hotel 
                                    	final Node hotelNodeToDelete = hotelsNode.getNode(String.valueOf(hotelDiff.getHotelItineraryId()));
                                    	hotelNodeToDelete.remove();
                                    }
                                    
                                    importResult.incrementSuccessNumber();
                                    itemsWrittenDiff++;

                                    imported = true;
                                    
                                    //set current cruise to activate state
                                    final Map<String, String> cruisePaths = cruisesMapping.get(itineraryModel.getCruiseId());
                                    for (Map.Entry<String, String> cruisePath : cruisePaths.entrySet()) {
                                    	final Node cruiseContentNode = session.getNode(cruisePath.getValue() + "/jcr:content");
                                    	 final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
                                         final Boolean isVisible = cruiseContentNode.getProperty("isVisible").getBoolean();

                                         if (startDate.after(Calendar.getInstance()) && isVisible) {
	                                    		cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
	                                    	}
                                    	}

                                    if (itemsWrittenDiff % sessionRefresh == 0 && session.hasPendingChanges()) {
                                        try {
                                            session.save();
                                            LOGGER.info("{} hotels diff imported, saving session", +itemsWrittenDiff);
                                        } catch (RepositoryException e) {
                                            session.refresh(true);
                                        }
                                    }
                                    
                                }  catch (ImporterException e) {
                                    LOGGER.error("Cannot find excursion in cache {}", hotelDiff.getHotelId() + "-" + itineraryModel.getPortId());

                                    importResult.incrementErrorNumber();
                                }catch (Exception e) {
                                    LOGGER.error("Cannot write hotel {}", hotelDiff.getHotelId(), e);

                                    importResult.incrementErrorNumber();
                                }
                            }
                        }

                        LOGGER.trace("Hotel {} voyage id: {} city id: {} imported: {}", hotelDiff.getHotelId(), hotelDiff.getVoyageId(), hotelDiff.getCityId(), imported);
                    } catch (Exception e) {
                        LOGGER.warn("Cannot deal with hotel {} - {}", hotelDiff.getHotelId(), e.getMessage());

                        importResult.incrementErrorNumber();
                    }
                }

                apiPageDiff++;
            } while (hotelsDiff.size() > 0);
            

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
                    "lastModificationDateCruisesItinerariesHotels", false);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.info("{} itineraries hotels imported, saving session", +itemsWritten);
                    LOGGER.info("{} itineraries hotels diff imported, saving session", +itemsWrittenDiff);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import hotels", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read hotels from API", e);
        } finally {
            importRunning = false;
        }

        LOGGER.info("Ending itineraries hotels import, success: {}, errors: {}, api calls : {} and {} diff",
                +importResult.getSuccessNumber(), +importResult.getErrorNumber(), apiPage, apiPageDiff);

        return importResult;
    }
}
