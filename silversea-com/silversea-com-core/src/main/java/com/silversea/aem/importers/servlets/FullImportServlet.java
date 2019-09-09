package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.ImportJobRequest;
import com.silversea.aem.importers.ImportRunner;
import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.internalpages.InternalPageRepository;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.logging.SSCLogger;
import com.silversea.aem.services.CruisesCacheService;
import io.vavr.control.Either;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import io.vavr.collection.List;

import static com.silversea.aem.importers.ImportJobRequest.jobRequest;
import static java.util.stream.Collectors.toList;

@SlingServlet(paths = "/bin/api-import-full")
public class FullImportServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(FullImportServlet.class);

    public enum Mode {
        agencies,
        cities,
        excursions,
        hotels,
        landprograms,

        countries,
        features,
        brochures,

        exclusiveoffers,

        cruises,
        itineraries,
        itinerarieshotels,
        itinerariesexcursions,
        itinerarieslandprograms,
        prices,
        cruisesexclusiveoffers,

        combocruises,
        dummy
    }

    @Reference
    private TravelAgenciesImporter agenciesImporter;

    @Reference
    private CitiesImporter citiesImporter;

    @Reference
    private ShoreExcursionsImporter shoreExcursionsImporter;

    @Reference
    private HotelsImporter hotelsImporter;

    @Reference
    private LandProgramsImporter landProgramsImporter;

    @Reference
    private ExclusiveOffersImporter exclusiveOffersImporter;

    @Reference
    private CountriesImporter countriesImporter;

    @Reference
    private FeaturesImporter featuresImporter;

    @Reference
    private BrochuresImporter brochuresImporter;

    @Reference
    private CruisesImporter cruisesImporter;

    @Reference
    private CruisesItinerariesImporter cruisesItinerariesImporter;

    @Reference
    private CruisesItinerariesHotelsImporter cruisesItinerariesHotelsImporter;

    @Reference
    private CruisesItinerariesLandProgramsImporter cruisesItinerariesLandProgramsImporter;

    @Reference
    private CruisesItinerariesExcursionsImporter cruisesItinerariesExcursionsImporter;

    @Reference
    private CruisesPricesImporter cruisesPricesImporter;

    @Reference
    private ComboCruisesImporter comboCruisesImporter;

    @Reference
    private CruisesExclusiveOffersImporter cruisesExclusiveOffersImporter;

    @Reference
    private LogzLoggerFactory sscLogFactory;

    @Reference
    private CruisesCacheService cruiseCache;

    private List<ImportJobRequest> allImportJobs;

    private List<ImportJobRequest> allImportJobs() {
        if (allImportJobs ==  null) {
            allImportJobs = List.of(
                    jobRequest(Mode.cities.name(), citiesImporter::importAllItems),
                    jobRequest(Mode.excursions.name(), shoreExcursionsImporter::importAllShoreExcursions),
                    jobRequest(Mode.hotels.name(), hotelsImporter::importAllHotels),
                    jobRequest(Mode.landprograms.name(), landProgramsImporter::importAllLandPrograms),
                    jobRequest(Mode.exclusiveoffers.name(), exclusiveOffersImporter::importAllItems),
                    jobRequest(Mode.countries.name(), () -> countriesImporter.importData(false)),
                    jobRequest(Mode.features.name(), featuresImporter::importAllFeatures),
                    jobRequest(Mode.brochures.name(), brochuresImporter::importAllBrochures),
                    jobRequest(Mode.cruises.name(), cruisesImporter::importAllItems),
                    jobRequest(Mode.combocruises.name(), comboCruisesImporter::importAllItems),
                    jobRequest(Mode.itineraries.name(), () -> cruisesItinerariesImporter.importAllItems(false)),
                    jobRequest(Mode.itinerarieshotels.name(), () -> cruisesItinerariesHotelsImporter.importAllItems(false)),
                    jobRequest(Mode.itinerarieslandprograms.name(), () -> cruisesItinerariesLandProgramsImporter.importAllItems(false)),
                    jobRequest(Mode.itinerariesexcursions.name(), () -> cruisesItinerariesExcursionsImporter.importAllItems(false)),
                    jobRequest(Mode.prices.name(), () -> cruisesPricesImporter.importAllItems(false)),
                    jobRequest(Mode.cruisesexclusiveoffers.name(), cruisesExclusiveOffersImporter::importAllItems),
                    jobRequest(Mode.agencies.name(), agenciesImporter::importAllItems),
                    jobRequest(Mode.dummy.name(), ImportResult::new)
            );
        }
        return allImportJobs;
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        SSCLogger logger = sscLogFactory.getLogger(UpdateImportServlet.class);
        List<String> errors = getImportJobs(request).filter(Either::isLeft).map(Either::getLeft);
        List<ImportJobRequest> jobs = getImportJobs(request).filter(Either::isRight).map(Either::get);
        Map<String, ImportResult> results = new HashMap<>();

        if (errors.isEmpty()) {
            new ImportRunner(jobs, logger).run().forEach(report -> results.put(report.name(), report.result()));
        }

        InternalPageRepository repo = new InternalPageRepository(request.getResourceResolver(), cruiseCache);
        String content = repo.fullImportPage(results, errors).getOrElseGet(Throwable::getMessage);
        response.getWriter().write(content);
        response.setContentType("text/html");
    }

    private List<Either<String, ImportJobRequest>> getImportJobs(SlingHttpServletRequest request) {
        return Optional
                .ofNullable(request.getParameter("mode"))
                .map(s -> List.of(s.split(",")))
                .orElse(List.empty())
                .map(this::findImporter);
    }

    private Either<String, ImportJobRequest> findImporter(String m) {
        return allImportJobs()
                .find(x -> x.name().equals(m))
                .map(Either::right)
                .getOrElse(Either.left("Mode does not exist: " + m))
                .mapLeft(Object::toString);
    }

}
