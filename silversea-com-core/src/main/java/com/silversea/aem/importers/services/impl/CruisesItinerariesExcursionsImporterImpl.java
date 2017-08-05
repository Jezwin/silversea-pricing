package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesExcursionsImporter;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.model.ShorexItinerary;
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

/**
 * TODO add last import date
 */
@Service
@Component
public class CruisesItinerariesExcursionsImporterImpl implements CruisesItinerariesExcursionsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesItinerariesExcursionsImporterImpl.class);

    protected int sessionRefresh = 100;
    protected int pageSize = 100;

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Reference
    protected ApiConfigurationService apiConfig;

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
    public ImportResult importAllItems() {
        return importSampleSet(-1);
    }

    @Override
    public ImportResult importSampleSet(int size) {
        LOGGER.debug("Starting excursions import ({})", size == -1 ? "all" : size);

        int successNumber = 0;
        int errorNumber = 0;
        int apiPage = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final ShorexesApi shorexesApi = new ShorexesApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing excursions deletion
            LOGGER.debug("Cleaning already imported excursions");

            ImporterUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary/excursion\"]");

            // Initializing elements necessary to import excursions
            // itineraries
            final List<ItineraryModel> itinerariesMapping = ImporterUtils.getItineraries(resourceResolver);

            // excursions
            final Map<Integer, Map<String, String>> excursionsMapping = ImporterUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/excursion\"]",
                    "shorexId");

            // Importing excursions
            List<ShorexItinerary> excursions;
            int itemsWritten = 0;

            do {
                excursions = shorexesApi.shorexesGetItinerary(null, null, null, apiPage, pageSize, null);

                // Iterating over excursions received from API
                for (final ShorexItinerary excursion : excursions) {

                    // Trying to deal with one excursion
                    try {
                        final Integer excursionId = excursion.getShorexId();
                        boolean imported = false;

                        if (!excursionsMapping.containsKey(excursionId)) {
                            throw new ImporterException("Excursion " + excursionId + " is not present in excursions cache");
                        }

                        // Iterating over itineraries in cache to write excursion
                        for (final ItineraryModel itineraryModel : itinerariesMapping) {

                            // Checking if the itinerary correspond to excursion informations
                            if (itineraryModel.isItinerary(excursion.getVoyageId(), excursion.getCityId(), excursion.getDate().toGregorianCalendar())) {

                                // Trying to write excursion data on itinerary
                                try {
                                    final Resource itineraryResource = itineraryModel.getResource();

                                    LOGGER.trace("Importing excursion {} in itinerary {}", excursion.getShorexItineraryId(), itineraryResource.getPath());

                                    final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                    final Node excursionsNode = JcrUtils.getOrAddNode(itineraryNode, "excursions", "nt:unstructured");

                                    // TODO to check : getShorexItineraryId() is not unique over API
                                    final Node excursionNode = excursionsNode.addNode(JcrUtil.createValidChildName(excursionsNode,
                                            String.valueOf(excursion.getShorexItineraryId())));

                                    final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                                    // associating excursion page
                                    if (excursionsMapping.get(excursionId).containsKey(lang)) {
                                        excursionNode.setProperty("excursionReference", excursionsMapping.get(excursionId).get(lang));
                                    }

                                    excursionNode.setProperty("excursionId", excursionId);
                                    excursionNode.setProperty("excursionItineraryId", excursion.getShorexItineraryId());
                                    excursionNode.setProperty("date", excursion.getDate().toGregorianCalendar());
                                    excursionNode.setProperty("plannedDepartureTime", excursion.getPlannedDepartureTime());
                                    excursionNode.setProperty("generalDepartureTime", excursion.getGeneralDepartureTime());
                                    excursionNode.setProperty("duration", excursion.getDuration());
                                    excursionNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/excursion");

                                    successNumber++;
                                    itemsWritten++;

                                    imported = true;

                                    if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                        try {
                                            session.save();

                                            LOGGER.debug("{} excursions imported, saving session", +itemsWritten);
                                        } catch (RepositoryException e) {
                                            session.refresh(true);
                                        }
                                    }
                                } catch (RepositoryException e) {
                                    LOGGER.error("Cannot write excursion {}", excursion.getShorexId(), e);

                                    errorNumber++;
                                }
                            }

                            if (size != -1 && itemsWritten >= size) {
                                break;
                            }
                        }

                        LOGGER.trace("Excursion {} voyage id: {} city id: {} imported status: {}", excursion.getShorexId(), excursion.getVoyageId(), excursion.getCityId(), imported);
                    } catch (ImporterException e) {
                        LOGGER.warn("Cannot deal with excursion {} - {}", excursion.getShorexId(), e.getMessage());

                        errorNumber++;
                    }
                }

                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (excursions.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} itineraries excursions imported, saving session", +itemsWritten);
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
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.info("Ending itineraries excursions import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        // TODO implement
        return null;
    }
}
