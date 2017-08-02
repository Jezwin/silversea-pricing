package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesImporter;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Voyage;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Service
@Component
public class CruisesImporterImpl implements CruisesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

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
    public ImportResult importAllCruises() {
        return importSampleSetCruises(-1);
    }

    @Override
    public ImportResult importSampleSetCruises(final int cruisesNumber) {
        LOGGER.debug("Starting cruises import ({})", cruisesNumber == -1 ? "all" : cruisesNumber);

        int successNumber = 0;
        int errorNumber = 0;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final VoyagesApi voyagesApi = new VoyagesApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing cruises deletion
            LOGGER.debug("Cleaning already imported cruises");

            final Iterator<Resource> existingCruises = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]", "xpath");

            int i = 0;
            while (existingCruises.hasNext()) {
                final Resource cruise = existingCruises.next();

                final Node cruiseNode = cruise.adaptTo(Node.class);

                if (cruiseNode != null) {
                    try {
                        cruiseNode.remove();

                        i++;
                    } catch (RepositoryException e) {
                        LOGGER.error("Cannot remove existing cruise {}", cruise.getPath(), e);
                    }
                }

                if (i % sessionRefresh == 0 && session.hasPendingChanges()) {
                    try {
                        session.save();

                        LOGGER.debug("{} cruises cleaned, saving session", +i);
                    } catch (RepositoryException e) {
                        session.refresh(true);
                    }
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} cruises cleaned, saving session", +i);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            // Initializing elements necessary to import cruise
            final Iterator<Resource> destinations = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/destination\"]", "xpath");

            final Map<String, List<String>> destinationsMapping = new HashMap<>();
            while (destinations.hasNext()) {
                final Resource destination = destinations.next();

                final String destinationId = destination.getValueMap().get("destinationId", String.class);

                if (destinationId != null) {
                    if (destinationsMapping.containsKey(destinationId)) {
                        destinationsMapping.get(destinationId).add(destination.getParent().getPath());
                    } else {
                        final List<String> destinationPaths = new ArrayList<>();
                        destinationPaths.add(destination.getParent().getPath());
                        destinationsMapping.put(destinationId, destinationPaths);
                    }
                }
            }

            final Iterator<Resource> features = resourceResolver.findResources("/jcr:root/etc/tags/features//elment(*,cq:Tag)[featureId]", "xpath");

            final HashMap<Integer, String> featuresMapping = new HashMap<>();
            while (features.hasNext()) {
                final Resource feature = features.next();

                final Integer featureId = feature.getValueMap().get("featureId", Integer.class);

                if (featureId != null) {
                    featuresMapping.put(featureId, feature.getPath());
                }
            }

            // Importing cruises
            List<Voyage> cruises;
            int j = 1, k = 0;

            do {
                cruises = voyagesApi.voyagesGet(null, null, null, null, null, j, pageSize, null, null);

                for (Voyage cruise : cruises) {
                    LOGGER.trace("importing cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

                    // old : [Voyage Code]-[Departure port]-to-[Arrival port]
                    // new : [Departure port]-to(i18n)-[Arrival port]-[Voyage Code]
                    final String pageName = cruise.getVoyageName() + " - " + cruise.getVoyageCod();

                    try {
                        final List<String> destinationPaths = destinationsMapping.get(String.valueOf(cruise.getDestinationId()));

                        if (destinationPaths == null) {
                            throw new ImporterException("Cannot find destination with id " + String.valueOf(cruise.getDestinationId()));
                        }

                        for (String destinationPath : destinationPaths) {
                            final Node destinationNode = session.getNode(destinationPath);

                            final Page cruisePage = pageManager.create(destinationPath,
                                    JcrUtil.createValidChildName(destinationNode, StringHelper.getFormatWithoutSpecialCharcters(pageName)),
                                    WcmConstants.PAGE_TEMPLATE_CRUISE, cruise.getVoyageName(), false);

                            final Resource cruiseContentResource = cruisePage.getContentResource();
                            final Node cruiseContentNode = cruiseContentResource.adaptTo(Node.class);

                            // setting alias
                            if (destinationPath.contains("/fr/") || destinationPath.contains("/es/") || destinationPath.contains("/pt-br/")) {
                                cruiseContentNode.setProperty("sling:alias", JcrUtil.createValidName(pageName.replace(" to ", " a ")));
                            } else if (destinationPath.contains("/de/")) {
                                cruiseContentNode.setProperty("sling:alias", JcrUtil.createValidName(pageName.replace(" to ", " nach ")));
                            }

                            // Adding tags
                            List<String> tagIds = new ArrayList<>();

                            // setting cruise type
                            if (cruise.getIsExpedition() != null && cruise.getIsExpedition()) {
                                tagIds.add("cruise-type:silversea-expedition");
                            } else {
                                tagIds.add("cruise-type:silversea-cruise");
                            }

                            // setting features
                            for (int featureId : cruise.getFeatures()) {
                                if (featuresMapping.containsKey(featureId)) {
                                    tagIds.add(featuresMapping.get(featureId));
                                }
                            }

                            cruiseContentNode.setProperty("cq:tags", tagIds.toArray(new String[tagIds.size()]));

                            // TODO set ship

                            // setting other properties
                            cruiseContentNode.setProperty("apiTitle", cruise.getVoyageName()); // TODO confirm
                            cruiseContentNode.setProperty("importedDescription", cruise.getVoyageDescription()); // TODO confirm
                            cruiseContentNode.setProperty("startDate", cruise.getDepartDate().toGregorianCalendar());
                            cruiseContentNode.setProperty("endDate", cruise.getArriveDate().toGregorianCalendar());
                            cruiseContentNode.setProperty("voyageHighlights", cruise.getVoyageHighlights());
                            cruiseContentNode.setProperty("duration", cruise.getDays());
                            cruiseContentNode.setProperty("cruiseCode", cruise.getVoyageCod());

                            // TODO download or associate map

                            // TODO associate exclusive offer

                            successNumber++;
                            k++;

                            if (k % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} cruises imported, saving session", +k);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }

                            if (k >= cruisesNumber) {
                                break;
                            }
                        }
                    } catch (RepositoryException | WCMException | ImporterException e) {
                        LOGGER.error("Cannot write cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod(), e);

                        errorNumber++;
                    }

                    if (k >= cruisesNumber) {
                        break;
                    }
                }

                j++;
            } while (cruises.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} cruises imported, saving session", +k);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import cruises", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cruises from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending cruises import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateCruises() {
        return null;
    }
}
