package com.silversea.aem.importers.polling;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.*;
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

@Component(label = "Silversea - API Updater", metatype = true, immediate = true)
@Service(value = Runnable.class)
@Properties({
        @Property(name = "scheduler.expression", value = "0 0 0 * * ?"),
        @Property(name = "scheduler.concurrent", boolValue = false)}
)
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
    private LandProgramsImporter landProgramsImporter;

    @Reference
    private ShoreExcursionsImporter shoreExcursionsImporter;

    @Reference
    private BrochuresImporter brochuresImporter;

    @Reference
    private FeaturesImporter featuresImporter;

    @Reference
    private ExclusiveOffersImporter exclusiveOffersImporter;

    @Reference
    private CruisesImporter cruisesImporter;

    @Reference
    private CruisesItinerariesImporter cruisesItinerariesImporter;

    @Reference
    private CruisesPricesImporter cruisesPricesImporter;

    @Reference
    private CruisesItinerariesHotelsImporter cruisesItinerariesHotelsImporter;

    @Reference
    private CruisesItinerariesLandProgramsImporter cruisesItinerariesLandProgramsImporter;

    @Reference
    private CruisesItinerariesExcursionsImporter cruisesItinerariesExcursionsImporter;

    @Reference
    private CruisesExclusiveOffersImporter cruisesExclusiveOffersImporter;

    @Reference
    private Replicator replicator;

    @Override
    public void run() {
        if (slingSettingsService.getRunModes().contains("author")) {
            LOGGER.info("Running ...");

            // update cities
            ImportResult importResult = citiesImporter.updateItems();
            LOGGER.info("Cities import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            // update hotels
            importResult = hotelsImporter.updateHotels();
            LOGGER.info("Hotels import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            // update land programs
            importResult = landProgramsImporter.updateLandPrograms();
            LOGGER.info("Land programs import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            // update excursions
            importResult = shoreExcursionsImporter.updateShoreExcursions();
            LOGGER.info("Excursions import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            // update brochures
            importResult = brochuresImporter.updateBrochures();
            LOGGER.info("Brochures import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            // update features
            importResult = featuresImporter.updateFeatures();
            LOGGER.info("Features import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            // update exclusive offers
            importResult = exclusiveOffersImporter.importAllItems();
            LOGGER.info("Exclusive offers import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            // update cruises
            importResult = cruisesImporter.updateItems();
            LOGGER.info("Cruises import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            importResult = cruisesItinerariesImporter.importAllItems(true);
            LOGGER.info("Cruises itineraries import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            try {
                importResult = cruisesPricesImporter.importAllItems(true);
                LOGGER.info("Cruises prices import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import cruise prices", e);
            }

            try {
                importResult = cruisesItinerariesHotelsImporter.importAllItems(true);
                LOGGER.info("Cruises hotels import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import cruise hotels", e);
            }

            try {
                importResult = cruisesItinerariesLandProgramsImporter.importAllItems(true);
                LOGGER.info("Cruises land programs import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import cruise land programs", e);
            }

            try {
                importResult = cruisesItinerariesExcursionsImporter.importAllItems(true);
                LOGGER.info("Cruises excursions import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import cruise excursions", e);
            }

            try {
                importResult = cruisesExclusiveOffersImporter.importAllItems();
                LOGGER.info("Cruises exclusive offers import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import cruises exclusive offers", e);
            }

            // replicate all modifications
            LOGGER.info("Start replication on modified pages");

            // TODO get all "silversea/silversea-com/components/pages/combosegment" and mark to activate target of cruiseReference
            // TODO move it to combocruise diff when ok

            // TODO check is properties are correctly removed in case of assets or tags
            replicateModifications("/jcr:root/content/dam/element(*,dam:Asset)[jcr:content/toDeactivate or jcr:content/toActivate]");
            replicateModifications("/jcr:root/content//element(*,cq:Page)[jcr:content/toDeactivate or jcr:content/toActivate]");
            replicateModifications("/jcr:root/content//element(*,cq:Tags)[toDeactivate or toActivate]");

        } else {
            LOGGER.debug("API updater service run only on author instance");
        }
    }

    /**
     * Replicate all modification done in the update process
     *
     * @param query the query of the resources to replicate
     */
    private void replicateModifications(final String query) {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final Session session = resourceResolver.adaptTo(Session.class);

            if (session == null) {
                throw new ImporterException("Cannot get session");
            }

            int successNumber = 0, errorNumber = 0;
            int j = 0;

            Iterator<Resource> resources = resourceResolver.findResources(query, "xpath");

            while (resources.hasNext()) {
                Resource resource = resources.next();

                final Page page = resource.adaptTo(Page.class);

                if (page != null && page.getContentResource() != null) {
                    final ValueMap pageProperties = page.getProperties();
                    final Node pageContentNode = page.getContentResource().adaptTo(Node.class);

                    try {
                        if (pageProperties.get(ImportersConstants.PN_TO_DEACTIVATE, false)) {
                            replicator.replicate(session, ReplicationActionType.DEACTIVATE, page.getPath());

                            pageContentNode.getProperty(ImportersConstants.PN_TO_DEACTIVATE).remove();

                            LOGGER.trace("{} page deactivated", page.getPath());
                        }

                        if (pageProperties.get(ImportersConstants.PN_TO_ACTIVATE, false)) {
                            replicator.replicate(session, ReplicationActionType.ACTIVATE, page.getPath());

                            pageContentNode.getProperty(ImportersConstants.PN_TO_ACTIVATE).remove();

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

                    LOGGER.error("Cannot get page {}", resource.getPath());
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
    }
}
