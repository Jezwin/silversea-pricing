package com.silversea.aem.importers.polling;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.importers.services.HotelsImporter;
import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.*;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(label = "Silversea - API Updater", metatype = true)
@Service(value = Runnable.class)
@Properties({
        @Property(name = "scheduler.expression", value = "0 * * * * ?"),
        @Property(name = "scheduler.concurrent", boolValue = false)
})
public class ApiUpdater implements Runnable {

    final static private Logger LOGGER = LoggerFactory.getLogger(ApiUpdater.class);

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CitiesImporter citiesImporter;

    @Reference
    private HotelsImporter hotelsImporter;

    @Reference
    private Replicator replicator;

    @Override
    public void run() {
        if (slingSettingsService.getRunModes().contains("author")) {
            LOGGER.info("Running ...");

            // update cities
            final ImportResult importResultCities = citiesImporter.updateCities();
            LOGGER.info("Cities import : {} success, {} errors", importResultCities.getSuccessNumber(), importResultCities.getErrorNumber());

            // update hotels
            final ImportResult importResultHotels = hotelsImporter.updateHotels();
            LOGGER.info("Hotels import : {} success, {} errors", importResultHotels.getSuccessNumber(), importResultHotels.getErrorNumber());

            // TODO update land programs
            // TODO update excursions

            // replicate all modifications
            LOGGER.debug("Start replication on modified pages");

            Map<String, Object> authenticationParams = new HashMap<>();
            authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

            try {
                final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
                final Session session = resourceResolver.adaptTo(Session.class);

                if (session == null) {
                    throw new ImporterException("Cannot get session");
                }

                int successNumber = 0, errorNumber = 0;
                int j = 0;

                Iterator<Resource> resources = resourceResolver
                        .findResources("/jcr:root/content/silversea-com//element(*,cq:Page)[" +
                                "jcr:content/toDeactivate or jcr:content/toActivate]", "xpath");

                while (resources.hasNext()) {
                    Resource resource = resources.next();

                    final Page page = resource.adaptTo(Page.class);

                    if (page != null && page.getContentResource() != null) {
                        final ValueMap pageProperties = page.getProperties();
                        final Node pageContentNode = page.getContentResource().adaptTo(Node.class);

                        try {
                            if (pageProperties.get("toDeactivate", false)) {
                                replicator.replicate(session, ReplicationActionType.DEACTIVATE, page.getPath());

                                pageContentNode.getProperty("toDeactivate").remove();

                                LOGGER.trace("{} page deactivated", page.getPath());
                            }

                            if (pageProperties.get("toActivate", false)) {
                                replicator.replicate(session, ReplicationActionType.ACTIVATE, page.getPath());

                                pageContentNode.getProperty("toActivate").remove();

                                LOGGER.trace("{} page activated", page.getPath());
                            }

                            successNumber++;
                            j++;

                            if (j % 100 == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} pages replicated, saving session", +j);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }
                        } catch (ReplicationException e) {
                            LOGGER.error("Cannot replicate page {}", page.getPath());

                            errorNumber++;
                        } catch (RepositoryException e) {
                            LOGGER.error("Cannot remove status property on page {}", page.getPath());

                            errorNumber++;
                        }
                    } else {
                        errorNumber++;

                        LOGGER.error("Cannot get page {}", page.getPath());
                    }
                }

                try {
                    if (session.hasPendingChanges()) {
                        session.save();

                        LOGGER.debug("{} pages replicated, saving session", +j);
                    }
                } catch (RepositoryException e) {
                    try {
                        session.refresh(false);
                    } catch (RepositoryException e1) {
                        LOGGER.error("Cannot refresh session");
                    }
                }

                LOGGER.info("Replication done, success: {}, errors: {}", successNumber, errorNumber);
            } catch (LoginException | ImporterException e) {
                LOGGER.error("Cannot get resource resolver or session", e);
            }
        } else {
            LOGGER.debug("API updater service run only on author instance");
        }
    }
}