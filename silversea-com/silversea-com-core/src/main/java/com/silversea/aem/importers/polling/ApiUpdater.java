package com.silversea.aem.importers.polling;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.silversea.aem.importers.*;
import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.logging.LogzLogger;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.logging.SSCLogger;
import com.silversea.aem.services.CruisesCacheService;

import io.vavr.collection.List;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;
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

import static com.silversea.aem.importers.ImportJobRequest.jobRequest;

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

    @Reference
    private LogzLoggerFactory sscLogFactory;

    @Override
    public void run() {
        SSCLogger sscLog = sscLogFactory.getLogger(ApiUpdater.class);

        if (slingSettingsService.getRunModes().contains("author")) {
            LOGGER.info("Running ...");
            List<ImportJobRequest> jobs = buildImportSchedule();
            ImportRunner runner = new ImportRunner(jobs, sscLog);
            runner.run();
            // replicate all modifications
            LOGGER.info("Start replication on modified pages");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/dam/silversea-com//element(*,dam:AssetContent)[toDeactivate or toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/en//element(*,cq:PageContent)[toDeactivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/de//element(*,cq:PageContent)[toDeactivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/es//element(*,cq:PageContent)[toDeactivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/pt-br//element(*,cq:PageContent)[toDeactivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/fr//element(*,cq:PageContent)[toDeactivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/en//element(*,cq:PageContent)[toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/de//element(*,cq:PageContent)[toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/es//element(*,cq:PageContent)[toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/pt-br//element(*,cq:PageContent)[toActivate]");
            replicateModifications(resourceResolverFactory, replicator,
                    "/jcr:root/content/silversea-com/fr//element(*,cq:PageContent)[toActivate]");
            replicateModifications(resourceResolverFactory, replicator, "/jcr:root/etc/tags//element(*,cq:Tag)[toDeactivate or toActivate]");
            cruisesCacheService.buildCruiseCache();
        } else {
            LOGGER.debug("API updater service run only on author instance");
        }
    }

    private List<ImportJobRequest> buildImportSchedule() {
        return List.of(
            jobRequest("CitiesUpdate", citiesImporter::updateItems),
            jobRequest("HotelsUpdate", hotelsImporter::updateHotels),
            jobRequest("LandProgramsUpdate", landProgramsImporter::updateLandPrograms),
            jobRequest("ShorexesUpdate", shoreExcursionsImporter::updateShoreExcursions),
            jobRequest("BrochuresUpdate", brochuresImporter::updateBrochures),
            jobRequest("FeaturesUpdate", featuresImporter::updateFeatures),
            jobRequest("ExclusiveOffersFull", exclusiveOffersImporter::importAllItems),
            jobRequest("CruisesUpdate", cruisesImporter::updateItems),
            jobRequest("CruisesItinerariesUpdate", () -> cruisesItinerariesImporter.importAllItems(true)),
            jobRequest("CruisesPricesUpdate", () -> cruisesPricesImporter.importAllItems(true)),
            jobRequest("CruisesHotelsUpdate", () -> cruisesItinerariesHotelsImporter.importAllItems(true)),
            jobRequest("CruisesLandProgramsUpdate", () -> cruisesItinerariesLandProgramsImporter.importAllItems(true)),
            jobRequest("CruisesExcursionsUpdate", () -> cruisesItinerariesExcursionsImporter.importAllItems(true)),
            jobRequest("CruiseExclusiveOffersFull", cruisesExclusiveOffersImporter::importAllItems),
            jobRequest("MultiCruiseUpdate", multiCruisesImporter::updateItems),
            jobRequest("MultiCruiseItinerariesFull", multiCruisesItinerariesImporter::importAllItems),
            jobRequest("MultiCruisesPricesFull", multiCruisesPricesImporter::importAllItems),
            jobRequest("MultiCruisesHotelsFull", multiCruisesItinerariesHotelsImporter::importAllItems),
            jobRequest("MultiCruisesLandProgramsFull", multiCruisesItinerariesLandProgramsImporter::importAllItems),
            jobRequest("MultiCruisesExcursions", multiCruisesItinerariesExcursionsImporter::importAllItems),
            jobRequest("CitiesDeactivate", citiesImporter::DesactivateUselessPort),
            jobRequest("ShorexesDeactivate", shoreExcursionsImporter::disactiveAllItemDeltaByAPI),
            jobRequest("LandProgramsDeactivate", landProgramsImporter::disactiveAllItemDeltaByAPI),
            jobRequest("HotelsDeactivate", hotelsImporter::disactiveAllItemDeltaByAPI),
            jobRequest("CruiseAliasUpdate", cruisesImporter::updateCheckAlias),
            jobRequest("CruisesMarkActivation", () ->  { comboCruisesImporter.markSegmentsForActivation(); return new ImportResult(0, 0);}),
            jobRequest("AgenciesFull", agenciesImporter::importAllItems)
        );
    }

    /**
     * Replicate all modification done in the update process
     *
     * @param query the query of the resources to replicate
     */
    public static void replicateModifications(ResourceResolverFactory resourceResolverFactory,
                                                      Replicator replicator, final String query) {

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            Session session = resourceResolver.adaptTo(Session.class);

            if (session == null) {
                throw new ImporterException("Cannot get session");
            }

            int j = 0;
            int success = 0, error = 0;

            final Iterator<Resource> resources = resourceResolver.findResources(query, "xpath");

            while (resources.hasNext()) {
                Resource resource = resources.next();
                Node node = resource.adaptTo(Node.class);

                if (node != null) {
                    try {


                        if (node.hasProperty(ImportersConstants.PN_TO_ACTIVATE)
                                && node.getProperty(ImportersConstants.PN_TO_ACTIVATE).getBoolean()) {
                            replicator.replicate(session, ReplicationActionType.ACTIVATE, node.getPath());

                            if (node.getProperty("jcr:primaryType").getString().equals("dam:AssetContent")) {
                                replicator.replicate(session, ReplicationActionType.ACTIVATE, node.getParent().getPath());
                            }

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

                            if (node.hasProperty(ImportersConstants.PN_TO_ACTIVATE)){
                                node.getProperty(ImportersConstants.PN_TO_ACTIVATE).remove();
                            }

                            LOGGER.info("{} page deactivated", node.getPath());
                        }

                        success++;
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

                        error++;
                    } catch (RepositoryException e) {
                        LOGGER.error("Cannot remove status property on page {}", node.getPath());
                        error++;
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

            LOGGER.info("Replication done, success: {}, errors: {}", success, error);
            session = null;
        } catch (LoginException | ImporterException | RepositoryException e) {
            LOGGER.error("Cannot get resource resolver or session", e);
        }
    }
}
