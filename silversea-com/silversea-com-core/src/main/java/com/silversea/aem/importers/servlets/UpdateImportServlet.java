package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.importers.servlets.responses.ImportResponseView;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.services.GlobalCacheService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@SlingServlet(paths = "/bin/api-import-diff")
public class UpdateImportServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(UpdateImportServlet.class);

    private enum Mode {
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

    private enum Replicate{
        all
    }

    private enum Cache {
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

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        if(!hasParameters(request)){
            String errorMessage = "One or more parameters missing. Please include 'mode', 'replicate' and/or 'cache'\n";
            errorMessage += "\nmode parameter values:\n" +StringUtils.join(Mode.values(), ",\n");
            errorMessage += "\nreplication parameter values:\n" +StringUtils.join(Replicate.values(), ",\n");
            errorMessage += "\ncache parameter values:\n" +StringUtils.join(Cache.values(), ",\n");
            throw new ServletException(errorMessage);
        }
        Map<Enum, ImportResult> results = new HashMap<>();

        if (slingSettingsService.getRunModes().contains("author")) {
            Import(request, response, results);
            Replicate(request);
        }
        ClearCache(request, results);

        ImportResponseView.buildResponse(response, results);
    }

    private void ClearCache(SlingHttpServletRequest request, Map<Enum, ImportResult> results) {
        final String cacheParams = request.getParameter("cache");
        if(cacheParams == null){
            return;
        }
        for (String cacheParam : cacheParams.split(",")) {
            Cache cache = Cache.valueOf(cacheParam);
            if (cache.equals(Cache.stylesconfiguration)) {
                results.put(cache, styleCache.buildCache());
            } else if (cache.equals(Cache.FYCCacheRebuild)) {
                results.put(cache, cruisesCacheService.buildCruiseCache());
            } else if (cache.equals(Cache.clearGlobalCache)) {
                results.put(cache, globalCacheService.clear());
            }
        }
    }

    private void Replicate(SlingHttpServletRequest request) {
        final String replicateParam = request.getParameter("replicate");
        if(replicateParam != null && replicateParam.equals("all")) {
            replicateImporter.replicate();
        }
    }

    private void Import(SlingHttpServletRequest request, SlingHttpServletResponse response, Map<Enum, ImportResult> results) throws IOException, ServletException {
        final String modeParams = request.getParameter("mode");
        if(modeParams == null){
            return;
        }
        for (String modeParam : modeParams.split(",")){
            Mode mode = getMode(modeParam, response);
            ImportResult result = RunImporter(mode);
            results.put(mode, result);
        }
    }

    private boolean hasParameters(SlingHttpServletRequest request) {
        return request.getParameterNames().hasMoreElements();
    }

    private Mode getMode(String mod, SlingHttpServletResponse response) throws IOException {
        try {
            return Mode.valueOf(mod);
        } catch (IllegalArgumentException e) {
            try(PrintWriter out = response.getWriter()) {
                out.println("Invalid param:" + mod);
            }
            return Mode.dummy;
        }
    }

    private ImportResult RunImporter(Mode mode) throws ServletException {
        try {
            if (mode.equals(Mode.cities)) {
                return citiesImporter.updateItems();
            } else if (mode.equals(Mode.citiesDisactive)) {
                return citiesImporter.DesactivateUselessPort();
            } else if (mode.equals(Mode.hotels)) {
                return hotelsImporter.updateHotels();
            } else if (mode.equals(Mode.excursions)) {
                return shoreExcursionsImporter.updateShoreExcursions();
            } else if (mode.equals(Mode.landprograms)) {
                return landProgramsImporter.updateLandPrograms();
            } else if (mode.equals(Mode.countries)) {
                return countriesImporter.importData(false);
            } else if (mode.equals(Mode.exclusiveoffers)) {
                return exclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.features)) {
                return featuresImporter.updateFeatures();
            } else if (mode.equals(Mode.cruises)) {
                return cruisesImporter.updateItems();
            } else if (mode.equals(Mode.itineraries)) {
                return cruisesItinerariesImporter.importAllItems(true);
            } else if (mode.equals(Mode.prices)) {
                return cruisesPricesImporter.importAllItems(true);
            } else if (mode.equals(Mode.itinerarieshotels)) {
                return cruisesItinerariesHotelsImporter.importAllItems(true);
            } else if (mode.equals(Mode.itinerarieslandprograms)) {
                return cruisesItinerariesLandProgramsImporter.importAllItems(true);
            } else if (mode.equals(Mode.itinerariesexcursions)) {
                return cruisesItinerariesExcursionsImporter.importAllItems(true);
            } else if (mode.equals(Mode.cruisesexclusiveoffers)) {
                return cruisesExclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.multicruises)) {
                return multiCruisesImporter.updateItems();
            } else if (mode.equals(Mode.multicruisesitineraries)) {
                return multiCruisesItinerariesImporter.importAllItems();
            } else if (mode.equals(Mode.multicruisesprices)) {
                return multiCruisesPricesImporter.importAllItems();
            } else if (mode.equals(Mode.multicruisesitinerarieshotels)) {
                return multiCruisesItinerariesHotelsImporter.importAllItems();
            } else if (mode.equals(Mode.multicruisesitinerarieslandprograms)) {
                return multiCruisesItinerariesLandProgramsImporter.importAllItems();
            } else if (mode.equals(Mode.multicruisesitinerariesexcursions)) {
                return multiCruisesItinerariesExcursionsImporter.importAllItems();
            } else if (mode.equals(Mode.combocruises)) {
                // comboCruisesImporter.importData(true);
            } else if (mode.equals(Mode.brochures)) {
                return brochuresImporter.updateBrochures();
            } else if (mode.equals(Mode.ccptgeneration)) {
                return ccptImporter.importAllItems();
            } else if (mode.equals(Mode.phonegeneration)) {
                return phoneImporter.importAllItems();
            } else if (mode.equals(Mode.excursionsDisactive)) {
                return shoreExcursionsImporter.disactiveAllItemDeltaByAPI();
            } else if (mode.equals(Mode.landProgramsDisactive)) {
                return landProgramsImporter.disactiveAllItemDeltaByAPI();
            } else if (mode.equals(Mode.hotelsDisactive)) {
                return hotelsImporter.disactiveAllItemDeltaByAPI();
            } else if (mode.equals(Mode.importAllPortImages)) {
                return citiesImporter.importAllPortImages();
            } else if (mode.equals(Mode.portsGeneration)) {
                return portsImporter.importAllItems();
            } else if (mode.equals(Mode.hotelImagesGeneration)) {
                return hotelsImporter.importHotelImages();
            } else if (mode.equals(Mode.testAliasCruiseAlign)) {
                return cruisesImporter.updateCheckAlias();
            } else if (mode.equals(Mode.combocruisessegmentsactivation)) {
                return comboCruisesImporter.markSegmentsForActivation();
            }
            else if(mode.equals(Mode.dummy)){
                return new ImportResult();
            }
        } catch (ImporterException e) {
            throw new ServletException(e);
        }
        throw new NotImplementedException("Mode " + mode + " does not have an importer defined");
    }
}
