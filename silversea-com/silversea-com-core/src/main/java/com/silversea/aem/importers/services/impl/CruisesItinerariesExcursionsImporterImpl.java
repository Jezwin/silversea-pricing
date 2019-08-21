package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesExcursionsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;

import com.silversea.aem.utils.LogzLogger;
import com.silversea.aem.utils.LogzLoggerFactory;
import io.logz.sender.com.google.gson.JsonObject;
import io.swagger.client.ApiException;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.ShorexItinerary;
import io.swagger.client.model.ShorexItinerary77;
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
public class CruisesItinerariesExcursionsImporterImpl implements CruisesItinerariesExcursionsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesItinerariesExcursionsImporterImpl.class);
    protected int sessionRefresh = 100;
    protected int pageSize = 100;

    private boolean importRunning;

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Reference
    protected ApiConfigurationService apiConfig;

    @Reference
    protected LogzLoggerFactory  logzLoggerFactory;

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
        LogzLogger logzLogger = logzLoggerFactory.getLogger("CruisesItinerariesExcursionsImporterImpl");
        JsonObject log = new JsonObject();
        log.addProperty("message", "Start of the import process");
        logzLogger.logInfo("start",log);
        if (importRunning) {
            throw new ImporterException("Import is already running");
        }

        LOGGER.debug("Starting excursions import");
        importRunning = true;

        final ImportResult importResult = new ImportResult();
        int apiPage = 1;
        int apiPageDiff = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final ShorexesApi shorexesApi = new ShorexesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            if (!update) {
                // Existing excursions deletion
                LOGGER.debug("Cleaning already imported excursions");

                ImportersUtils.deleteResources(resourceResolver, sessionRefresh,
                        "/jcr:root/content/silversea-com//element(*,nt:unstructured)" +
                                "[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary/excursion\"]");
            }

            // getting last import date
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage, "lastModificationDateCruisesItinerariesExcursions");

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
            
            // Initializing elements necessary to import excursions
            
            // cruises mapping
            final Map<Integer, Map<String, String>> cruisesMapping = ImportersUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
                    "cruiseId");
            
            // itineraries
            final List<ItineraryModel> itinerariesMapping = ImportersUtils.getItineraries(resourceResolver);

            // excursions
            final Map<String, Map<String, String>> excursionsMapping = ImportersUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/excursion\"]", "shorexId", "cityId");

            // Importing excursions
            //Default update excursion for modified cruise
            List<ShorexItinerary> excursions;
            int itemsWritten = 0;
            apiPage = 1;

            
            do {
                excursions = shorexesApi.shorexesGetItinerary(null, null, null, apiPage, pageSize, null);

                // Iterating over excursions received from API
                for (final ShorexItinerary excursion : excursions) {

                    // Trying to deal with one excursion
                    try {
                        if (update && !modifiedCruises.contains(excursion.getVoyageId())) {
                            throw new ImporterException("Cruise " + excursion.getVoyageId() + " is not modified");
                        }

                        final Integer excursionId = excursion.getShorexId();
                        boolean imported = false;



                        // Iterating over itineraries in cache to write excursion
                        for (final ItineraryModel itineraryModel : itinerariesMapping) {

                            // Checking if the itinerary correspond to excursion informations
                            if (itineraryModel.isItineraryBasedOnDayOnly(excursion.getVoyageId(),
                                    excursion.getDate().toGregorianCalendar()) && excursion.getCityId().equals(itineraryModel.getPortId())) {
                                //YES THIS IS THE FUCKINJG PART OF SHIT
                               // if(!excursion.getCityId().equals(itineraryModel.getPortId())){
                                   // throw new ImporterException(
                                     //       "Excursion " + excursionId + " iHAAAAAAAAAAAAAA");
                                //    LOGGER.debug("HAAA FUCK I HATE THIS SHIT -- trhy to import " + excursion.getShorexItineraryId() + " on voyage " + excursion.getVoyageId() + " but port of excursion " + excursion.getCityId() + " is not equal with " + itineraryModel.getPortId());
                               // }

                                // Trying to write excursion data on itinerary
                                try {
                                    if (!excursionsMapping.containsKey(excursionId + "-" + itineraryModel.getPortId())) {
                                        throw new ImporterException(
                                                "Excursion " + excursionId + " is not present in excursions cache");
                                    }

                                    final Resource itineraryResource = itineraryModel.getResource();

                                    LOGGER.trace("Importing excursion {} in itinerary {}",
                                            excursion.getShorexItineraryId(), itineraryResource.getPath());

                                    final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                    final Node excursionsNode = JcrUtils.getOrAddNode(itineraryNode, "excursions", "nt:unstructured");

                                    // TODO to check : getShorexItineraryId() is not unique over API
                                    if (!excursionsNode.hasNode(String.valueOf(excursion.getShorexItineraryId()))) {
                                        final Node excursionNode = excursionsNode.addNode(
                                                JcrUtil.createValidChildName(excursionsNode,
                                                        String.valueOf(excursion.getShorexItineraryId())));

                                        final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                                        // associating excursion page
                                        if (excursionsMapping.get(excursionId + "-" + itineraryModel.getPortId()).containsKey(lang)) {
                                            excursionNode.setProperty("excursionReference",
                                                    excursionsMapping.get(excursionId + "-" + itineraryModel.getPortId()).get(lang));
                                          //  here to check the good link to Excursion under the good port
                                        }

                                        excursionNode.setProperty("excursionId", excursionId);
                                        excursionNode.setProperty("excursionItineraryId", excursion.getShorexItineraryId());
                                        excursionNode.setProperty("date", excursion.getDate().toGregorianCalendar());
                                        excursionNode.setProperty("plannedDepartureTime", excursion.getPlannedDepartureTime());
                                        excursionNode.setProperty("generalDepartureTime", excursion.getGeneralDepartureTime());
                                        excursionNode.setProperty("duration", excursion.getDuration());
                                        excursionNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/excursion");

                                        importResult.incrementSuccessNumber();
                                        itemsWritten++;

                                        imported = true;

                                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                            try {
                                                session.save();

                                                LOGGER.info("{} excursions imported, saving session", +itemsWritten);
                                            } catch (RepositoryException e) {
                                                session.refresh(true);
                                            }
                                        }
                                    }
                                }catch (ImporterException e) {
                                    LOGGER.error("Cannot find excursion in cache {}", excursion.getShorexId() + "-" + itineraryModel.getPortId());

                                    importResult.incrementErrorNumber();
                                } catch (Exception e) {
                                    LOGGER.error("Cannot write excursion {}", excursion.getShorexId(), e);

                                    importResult.incrementErrorNumber();
                                }
                            }
                        }

                        LOGGER.trace("Excursion {} voyage id: {} city id: {} imported status: {}",
                                excursion.getShorexId(), excursion.getVoyageId(), excursion.getCityId(), imported);
                    } catch (ImporterException e) {
                        LOGGER.warn("Cannot deal with excursion {} - {}", excursion.getShorexId(), e.getMessage());

                        importResult.incrementErrorNumber();
                    }
                }

                apiPage++;
            } while (excursions.size() > 0);
            
            
            //Update in function of the changeFrom endpoint (real diff)
            List<ShorexItinerary77> excursionsDiff;
            int itemsWrittenDiff = 0;
            apiPageDiff = 1;

            LOGGER.info("Launching itineraries excursions diff import");
            do {
                excursionsDiff = shorexesApi.shorexesGetItinerary2(lastModificationDate, apiPageDiff, pageSize, null);
                
                // Iterating over excursions received from API
                for (final ShorexItinerary77 excursionDiff : excursionsDiff) {

                    // Trying to deal with one excursion
                    try {
                        
                        final Integer excursionId = excursionDiff.getShorexId();
                        boolean imported = false;


                        // Iterating over itineraries in cache to write excursion
                        for (final ItineraryModel itineraryModel : itinerariesMapping) {

                            // Checking if the itinerary correspond to excursion informations
                            if (itineraryModel.isItineraryBasedOnDayOnly(excursionDiff.getVoyageId(),
                            		excursionDiff.getDate().toGregorianCalendar()) && excursionDiff.getCityId().equals(itineraryModel.getPortId())) {

                                // Trying to write or update excursion data on itinerary
                                try {
                                    if (!excursionsMapping.containsKey(excursionId+ "-" + itineraryModel.getPortId())) {
                                        throw new ImporterException(
                                                "Excursion " + excursionId + " is not present in excursions cache");
                                    }


                                    final Resource itineraryResource = itineraryModel.getResource();

                                    LOGGER.trace("Importing excursion {} in itinerary {}",
                                    		excursionDiff.getShorexItineraryId(), itineraryResource.getPath());

                                    final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                    final Node excursionsNode = JcrUtils.getOrAddNode(itineraryNode, "excursions", "nt:unstructured");

                                    if(!BooleanUtils.isTrue(excursionDiff.getIsDeleted())){
                                    // TODO to check : getShorexItineraryId() is not unique over API
	                                    if (!excursionsNode.hasNode(String.valueOf(excursionDiff.getShorexItineraryId()))) {
	                                    	//Create new excursion
	                                        final Node excursionNode = excursionsNode.addNode(
	                                                JcrUtil.createValidChildName(excursionsNode,
	                                                        String.valueOf(excursionDiff.getShorexItineraryId())));
	
	                                        final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);
	
	                                        // associating excursion page
	                                        if (excursionsMapping.get(excursionId+ "-" + itineraryModel.getPortId()).containsKey(lang)) {
	                                            excursionNode.setProperty("excursionReference",
	                                                    excursionsMapping.get(excursionId+ "-" + itineraryModel.getPortId()).get(lang));
	                                        }
	
	                                        excursionNode.setProperty("excursionId", excursionId);
	                                        excursionNode.setProperty("excursionItineraryId", excursionDiff.getShorexItineraryId());
	                                        excursionNode.setProperty("date", excursionDiff.getDate().toGregorianCalendar());
	                                        excursionNode.setProperty("plannedDepartureTime", excursionDiff.getPlannedDepartureTime());
	                                        excursionNode.setProperty("generalDepartureTime", excursionDiff.getGeneralDepartureTime());
	                                        excursionNode.setProperty("duration", excursionDiff.getDuration());
	                                        excursionNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/excursion");
	
	                                        
	
	                                        
                                    }else{
                                    	//update existing node excursionDiff
                                    	final Node excursionNodeToUpdate = excursionsNode.getNode(String.valueOf(excursionDiff.getShorexItineraryId()));
                                    	excursionNodeToUpdate.setProperty("date", excursionDiff.getDate().toGregorianCalendar());
                                    	excursionNodeToUpdate.setProperty("plannedDepartureTime", excursionDiff.getPlannedDepartureTime());
                                    	excursionNodeToUpdate.setProperty("generalDepartureTime", excursionDiff.getGeneralDepartureTime());
                                    	excursionNodeToUpdate.setProperty("duration", excursionDiff.getDuration());
                                    	
                                    }
                                    }else{
                                    	//remove the current excursionDiff
                                    	final Node excursionNodeToDelete = excursionsNode.getNode(String.valueOf(excursionDiff.getShorexItineraryId()));
                                    	excursionNodeToDelete.remove();
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

                                            LOGGER.info("{} excursions diff imported, saving session", +itemsWrittenDiff);
                                        } catch (RepositoryException e) {
                                            session.refresh(true);
                                        }
                                    }
                                    
                                }catch (ImporterException e) {
                                    LOGGER.error("Cannot find excursion in cache {}", excursionDiff.getShorexId() + "-" + itineraryModel.getPortId());

                                    importResult.incrementErrorNumber();
                                } catch (Exception e) {
                                    LOGGER.error("Cannot write excursion {}", excursionDiff.getShorexId(), e);

                                    importResult.incrementErrorNumber();
                                }
                            }
                        }

                        LOGGER.trace("Excursion {} voyage id: {} city id: {} imported status: {}",
                        		excursionDiff.getShorexId(), excursionDiff.getVoyageId(), excursionDiff.getCityId(), imported);
                    } catch (Exception e) {
                        LOGGER.warn("Cannot deal with excursion {} - {}", excursionDiff.getShorexId(), e.getMessage());

                        importResult.incrementErrorNumber();
                    }
                }

                apiPageDiff++;
            } while (excursionsDiff.size() > 0);
            
            
            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
                    "lastModificationDateCruisesItinerariesExcursions", false);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.info("{} itineraries excursions imported, saving session", +itemsWritten);
                    LOGGER.info("{} itineraries diff excursions imported, saving session", +itemsWrittenDiff);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import excursions", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read excursions from API", e);
        } finally {
            importRunning = false;
        }

        LOGGER.info("Ending itineraries excursions import, success: {}, errors: {}, api calls : {} and {} for diff",
                +importResult.getSuccessNumber(), +importResult.getErrorNumber(), apiPage, apiPageDiff);

        return importResult;
    }
}
