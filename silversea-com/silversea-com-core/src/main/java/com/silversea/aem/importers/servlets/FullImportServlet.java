package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
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

        combocruises,
        invalid
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
            throw new ServletException("the mode parameter must be among the values :\n" + StringUtils.join(Mode.values(), ",\n"));
        }

        final List<Mode> modes = new ArrayList<>();
        String[] modeParams = modeParam.split(",");
        for (String mod : modeParams) {
            modes.add(getMode(mod, response));
        }
        Map<Mode, ImportResult> results = new HashMap<>();
        for (Mode mode : modes) {
            ImportResult result = RunFullExtract(mode);
            results.put(mode, result);
        }
        buildResponse(response, results);
    }

    private void buildResponse(SlingHttpServletResponse response, Map<Mode, ImportResult> results) throws IOException {
        // Returning a html doc like this is old fashion.
        // Replace this method, by using RequestDispatcher and a html template.
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
            out.println("<title>Full Import</title></head>");
            out.println("<body>");
            out.println("<h1>Importer Results</h1>");
            out.println("<table>");
            out.println("<tr> <th>Importer mode</th> <th> Success Count</th> <th>Error Count</th></tr>");
            for (Mode mode: results.keySet()) {
                ImportResult result = results.get(mode);
                out.println("<tr>" +
                            "<td>"+ mode +"</td>" +
                            "<td>"+result.getSuccessNumber()+"</td>" +
                            "<td>"+result.getErrorNumber()+ "</td>" +
                            "</tr>");
            }
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            response.setContentType("text/html");
        }
    }

    private Mode getMode(String mod, SlingHttpServletResponse response) throws IOException {

        try {
            return Mode.valueOf(mod);
        } catch (IllegalArgumentException e) {
            try(PrintWriter out = response.getWriter()) {
                out.println("Invalid param:" + mod);
            }
            return Mode.invalid;

        }
    }

    private ImportResult RunFullExtract(Mode mode) throws ServletException {
        ImportResult result = null;
        try {
            if (mode.equals(Mode.cities)) {
                result = citiesImporter.importAllItems();
            } else if (mode.equals(Mode.excursions)) {
                result = shoreExcursionsImporter.importAllShoreExcursions();
            } else if (mode.equals(Mode.hotels)) {
                result = hotelsImporter.importAllHotels();
            } else if (mode.equals(Mode.landprograms)) {
                result = landProgramsImporter.importAllLandPrograms();
            } else if (mode.equals(Mode.exclusiveoffers)) {
                result = exclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.countries)) {
                result = countriesImporter.importData(false);
            } else if (mode.equals(Mode.features)) {
                result = featuresImporter.importAllFeatures();
            } else if (mode.equals(Mode.brochures)) {
                result = brochuresImporter.importAllBrochures();
            } else if (mode.equals(Mode.cruises)) {
                result = cruisesImporter.importAllItems();
            } else if (mode.equals(Mode.combocruises)) {
                result = comboCruisesImporter.importAllItems();
            } else if (mode.equals(Mode.itineraries)) {
                result = cruisesItinerariesImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerarieshotels)) {
                result = cruisesItinerariesHotelsImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerarieslandprograms)) {
                result = cruisesItinerariesLandProgramsImporter.importAllItems(false);
            } else if (mode.equals(Mode.itinerariesexcursions)) {
                result = cruisesItinerariesExcursionsImporter.importAllItems(false);
            } else if (mode.equals(Mode.prices)) {
                result = cruisesPricesImporter.importAllItems(false);
            } else if (mode.equals(Mode.cruisesexclusiveoffers)) {
                result = cruisesExclusiveOffersImporter.importAllItems();
            } else if (mode.equals(Mode.agencies)) {
                result = agenciesImporter.importAllItems();
            }
            else if (mode.equals(Mode.invalid)){
                return new ImportResult();
            }
        } catch (ImporterException e) {
            throw new ServletException(e);
        }
        return result;
    }
}
