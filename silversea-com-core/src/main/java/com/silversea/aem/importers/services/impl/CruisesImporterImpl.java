package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
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
    public ImportResult importSampleSetCruises(int cruisesNumber) {
        LOGGER.debug("Starting cruises import ({})", cruisesNumber == -1 ? "all" : cruisesNumber);

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
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
            try {
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

                        LOGGER.debug("{} cities imported, saving session", +i);
                    } catch (RepositoryException e) {
                        session.refresh(false);
                    }
                }
            } catch (RepositoryException e) {
                throw new ImporterException("Cannot clean existing cruises");
            }

            // Initializing elements necessary to import cruise
            final Iterator<Resource> destinations = resourceResolver.findResources("/jcr:root/content/silversea-com/en"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/destination\"]", "xpath");

            final Map<String, String> destinationsMapping = new HashMap<>();
            while (destinations.hasNext()) {
                final Resource destination = destinations.next();

                final String destinationCode = destination.getValueMap().get("destinationCode", String.class);

                if (destinationCode != null) {
                    destinationsMapping.put(destinationCode, destination.getParent().getPath());
                }
            }

            // TODO read dynamically langs from content tree
            final List<String> langs = Arrays.asList("en", "de", "fr", "es", "pt-br");

            // Importing cruises
            List<Voyage> cruises;
            int j = 0;

            do {
                cruises = voyagesApi.voyagesGet(null, null, null, null, null, j, pageSize, null, null);

                for (Voyage cruise : cruises) {
                    LOGGER.trace("importing cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

                    // old : [Voyage Code]-[Departure port]-to-[Arrival port]
                    // new : [Departure port]-to(i18n)-[Arrival port]-[Voyage Code]
                    final String pageName = cruise.getVoyageName() + " - " + cruise.getVoyageCod();

                    for (String lang : langs) {

                        // setting alias
                        final String alias;
                        if (lang.equals("en")) {
                            alias = pageName;
                        } else if (lang.equals("fr") || lang.equals("es") || lang.equals("pt-br")) {
                            alias = pageName.replace(" to ", " a ");
                        } else if (lang.equals("de")) {
                            alias = pageName.replace(" to ", " nach ");
                        }


                    }
                }

                j++;
            } while (cruises.size() > 0);

            // Getting paths to import data
            LOGGER.trace("Getting root page : {}", apiConfig.apiRootPath("citiesUrl"));
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final List<String> locales = ImporterUtils.getSiteLocales(pageManager);

        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cruises from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateCruises() {
        return null;
    }
}
