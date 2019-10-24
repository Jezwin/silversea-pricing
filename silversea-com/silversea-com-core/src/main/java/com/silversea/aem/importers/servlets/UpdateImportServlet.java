package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.ImportJobRequest;
import com.silversea.aem.importers.ImportRunner;
import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.internalpages.InternalPageRepository;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.logging.SSCLogger;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.services.GlobalCacheService;
import io.vavr.Lazy;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.silversea.aem.constants.RunModesConstants.AUTHOR;
import static com.silversea.aem.importers.ImportJobRequest.jobRequest;
import static com.silversea.aem.importers.utils.ImportersUtils.getAEMInstanceType;
import static com.silversea.aem.logging.JsonLog.jsonLogWithMessage;

@SlingServlet(paths = "/bin/api-import-diff")
public class UpdateImportServlet extends SlingSafeMethodsServlet {

    public enum Mode {
        brochures,
        ccptgeneration,
        cities,
        citiesDisactive,
        combocruises,
        combocruisessegmentsactivation,
        countries,
        cruises,
        cruisesexclusiveoffers,
        exclusiveoffers,
        excursions,
        excursionsDisactive,
        features,
        hotelImagesGeneration,
        hotels,
        hotelsDisactive,
        importAllPortImages,
        itineraries,
        itinerariesexcursions,
        itinerarieshotels,
        itinerarieslandprograms,
        landProgramsDisactive,
        landprograms,
        multicruises,
        multicruisesitineraries,
        multicruisesitinerariesexcursions,
        multicruisesitinerarieshotels,
        multicruisesitinerarieslandprograms,
        multicruisesprices,
        phonegeneration,
        portsGeneration,
        prices,
        testAliasCruiseAlign,
        dummy
    }

    public enum Replicate {
        all
    }

    public enum Cache {
        FYCCacheRebuild,
        clearGlobalCache,
        stylesconfiguration
    }

    @Reference
    private PortsImporter portsImporter;

    @Reference
    private CitiesImporter citiesImporter;

    @Reference
    private HotelsImporter hotelsImporter;

    @Reference
    private LandProgramsImporter landProgramsImporter;

    @Reference
    private ShoreExcursionsImporter shoreExcursionsImporter;

    @Reference
    private CountriesImporter countriesImporter;

    @Reference
    private CruisesImporter cruisesImporter;

    @Reference
    private CruisesItinerariesHotelsImporter cruisesItinerariesHotelsImporter;

    @Reference
    private CruisesItinerariesLandProgramsImporter cruisesItinerariesLandProgramsImporter;

    @Reference
    private CruisesItinerariesExcursionsImporter cruisesItinerariesExcursionsImporter;

    @Reference
    private CruisesItinerariesImporter cruisesItinerariesImporter;

    @Reference
    private CruisesPricesImporter cruisesPricesImporter;

    @Reference
    private CruisesExclusiveOffersImporter cruisesExclusiveOffersImporter;

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
    private ComboCruisesImporter comboCruisesImporter;

    @Reference
    private ExclusiveOffersImporter exclusiveOffersImporter;

    @Reference
    private FeaturesImporter featuresImporter;

    @Reference
    private BrochuresImporter brochuresImporter;

    @Reference
    private CcptImporter ccptImporter;

    @Reference
    private PhoneImporter phoneImporter;

    @Reference
    private StyleCache styleCache;

    @Reference
    private CruisesCacheService cruisesCacheService;

    @Reference
    private ReplicateImporter replicateImporter;

    @Reference
    private GlobalCacheService globalCacheService;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private LogzLoggerFactory sscLogFactory;

    @Reference
    private CruisesCacheService cruiseCache;

    private Lazy<List<ImportJobRequest>> allImportJobs = Lazy.of(() ->
            List.of(
                    jobRequest(Mode.cities.name(), citiesImporter::updateItems),
                    jobRequest(Mode.citiesDisactive.name(), citiesImporter::DesactivateUselessPort),
                    jobRequest(Mode.hotels.name(), hotelsImporter::updateHotels),
                    jobRequest(Mode.excursions.name(), shoreExcursionsImporter::updateShoreExcursions),
                    jobRequest(Mode.landprograms.name(), landProgramsImporter::updateLandPrograms),
                    jobRequest(Mode.countries.name(), () -> countriesImporter.importData(false)),
                    jobRequest(Mode.exclusiveoffers.name(), exclusiveOffersImporter::importAllItems),
                    jobRequest(Mode.features.name(), featuresImporter::updateFeatures),
                    jobRequest(Mode.cruises.name(), cruisesImporter::updateItems),
                    jobRequest(Mode.itineraries.name(), () -> cruisesItinerariesImporter.importAllItems(true)),
                    jobRequest(Mode.prices.name(), () -> cruisesPricesImporter.importAllItems(true)),
                    jobRequest(Mode.itinerarieshotels.name(), () -> cruisesItinerariesHotelsImporter.importAllItems(true)),
                    jobRequest(Mode.itinerarieslandprograms.name(), () -> cruisesItinerariesLandProgramsImporter.importAllItems(true)),
                    jobRequest(Mode.itinerariesexcursions.name(), () -> cruisesItinerariesExcursionsImporter.importAllItems(true)),
                    jobRequest(Mode.cruisesexclusiveoffers.name(), cruisesExclusiveOffersImporter::importAllItems),
                    jobRequest(Mode.multicruises.name(), multiCruisesImporter::updateItems),
                    jobRequest(Mode.multicruisesitineraries.name(), multiCruisesItinerariesImporter::importAllItems),
                    jobRequest(Mode.multicruisesprices.name(), multiCruisesPricesImporter::importAllItems),
                    jobRequest(Mode.multicruisesitinerarieshotels.name(), multiCruisesItinerariesHotelsImporter::importAllItems),
                    jobRequest(Mode.multicruisesitinerarieslandprograms.name(), multiCruisesItinerariesLandProgramsImporter::importAllItems),
                    jobRequest(Mode.multicruisesitinerariesexcursions.name(), multiCruisesItinerariesExcursionsImporter::importAllItems),
                    jobRequest(Mode.brochures.name(), brochuresImporter::updateBrochures),
                    jobRequest(Mode.ccptgeneration.name(), ccptImporter::importAllItems),
                    jobRequest(Mode.phonegeneration.name(), phoneImporter::importAllItems),
                    jobRequest(Mode.excursionsDisactive.name(), shoreExcursionsImporter::disactiveAllItemDeltaByAPI),
                    jobRequest(Mode.landProgramsDisactive.name(), landProgramsImporter::disactiveAllItemDeltaByAPI),
                    jobRequest(Mode.hotelsDisactive.name(), hotelsImporter::disactiveAllItemDeltaByAPI),
                    jobRequest(Mode.importAllPortImages.name(), citiesImporter::importAllPortImages),
                    jobRequest(Mode.portsGeneration.name(), portsImporter::importAllItems),
                    jobRequest(Mode.hotelImagesGeneration.name(), hotelsImporter::importHotelImages),
                    jobRequest(Mode.testAliasCruiseAlign.name(), cruisesImporter::updateCheckAlias),
                    jobRequest(Mode.combocruisessegmentsactivation.name(), comboCruisesImporter::markSegmentsForActivation),
                    jobRequest(Mode.dummy.name(), ImportResult::new)
            )
    );


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        SSCLogger logger = sscLogFactory.getLogger(UpdateImportServlet.class);

        String currentAemInstanceType = getAEMInstanceType(slingSettingsService);
        logger.logInfo(jsonLogWithMessage("StartUpdateImportServlet","Start Update import servlet on " + currentAemInstanceType));

        List<String> errors = getImportJobs(request).filter(Either::isLeft).map(Either::getLeft);
        List<ImportJobRequest> jobs = getImportJobs(request).filter(Either::isRight).map(Either::get);
        Map<String, ImportResult> results = new HashMap<>();

        if(errors.isEmpty()) {
            new ImportRunner(jobs, logger).run().forEach(report -> results.put(report.name(), report.result()));
            if (currentAemInstanceType.equals(AUTHOR)){
                Replicate(request);
            }
            ClearCache(request, results);
        }

        InternalPageRepository repo = new InternalPageRepository(request.getResourceResolver(), cruiseCache);
        String content = repo.diffImportPage(results, errors).getOrElseGet(Throwable::getMessage);
        response.getWriter().write(content);
        response.setContentType("text/html");

        logger.logInfo(jsonLogWithMessage("UpdateImportComplete","Update import servlet completed on " + currentAemInstanceType));
    }

    private List<Either<String, ImportJobRequest>> getImportJobs(SlingHttpServletRequest request) {
        return Optional
                .ofNullable(request.getParameter("mode"))
                .map(s -> List.of(s.split(",")))
                .orElse(List.empty())
                .map(this::findImporter);
    }

    private Either<String, ImportJobRequest> findImporter(String m) {
        return allImportJobs.get()
                .find(x -> x.name().equals(m))
                .map(Either::right)
                .getOrElse(Either.left("Mode does not exist: " + m))
                .mapLeft(Object::toString);
    }

    private void ClearCache(SlingHttpServletRequest request, Map<String, ImportResult> results) {
        Optional.ofNullable(request.getParameter("cache"))
                .map(p -> List.of(p.split(",")))
                .ifPresent(ps -> ps.forEach(p -> {
                    Cache cache = Cache.valueOf(p);
                    if (cache.equals(Cache.stylesconfiguration)) {
                        results.put(cache.name(), styleCache.buildCache());
                    } else if (cache.equals(Cache.FYCCacheRebuild)) {
                        results.put(cache.name(), cruisesCacheService.buildCruiseCache());
                    } else if (cache.equals(Cache.clearGlobalCache)) {
                        results.put(cache.name(), globalCacheService.clear());
                    }
                }));
    }

    private void Replicate(SlingHttpServletRequest request) {
        final String replicateParam = request.getParameter("replicate");
        if(replicateParam != null && replicateParam.equals(Replicate.all.name())) {
            replicateImporter.replicate();
        }
    }

}
