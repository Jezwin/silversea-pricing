package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.*;
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

@SlingServlet(paths = "/bin/api-import-full")
public class FullImportServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(FullImportServlet.class);

    private enum Mode {
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

        combocruises
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

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException, ServletException {
        final String modeParam = request.getParameter("mode");
        if (modeParam == null) {
            throw new ServletException("the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
        }

        final Mode mode;
        try {
            mode = Mode.valueOf(modeParam);
        } catch (IllegalArgumentException e) {
            throw new ServletException("the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
        }

        try {
            if (mode.equals(Mode.cities)) {
                citiesImporter.importAllItems();
            } else if (mode.equals(Mode.excursions)) {
                shoreExcursionsImporter.importAllShoreExcursions();
            } else if (mode.equals(Mode.hotels)) {
                hotelsImporter.importAllHotels();
            } else if (mode.equals(Mode.landprograms)) {
                landProgramsImporter.importAllLandPrograms();
            } else if (mode.equals(Mode.exclusiveoffers)) {
                exclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.countries)) {
                countriesImporter.importData(false);
            } else if (mode.equals(Mode.features)) {
                featuresImporter.importAllFeatures();
            } else if (mode.equals(Mode.brochures)) {
                brochuresImporter.importAllBrochures();
            } else if (mode.equals(Mode.cruises)) {
                cruisesImporter.importAllItems();
            } else if (mode.equals(Mode.combocruises)) {
                comboCruisesImporter.importAllItems();
            } else if (mode.equals(Mode.itineraries)) {
                cruisesItinerariesImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerarieshotels)) {
                cruisesItinerariesHotelsImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerarieslandprograms)) {
                cruisesItinerariesLandProgramsImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerariesexcursions)) {
                cruisesItinerariesExcursionsImporter.importAllItems(false);
            } else if (mode.equals(Mode.prices)) {
                cruisesPricesImporter.importAllItems(false);
            } else if (mode.equals(Mode.cruisesexclusiveoffers)) {
                cruisesExclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.agencies)) {
                agenciesImporter.importAllItems();
            }
        } catch (ImporterException e) {
            throw new ServletException(e);
        }

        response.setContentType("text/html");
    }
}
