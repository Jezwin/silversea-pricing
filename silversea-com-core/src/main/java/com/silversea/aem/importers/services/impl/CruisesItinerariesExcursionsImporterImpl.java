package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesExcursionsImporter;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.model.HotelItinerary;
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

            final Iterator<Resource> existingExcursions = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary/excursion\"]", "xpath");

            int i = 0;
            while (existingExcursions.hasNext()) {
                final Resource excursion = existingExcursions.next();

                final Node excursionNode = excursion.adaptTo(Node.class);

                if (excursionNode != null) {
                    try {
                        excursionNode.remove();

                        i++;
                    } catch (RepositoryException e) {
                        LOGGER.error("Cannot remove existing excursion {}", excursion.getPath(), e);
                    }
                }

                if (i % sessionRefresh == 0 && session.hasPendingChanges()) {
                    try {
                        session.save();

                        LOGGER.debug("{} excursions cleaned, saving session", +i);
                    } catch (RepositoryException e) {
                        session.refresh(true);
                    }
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} excursions cleaned, saving session", +i);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            // Initializing elements necessary to import excursions
            // itineraries
            final Iterator<Resource> itinerariesForMapping = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary\"]", "xpath");

            final Map<Integer, List<String>> itinerariesMapping = new HashMap<>();
            while (itinerariesForMapping.hasNext()) {
                final Resource itinerary = itinerariesForMapping.next();

                final Integer itineraryId = itinerary.getValueMap().get("itineraryId", Integer.class);

                if (itineraryId != null) {
                    if (itinerariesMapping.containsKey(itineraryId)) {
                        itinerariesMapping.get(itineraryId).add(itinerary.getPath());
                    } else {
                        final List<String> itineraryPaths = new ArrayList<>();
                        itineraryPaths.add(itinerary.getPath());
                        itinerariesMapping.put(itineraryId, itineraryPaths);
                    }

                    LOGGER.trace("Adding itinerary {} ({}) to cache", itinerary.getPath(), itineraryId);
                }
            }

            // excursions
            final Iterator<Resource> excursionsForMapping = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/excursion\"]", "xpath");

            final Map<Integer, Map<String, String>> excursionsMapping = new HashMap<>();
            while (excursionsForMapping.hasNext()) {
                final Resource excursion = excursionsForMapping.next();

                final Page excursionPage = excursion.getParent().adaptTo(Page.class);
                final String language = LanguageHelper.getLanguage(excursionPage);

                final Integer excursionId = excursion.getValueMap().get("excursionId", Integer.class);

                if (excursionId != null) {
                    if (excursionsMapping.containsKey(excursionId)) {
                        excursionsMapping.get(excursionId).put(language, excursionPage.getPath());
                    } else {
                        final HashMap<String, String> excursionsPaths = new HashMap<>();
                        excursionsPaths.put(language, excursionPage.getPath());
                        excursionsMapping.put(excursionId, excursionsPaths);
                    }

                    LOGGER.trace("Adding excursion {} ({}) with lang {} to cache", excursion.getPath(), excursionId, language);
                }
            }

            // Importing excursions
            List<ShorexItinerary> excursions;
            int itemsWritten = 0;

            do {
                excursions = shorexesApi.shorexesGetItinerary(null, null, null, apiPage, pageSize, null);

                for (ShorexItinerary excursion : excursions) {
                    final Integer excursionId = excursion.getShorexId();

                    LOGGER.trace("importing excursion {} in itinerary {}", excursionId, excursion.getShorexItineraryId());

                    try {
                        final List<String> itineraryPaths = itinerariesMapping.get(excursion.getShorexItineraryId());

                        if (itineraryPaths == null) {
                            throw new ImporterException("Cannot find itinerary with id " + excursion.getShorexItineraryId());
                        }

                        for (String itineraryPath : itineraryPaths) {
                            final Resource itineraryResource = resourceResolver.getResource(itineraryPath);

                            if (itineraryResource == null) {
                                throw new ImporterException("Cannot get itinerary resource " + itineraryPath);
                            }

                            final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                            final Node excursionsNode = JcrUtils.getOrAddNode(itineraryNode, "excursions", "nt:unstructured");

                            if (excursionsNode.hasNode(String.valueOf(excursionId))) {
                                throw new ImporterException("Excursion item already exists");
                            }

                            final Node excursionNode = excursionsNode.addNode(String.valueOf(excursionId));
                            final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                            // associating excursion page
                            if (excursionsMapping.containsKey(excursionId)) {
                                if (excursionsMapping.get(excursionId).containsKey(lang)) {
                                    excursionNode.setProperty("excursionReference", excursionsMapping.get(excursionId).get(lang));
                                }
                            }

                            itineraryNode.setProperty("excursionId", excursionId);
                            itineraryNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/excursion");

                            successNumber++;
                            itemsWritten++;

                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} excursions imported, saving session", +itemsWritten);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }

                            if (size != -1 && itemsWritten >= size) {
                                break;
                            }
                        }
                    } catch (RepositoryException | ImporterException e) {
                        LOGGER.error("Cannot write excursion {}", excursion.getShorexId(), e);

                        errorNumber++;
                    }
                }


                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (excursions.size() > 0 && size != -1 && itemsWritten < size);

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

        LOGGER.debug("Ending excursions import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        // TODO implement
        return null;
    }
}
