package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.importers.servlets.responses.ImportResponseView;
import org.apache.commons.lang3.NotImplementedException;
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
import java.io.PrintWriter;
import java.util.*;

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

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException, ServletException {

        final List<Enum> modes = parseRequest(request, response);

        Map<Enum, ImportResult> results = new HashMap<>();
        for (Enum mode : modes) {
            ImportResult result = RunImporter(mode);
            results.put(mode, result);
        }
        ImportResponseView.buildResponse(response, results);
    }

    private List<Enum> parseRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        final String modeParam = request.getParameter("mode");
        if (modeParam == null) {
            throw new ServletException("the mode parameter must be among the values :\n" + StringUtils.join(Mode.values(), ",\n"));
        }

        final List<Enum> modes = new ArrayList<>();
        String[] modeParams = modeParam.split(",");
        for (String mod : modeParams) {
            modes.add(getMode(mod, response));
        }
        return modes;
    }

    private Mode getMode(String mode, SlingHttpServletResponse response) throws IOException {
        try {
            return Mode.valueOf(mode);
        } catch (IllegalArgumentException e) {
            try(PrintWriter out = response.getWriter()) {
                out.println("Invalid param:" + mode);
            }
            return Mode.dummy;
        }
    }

    private ImportResult RunImporter(Enum mode) throws ServletException {
        try {
            if (mode.equals(Mode.cities)) {
                return citiesImporter.importAllItems();
            } else if (mode.equals(Mode.excursions)) {
                return shoreExcursionsImporter.importAllShoreExcursions();
            } else if (mode.equals(Mode.hotels)) {
                return hotelsImporter.importAllHotels();
            } else if (mode.equals(Mode.landprograms)) {
                return landProgramsImporter.importAllLandPrograms();
            } else if (mode.equals(Mode.exclusiveoffers)) {
                return exclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.countries)) {
                return countriesImporter.importData(false);
            } else if (mode.equals(Mode.features)) {
                return featuresImporter.importAllFeatures();
            } else if (mode.equals(Mode.brochures)) {
                return brochuresImporter.importAllBrochures();
            } else if (mode.equals(Mode.cruises)) {
                return cruisesImporter.importAllItems();
            } else if (mode.equals(Mode.combocruises)) {
                return comboCruisesImporter.importAllItems();
            } else if (mode.equals(Mode.itineraries)) {
                return cruisesItinerariesImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerarieshotels)) {
                return cruisesItinerariesHotelsImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerarieslandprograms)) {
                return cruisesItinerariesLandProgramsImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerariesexcursions)) {
                return cruisesItinerariesExcursionsImporter.importAllItems(false);
            } else if (mode.equals(Mode.prices)) {
                return cruisesPricesImporter.importAllItems(false);
            } else if (mode.equals(Mode.cruisesexclusiveoffers)) {
                return cruisesExclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.agencies)) {
                return agenciesImporter.importAllItems();
            }
            else if (mode.equals(Mode.dummy)){
                return new ImportResult();
            }
        } catch (ImporterException e) {
            throw new ServletException(e);
        }
        throw new NotImplementedException("Mode " + mode + "does not have an importer defined");
    }
}
