package com.silversea.aem.importers.polling;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.services.CruisesCacheService;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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
    private CruisesCacheService cruisesCacheService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CitiesImporter citiesImporter;

    @Reference
    private TravelAgenciesImporter agenciesImporter;

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
    private MultiCruisesImporter multiCruisesImporter;

    @Reference
    private MultiCruisesItinerariesImporter multiCruisesItinerariesImporter;

    @Reference
    private MultiCruisesPricesImporter multiCruisesPricesImporter;

    @Reference
    private MultiCruisesItinerariesHotelsImporter multiCruisesItinerariesHotelsImporter;

    @Reference
    private MultiCruisesItinerariesLandProgramsImporter multiCruisesItinerariesLandProgramsImporter;

    @Reference
    private MultiCruisesItinerariesExcursionsImporter multiCruisesItinerariesExcursionsImporter;

    @Reference
    private CruisesExclusiveOffersImporter cruisesExclusiveOffersImporter;

    @Reference
    private ComboCruisesImporter comboCruisesImporter;

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
                LOGGER.info("Cruises exclusive offers import : {} success, {} errors", importResult.getSuccessNumber(),
                        importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import cruises exclusive offers", e);
            }

            //update multicruise
            importResult = multiCruisesImporter.updateItems();
            LOGGER.info("Multi Cruises import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            importResult = multiCruisesItinerariesImporter.importAllItems();
            LOGGER.info("Multi Cruises itineraries import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            try {
                importResult = multiCruisesPricesImporter.importAllItems();
                LOGGER.info("Multi Cruises prices import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import Multi cruise prices", e);
            }

            try {
                importResult = multiCruisesItinerariesHotelsImporter.importAllItems();
                LOGGER.info("Multi Cruises hotels import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import Multi cruise hotels", e);
            }

            try {
                importResult = multiCruisesItinerariesLandProgramsImporter.importAllItems();
                LOGGER.info("Multi Cruises land programs import : {} success, {} errors", importResult.getSuccessNumber(),
                        importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import Multi cruise land programs", e);
            }

            try {
                importResult = multiCruisesItinerariesExcursionsImporter.importAllItems();
                LOGGER.info("Multi Cruises excursions import : {} success, {} errors", importResult.getSuccessNumber(),
                        importResult.getErrorNumber());
            } catch (ImporterException e) {
                LOGGER.error("Cannot import Multi cruise excursions", e);
            }


            //desactivate port without planned cruises
            importResult = citiesImporter.DesactivateUselessPort();
            LOGGER.info("Cities desactivation : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            //desactivate Shorex not present anymore in the API
            importResult = shoreExcursionsImporter.disactiveAllItemDeltaByAPI();
            LOGGER.info("Shorex desactivation : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            //desactivate Land Program not present anymore in the API
            importResult = landProgramsImporter.disactiveAllItemDeltaByAPI();
            LOGGER.info("Land Programs desactivation : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            //desactivate Hotel not present anymore in the API
            importResult = hotelsImporter.disactiveAllItemDeltaByAPI();
            LOGGER.info("Hotels desactivation : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());


            comboCruisesImporter.markSegmentsForActivation();

            //update travel agencies
            importResult = agenciesImporter.importAllItems();
            LOGGER.info("Agencies import : {} success, {} errors", importResult.getSuccessNumber(), importResult.getErrorNumber());

            // replicate all modifications
            LOGGER.info("Start replication on modified pages");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/dam/silversea-com//element(*,dam:AssetContent)[toDeactivate or toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/en//element(*,cq:PageContent)[toDeactivate or toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/de//element(*,cq:PageContent)[toDeactivate or toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/es//element(*,cq:PageContent)[toDeactivate or toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/pt-br//element(*,cq:PageContent)[toDeactivate or toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/fr//element(*,cq:PageContent)[toDeactivate or toActivate]");
            replicateModifications(resourceResolverFactory, replicator, "/jcr:root/etc/tags//element(*,cq:Tag)[toDeactivate or toActivate]");

            cruisesCacheService.buildCruiseCache();
        } else {
            LOGGER.debug("API updater service run only on author instance");
        }
    }

    /**
     * Replicate all modification done in the update process
     *
     * @param query the query of the resources to replicate
     */
    public static void replicateModifications(ResourceResolverFactory resourceResolverFactory, Replicator replicator, final String query) {
        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            Session session = resourceResolver.adaptTo(Session.class);

            if (session == null) {
                throw new ImporterException("Cannot get session");
            }

            int successNumber = 0, errorNumber = 0;
            int j = 0;

            final Iterator<Resource> resources = resourceResolver.findResources(query, "xpath");

            while (resources.hasNext()) {
                Resource resource = resources.next();
                Node node = resource.adaptTo(Node.class);

                if (node != null) {
                    try {


                        if (node.hasProperty(ImportersConstants.PN_TO_ACTIVATE)
                                && node.getProperty(ImportersConstants.PN_TO_ACTIVATE).getBoolean()) {
                            replicator.replicate(session, ReplicationActionType.ACTIVATE, node.getPath());

                            node.getProperty(ImportersConstants.PN_TO_ACTIVATE).remove();

                            LOGGER.info("{} page activated", node.getPath());
                        }

                        if (node.hasProperty(ImportersConstants.PN_TO_DEACTIVATE)
                                && node.getProperty(ImportersConstants.PN_TO_DEACTIVATE).getBoolean()) {
                            //SSC-2387/SSC-2434
                            //unpublish the entire page if it's not an asset or a tag
                            if (node.getProperty("jcr:primaryType").getString().equals("cq:PageContent")) {
                                final Resource pageResource = resource.getParent();
                                final Node pageNode = pageResource.adaptTo(Node.class);

                                if (pageNode != null) {
                                    replicator.replicate(session, ReplicationActionType.DEACTIVATE, node.getPath());
                                    replicator.replicate(session, ReplicationActionType.DEACTIVATE, pageNode.getPath());
                                }
                            } else {
                                replicator.replicate(session, ReplicationActionType.DEACTIVATE, node.getPath());
                                if (node.getProperty("jcr:primaryType").getString().equals("dam:AssetContent")) {
                                    replicator.replicate(session, ReplicationActionType.DEACTIVATE, node.getParent().getPath());
                                }
                            }

                            node.getProperty(ImportersConstants.PN_TO_DEACTIVATE).remove();

                            LOGGER.info("{} page deactivated", node.getPath());
                        }

                        successNumber++;
                        j++;

                        //Force some wait in replication process to avoid overusing publisher.
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e1) {
                            LOGGER.error("Cannot wait");
                        }

                        if (j % 100 == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.info("{} pages replicated, saving session", +j);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (ReplicationException e) {
                        LOGGER.error("Cannot replicate page {}", node.getPath());

                        errorNumber++;
                    } catch (RepositoryException e) {
                        LOGGER.error("Cannot remove status property on page {}", node.getPath());

                        errorNumber++;
                    }
                    resource = null;
                    node = null;
                }
            }

            try {
                if (session.hasPendingChanges()) {
                    session.save();

                    LOGGER.info("{} pages replicated, saving session", +j);
                }
            } catch (RepositoryException e) {
                try {
                    session.refresh(false);
                } catch (RepositoryException e1) {
                    LOGGER.error("Cannot refresh session");
                }
            }

            LOGGER.info("Replication done, success: {}, errors: {}", successNumber, errorNumber);
            session = null;
        } catch (LoginException | ImporterException | RepositoryException e) {
            LOGGER.error("Cannot get resource resolver or session", e);
        }
    }
}
