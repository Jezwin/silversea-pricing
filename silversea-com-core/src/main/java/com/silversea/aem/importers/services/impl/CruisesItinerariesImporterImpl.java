package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesImporter;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.ItinerariesApi;
import io.swagger.client.model.Itinerary;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.mime.MimeTypeService;
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
public class CruisesItinerariesImporterImpl implements CruisesItinerariesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesItinerariesImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private MimeTypeService mimeTypeService;

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
    public ImportResult importAllItems() {
        return importSampleSet(-1);
    }

    @Override
    public ImportResult importSampleSet(int size) {
        LOGGER.debug("Starting itineraries import ({})", size == -1 ? "all" : size);

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

            final ItinerariesApi itinerariesApi = new ItinerariesApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing itineraries deletion
            LOGGER.debug("Cleaning already imported itineraries");

            final Iterator<Resource> existingItineraries = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary\"]", "xpath");

            int i = 0;
            while (existingItineraries.hasNext()) {
                final Resource itinerary = existingItineraries.next();

                final Node itineraryNode = itinerary.adaptTo(Node.class);

                if (itineraryNode != null) {
                    try {
                        itineraryNode.remove();

                        i++;
                    } catch (RepositoryException e) {
                        LOGGER.error("Cannot remove existing itinerary {}", itinerary.getPath(), e);
                    }
                }

                if (i % sessionRefresh == 0 && session.hasPendingChanges()) {
                    try {
                        session.save();

                        LOGGER.debug("{} itineraries cleaned, saving session", +i);
                    } catch (RepositoryException e) {
                        session.refresh(true);
                    }
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} itineraries cleaned, saving session", +i);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            // Initializing elements necessary to import itineraries
            // cruises
            final Iterator<Resource> cruises = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]", "xpath");

            final Map<Integer, Map<String, String>> cruisesMapping = new HashMap<>();
            while (cruises.hasNext()) {
                final Resource cruise = cruises.next();

                final Page cruisePage = cruise.getParent().adaptTo(Page.class);
                final String language = LanguageHelper.getLanguage(cruisePage);

                final Integer cruiseId = cruise.getValueMap().get("cruiseId", Integer.class);

                if (cruiseId != null) {
                    if (cruisesMapping.containsKey(cruiseId)) {
                        cruisesMapping.get(cruiseId).put(language, cruisePage.getPath());
                    } else {
                        final HashMap<String, String> shipPaths = new HashMap<>();
                        shipPaths.put(language, cruisePage.getPath());
                        cruisesMapping.put(cruiseId, shipPaths);
                    }

                    LOGGER.trace("Adding cruise {} ({}) with lang {} to cache", cruise.getPath(), cruiseId, language);
                }
            }

            // ports
            final Iterator<Resource> ports = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "xpath");

            final Map<Integer, Map<String, String>> portsMapping = new HashMap<>();
            while (ports.hasNext()) {
                final Resource port = ports.next();

                final Page portPage = port.getParent().adaptTo(Page.class);
                final String language = LanguageHelper.getLanguage(portPage);

                final Integer portId = port.getValueMap().get("cityId", Integer.class);

                if (portId != null) {
                    if (portsMapping.containsKey(portId)) {
                        portsMapping.get(portId).put(language, portPage.getPath());
                    } else {
                        final HashMap<String, String> portPaths = new HashMap<>();
                        portPaths.put(language, portPage.getPath());
                        portsMapping.put(portId, portPaths);
                    }

                    LOGGER.trace("Adding port {} ({}) with lang {} to cache", port.getPath(), portId, language);
                }
            }

            // Importing itineraries
            List<Itinerary> itineraries;
            int itemsWritten = 0;

            do {
                itineraries = itinerariesApi.itinerariesGet("2015-01-01", "2025-12-31", null, null, apiPage, pageSize, null);

                for (Itinerary itinerary : itineraries) {
                    LOGGER.trace("importing itinerary {} for cruise {}", itinerary.getItineraryId(), itinerary.getVoyageId());

                    try {
                        final Map<String, String> cruisePaths = cruisesMapping.get(itinerary.getVoyageId());

                        if (cruisePaths == null) {
                            throw new ImporterException("Cannot find cruise with id " + itinerary.getVoyageId());
                        }

                        for (Map.Entry<String, String> cruisePath : cruisePaths.entrySet()) {
                            final Node cruiseContentNode = session.getNode(cruisePath.getValue() + "/jcr:content");

                            LOGGER.trace("Adding itinerary {} under cruise {}", itinerary.getItineraryId(), cruisePath.getValue());

                            final Node itinerariesNode = JcrUtils.getOrAddNode(cruiseContentNode, "itineraries", "nt:unstructured");

                            if (itinerariesNode.hasNode(String.valueOf(itinerary.getItineraryId()))) {
                                throw new ImporterException("Itinerary item already exists");
                            }

                            final Node itineraryNode = itinerariesNode.addNode(String.valueOf(itinerary.getItineraryId()));
                            itineraryNode.setProperty("itineraryId", itinerary.getItineraryId());
                            itineraryNode.setProperty("date", itinerary.getItineraryDate().toGregorianCalendar());
                            itineraryNode.setProperty("arriveTime", itinerary.getArriveTime());
                            itineraryNode.setProperty("arriveAmPm", itinerary.getArriveTimeAmpm());
                            itineraryNode.setProperty("departTime", itinerary.getDepartTime());
                            itineraryNode.setProperty("departAmPm", itinerary.getDepartTimeAmpm());
                            itineraryNode.setProperty("overnight", itinerary.getIsOvernight());
                            itineraryNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary");

                            // associating port page
                            final Integer cityId = itinerary.getCityId();
                            if (portsMapping.containsKey(cityId)) {
                                if (portsMapping.get(cityId).containsKey(cruisePath.getKey())) {
                                    itineraryNode.setProperty("portReference", portsMapping.get(cityId).get(cruisePath.getKey()));
                                }
                            }

                            successNumber++;
                            itemsWritten++;

                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} itineraries imported, saving session", +itemsWritten);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }

                            if (size != -1 && itemsWritten >= size) {
                                break;
                            }
                        }
                    } catch (RepositoryException | ImporterException e) {
                        LOGGER.error("Cannot write itinerary {}", itinerary.getItineraryId(), e);

                        errorNumber++;
                    }

                }

                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (itineraries.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} itineraries imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import itineraries", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read itineraries from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending itineraries import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        // TODO implement
        return null;
    }
}
