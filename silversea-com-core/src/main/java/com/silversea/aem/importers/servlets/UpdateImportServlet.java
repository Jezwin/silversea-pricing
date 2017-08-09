package com.silversea.aem.importers.servlets;

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

@SlingServlet(paths = "/bin/api-update-import")
public class UpdateImportServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(UpdateImportServlet.class);

    @Reference
    private CitiesImporter updateImportCities;

    @Reference
    private HotelsImporter updateImportHotel;

    @Reference
    private LandProgramsImporter updateImportLandProgram;

    @Reference
    private ShoreExcursionsImporter updateImportShoreExcursion;

    @Reference
    private CruisesImporter cruisesImporter;

    @Reference
    private ComboCruisesImporter ComboCruisesImporter;

    @Reference
    private TravelAgenciesUpdateImporter updateTravalAgencies;

    @Reference
    private ExclusiveOffersUpdateImporter updateExclusiveOffers;

    @Reference
    private FeaturesImporter updateFeatures;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        // To Extract
        final String modeParam = request.getParameter("mode");

        if (modeParam == null) {
            throw new ServletException("the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
        }

        Mode mode;

        try {
            mode = Mode.valueOf(modeParam);
        } catch (IllegalArgumentException e) {
            throw new ServletException("the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
        }

        if (mode.equals(Mode.cities)) {
            updateImportCities.updateItems();
        } else if (mode.equals(Mode.hotels)) {
            updateImportHotel.updateHotels();
        } else if (mode.equals(Mode.excursions)) {
            updateImportShoreExcursion.updateShoreExcursions();
        } else if (mode.equals(Mode.landprograms)) {
            updateImportLandProgram.updateLandPrograms();
        } else if (mode.equals(Mode.travelagencies)) {
            updateTravalAgencies.updateImporData();
        } else if (mode.equals(Mode.exclusiveoffers)) {
            updateExclusiveOffers.updateImporData();
        } else if (mode.equals(Mode.features)) {
            updateFeatures.updateFeatures();
        } else if (mode.equals(Mode.cruises)) {
            cruisesImporter.updateItems();
            response.getWriter().write("Cruises import Done<br/>");
        } else if (mode.equals(Mode.combocruise)) {
            ComboCruisesImporter.importData(true);
        }
    }

    enum Mode {
        cities ("cities"),
        excursions ("excursions"),
        hotels ("hotels"),
        landprograms ("land-programs"),
        travelagencies ("travel-agencies"),
        exclusiveoffers ("exclusive-offers"),
        countries ("countries"),
        features ("features"),
        brochures ("brochures"),
        cruises ("cruises"),
        combocruise ("combo-cruises");

        private String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
