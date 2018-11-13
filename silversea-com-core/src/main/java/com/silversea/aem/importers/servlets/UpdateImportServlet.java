package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.*;
import com.silversea.aem.services.CruisesCacheService;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

@SlingServlet(paths = "/bin/api-import-diff")
public class UpdateImportServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(UpdateImportServlet.class);

    private enum Mode {
        FYCCacheRebuild,
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
        replicate,
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

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        final String modeParam = request.getParameter("mode");
        if (modeParam == null) {
            throw new ServletException(
                    "the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
        }

        final Mode mode;
        try {
            mode = Mode.valueOf(modeParam);
        } catch (IllegalArgumentException e) {
            throw new ServletException(
                    "the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
        }

        try {
            if (mode.equals(Mode.cities)) {
                citiesImporter.updateItems();
            } else if (mode.equals(Mode.citiesDisactive)) {
                citiesImporter.DesactivateUselessPort();
            } else if (mode.equals(Mode.hotels)) {
                hotelsImporter.updateHotels();
            } else if (mode.equals(Mode.excursions)) {
                shoreExcursionsImporter.updateShoreExcursions();
            } else if (mode.equals(Mode.landprograms)) {
                landProgramsImporter.updateLandPrograms();
            } else if (mode.equals(Mode.countries)) {
                countriesImporter.importData(false);
            } else if (mode.equals(Mode.exclusiveoffers)) {
                exclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.features)) {
                featuresImporter.updateFeatures();
            } else if (mode.equals(Mode.cruises)) {
                cruisesImporter.updateItems();
            } else if (mode.equals(Mode.itineraries)) {
                cruisesItinerariesImporter.importAllItems(true);
            } else if (mode.equals(Mode.prices)) {
                cruisesPricesImporter.importAllItems(true);
            } else if (mode.equals(Mode.itinerarieshotels)) {
                cruisesItinerariesHotelsImporter.importAllItems(true);
            } else if (mode.equals(Mode.itinerarieslandprograms)) {
                cruisesItinerariesLandProgramsImporter.importAllItems(true);
            } else if (mode.equals(Mode.itinerariesexcursions)) {
                cruisesItinerariesExcursionsImporter.importAllItems(true);
            } else if (mode.equals(Mode.cruisesexclusiveoffers)) {
                cruisesExclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.multicruises)) {
                multiCruisesImporter.updateItems();
            } else if (mode.equals(Mode.multicruisesitineraries)) {
                multiCruisesItinerariesImporter.importAllItems();
            } else if (mode.equals(Mode.multicruisesprices)) {
                multiCruisesPricesImporter.importAllItems();
            } else if (mode.equals(Mode.multicruisesitinerarieshotels)) {
                multiCruisesItinerariesHotelsImporter.importAllItems();
            } else if (mode.equals(Mode.multicruisesitinerarieslandprograms)) {
                multiCruisesItinerariesLandProgramsImporter.importAllItems();
            } else if (mode.equals(Mode.multicruisesitinerariesexcursions)) {
                multiCruisesItinerariesExcursionsImporter.importAllItems();
            } else if (mode.equals(Mode.combocruises)) {
                // comboCruisesImporter.importData(true);
            } else if (mode.equals(Mode.brochures)) {
                brochuresImporter.updateBrochures();
            } else if (mode.equals(Mode.combocruisessegmentsactivation)) {
                comboCruisesImporter.markSegmentsForActivation();
            } else if (mode.equals(Mode.ccptgeneration)) {
                ccptImporter.importAllItems();
            } else if (mode.equals(Mode.phonegeneration)) {
                phoneImporter.importAllItems();
            } else if (mode.equals(Mode.stylesconfiguration)) {
                styleCache.buildCache();
            } else if (mode.equals(Mode.excursionsDisactive)) {
                shoreExcursionsImporter.disactiveAllItemDeltaByAPI();
            } else if (mode.equals(Mode.landProgramsDisactive)) {
                landProgramsImporter.disactiveAllItemDeltaByAPI();
            } else if (mode.equals(Mode.hotelsDisactive)) {
                hotelsImporter.disactiveAllItemDeltaByAPI();
            } else if (mode.equals(Mode.importAllPortImages)) {
                citiesImporter.importAllPortImages();
            } else if (mode.equals(Mode.portsGeneration)) {
                portsImporter.importAllItems();
            } else if (mode.equals(Mode.FYCCacheRebuild)) {
                cruisesCacheService.buildCruiseCache();
            } else if (mode.equals(Mode.hotelImagesGeneration)) {
                hotelsImporter.importHotelImages();
            } else if (mode.equals(Mode.replicate)) {
                replicateImporter.replicate();
            }

        } catch (ImporterException e) {
            throw new ServletException(e);
        }

        response.setContentType("text/html");
    }
}
