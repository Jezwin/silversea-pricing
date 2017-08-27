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

@SlingServlet(paths = "/bin/api-import-diff")
public class UpdateImportServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(UpdateImportServlet.class);

    private enum Mode {
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
    private CitiesImporter citiesImporter;

    @Reference
    private HotelsImporter hotelsImporter;

    @Reference
    private LandProgramsImporter landProgramsImporter;

    @Reference
    private ShoreExcursionsImporter shoreExcursionsImporter;

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
    private ComboCruisesImporter comboCruisesImporter;

    @Reference
    private ExclusiveOffersImporter exclusiveOffersImporter;

    @Reference
    private FeaturesImporter featuresImporter;

    @Reference
    private BrochuresImporter brochuresImporter;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
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
                citiesImporter.updateItems();
            } else if (mode.equals(Mode.hotels)) {
                hotelsImporter.updateHotels();
            } else if (mode.equals(Mode.excursions)) {
                shoreExcursionsImporter.updateShoreExcursions();
            } else if (mode.equals(Mode.landprograms)) {
                landProgramsImporter.updateLandPrograms();
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
            } else if (mode.equals(Mode.combocruises)) {
                //comboCruisesImporter.importData(true);
            } else if (mode.equals(Mode.brochures)) {
                brochuresImporter.updateBrochures();
            }
        } catch (ImporterException e) {
            throw new ServletException(e);
        }

        response.setContentType("text/html");
    }
}
