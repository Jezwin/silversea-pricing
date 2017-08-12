package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.services.*;
import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

@SlingServlet(paths = "/bin/api-import-test")
public class FullImportServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(FullImportServlet.class);

    private enum Mode {
        cities,
        ex,
        hotels,
        lp,
        ta,
        eo,
        ships,
        countries,
        ft,
        brochures,
        cruises,
        cc,
        itineraries,
        itinerarieshotels,
        itinerariesexcursions,
        itinerarieslandprograms,
        prices,
        cruisesexclusiveoffers
    }

    private boolean isRunning = false;

    private StopWatch watch = new StopWatch();
    private StopWatch watchAll = new StopWatch();

    @Reference
    private CitiesImporter citiesImporter;

    @Reference
    private ShoreExcursionsImporter shoreExcursionsImporter;

    @Reference
    private HotelsImporter hotelsImporter;

    @Reference
    private LandProgramsImporter landProgramsImporter;

    @Reference
    private TravelAgenciesImporter travelAgenciesImporter;

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
    private CruisesItinerariesExcursionsImporter cruisesItinerariesExcursionsImporter;

    @Reference
    private CruisesPricesImporter cruisesPricesImporter;

    @Reference
    private CruisesItinerariesLandProgramsImporter cruisesItinerariesLandProgramsImporter;

    @Reference
    private ComboCruisesImporter comboCruisesImporter;

    @Reference
    private CruisesExclusiveOffersImporter cruisesExclusiveOffersImporter;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String timeAll;
        String time;

        final PrintWriter responseWriter = response.getWriter();

        try {
            response.setContentType("text/html");
            responseWriter.write("<html><head><title>Launcher Test Importer</title></head>"
                    + "<body><H1>Initial import...</H1><div class=\'content\'>");
            responseWriter.flush();

            // To Extract
            String modeParam = request.getParameter("mode");
            Boolean all = modeParam == null || "".equals(modeParam) || "ALL".equals(modeParam);
            Mode mode = null;

            String elementsNumberString = request.getParameter("size");
            int elementsNumber = -1;
            try {
                elementsNumber = Integer.valueOf(elementsNumberString);
            } catch (NumberFormatException ignored) {}

            if (!all) {
                try {
                    mode = Mode.valueOf(modeParam);
                } catch (IllegalArgumentException e) {
                    LOGGER.error("the mode parameter must be among the values : {}", StringUtils.join(Mode.values(), ", "));

                    responseWriter.write("the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
                    responseWriter.flush();
                    responseWriter.close();

                    return;
                }
            } else {
                watchAll.reset();
                watchAll.start();
            }
            // End To Extract

            if (!isRunning) {
                isRunning = true;

                int nbrError;
                int nbrSucces;

                if (all || mode.equals(Mode.cities)) {
                    responseWriter.write("Init import of cities ...<br/>");
                    responseWriter.flush();
                    watch.reset();
                    watch.start();
                    ImportResult importResult = citiesImporter.importAllItems();
                    responseWriter.write("Cities import failure number : <p>" + importResult.getErrorNumber() + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("Cities import success number : <p>" + importResult.getSuccessNumber() + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("cities import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    responseWriter.write("Time :<br/>" + time);
                    responseWriter.write("<br/> ---------------- <br />");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.ex)) {
                    responseWriter.write("Init import of shorex ...<br/>");
                    responseWriter.flush();
                    watch.reset();
                    watch.start();
                    ImportResult importResult = shoreExcursionsImporter.importAllShoreExcursions();
                    responseWriter.write("Shorex import failure number : <p>" + importResult.getErrorNumber() + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("Shorex import success number : <p>" + importResult.getSuccessNumber() + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("ShoreExcursions import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    responseWriter.write("Time :<br/>" + time);
                    responseWriter.write("<br/> ---------------- <br />");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.hotels)) {
                    responseWriter.write("Init import of hotels ...<br/>");
                    responseWriter.flush();
                    watch.reset();
                    watch.start();
                    ImportResult importResult = hotelsImporter.importAllHotels();
                    responseWriter.write("Hotels import failure number : <p>" + importResult.getErrorNumber() + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("hotels import success number : <p>" + importResult.getSuccessNumber() + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("Hotels import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    responseWriter.write("Time :<br/>" + time);
                    responseWriter.write("<br/> ---------------- <br />");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.lp)) {
                    responseWriter.write("Init import of lands programs ...<br/>");
                    responseWriter.flush();
                    watch.reset();
                    watch.start();
                    ImportResult importResult = landProgramsImporter.importAllLandPrograms();
                    responseWriter.write("Land program import failure number : <p>" + importResult.getErrorNumber() + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("land program import success number : <p>" + importResult.getSuccessNumber() + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("LandPrograms import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    responseWriter.write("Time :<br/>" + time);
                    responseWriter.write("<br/> ---------------- <br />");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.ta)) {
                    responseWriter.write("Init import of travel agencies ...<br/>");
                    responseWriter.flush();
                    watch.reset();
                    watch.start();
                    travelAgenciesImporter.importData();
                    nbrError = travelAgenciesImporter.getErrorNumber();
                    nbrSucces = travelAgenciesImporter.getSuccesNumber();
                    responseWriter.write("Travel agencies import failure number : <p>" + nbrError + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("Travel agencies import success number : <p>" + nbrSucces + "</p>");
                    responseWriter.write("<br/>");
                    responseWriter.write("TravelAgencies import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    responseWriter.write("Time :<br/>" + time);
                    responseWriter.write("<br/> ---------------- <br />");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.eo)) {
                    responseWriter.write("Init import of exclusives offers ...<br/>");
                    responseWriter.flush();
                    watch.reset();
                    watch.start();
                    final ImportResult importResult = exclusiveOffersImporter.importAllItems();
                    responseWriter.write("Exclusive offers import failure number : <b>" + importResult.getErrorNumber() + "</b>");
                    responseWriter.write("<br/>");
                    responseWriter.write("Exclusives offers import success number : <b>" + importResult.getSuccessNumber() + "</b>");
                    responseWriter.write("<br/>");
                    responseWriter.write("Exclusive offers import Done<br/>");
                    watch.stop();
                    time = watch.toString();
                    responseWriter.write("Time :<br/>" + time);
                    responseWriter.write("<br/> ---------------- <br />");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.countries)) {
                    countriesImporter.importData();
                    responseWriter.write("Countries import Done<br/>");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.ft)) {
                    featuresImporter.importAllFeatures();
                    responseWriter.write("Feature import Done<br/>");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.brochures)) {
                    brochuresImporter.importAllBrochures();
                    responseWriter.write("Brochures import Done<br/>");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.cruises)) {
                    cruisesImporter.importSampleSet(elementsNumber);
                    responseWriter.write("Cruises import Done<br/>");
                    responseWriter.flush();
                }

                if (all || mode.equals(Mode.cc)) {
                    comboCruisesImporter.importData(false);
                    responseWriter.write("Combo cruises Cruises import Done<br/>");
                    responseWriter.flush();
                }

                if (mode.equals(Mode.itineraries)) {
                    cruisesItinerariesImporter.importSampleSet(elementsNumber);
                    responseWriter.write("Itineraries import Done<br/>");
                    responseWriter.flush();
                }

                if (mode.equals(Mode.itinerarieshotels)) {
                    cruisesItinerariesHotelsImporter.importSampleSet(elementsNumber);
                    responseWriter.write("Itineraries hotels import Done<br/>");
                    responseWriter.flush();
                }

                if (mode.equals(Mode.itinerariesexcursions)) {
                    cruisesItinerariesExcursionsImporter.importSampleSet(elementsNumber);
                    responseWriter.write("Itineraries excursions import Done<br/>");
                    responseWriter.flush();
                }

                if (mode.equals(Mode.itinerarieslandprograms)) {
                    cruisesItinerariesLandProgramsImporter.importSampleSet(elementsNumber);
                    responseWriter.write("Itineraries land programs import Done<br/>");
                    responseWriter.flush();
                }

                if (mode.equals(Mode.prices)) {
                    cruisesPricesImporter.importSampleSet(elementsNumber);
                    responseWriter.write("Prices import Done<br/>");
                    responseWriter.flush();
                }

                if (mode.equals(Mode.cruisesexclusiveoffers)) {
                    cruisesExclusiveOffersImporter.importAllItems();
                    responseWriter.write("Cruises/exclusive offers import Done<br/>");
                    responseWriter.flush();
                }

            } else {
                responseWriter.write("<br/>an other import is already running<br />");
                responseWriter.flush();
            }

            if (all) {
                watchAll.stop();
                timeAll = watchAll.toString();
                responseWriter.write("<br/> ---------------- <br />");
                responseWriter.write("Time Global:<br/>" + timeAll);
                responseWriter.write("<br/> ---------------- <br />");
                responseWriter.flush();
            }

            responseWriter.write("<H2>All done.</H2></div></body></html>");
            responseWriter.close();
        } catch (RuntimeException | IOException e) {
            // watchAll.stop();
            timeAll = watchAll.toString();
            responseWriter.write("<br/> ---------------- <br/>");
            responseWriter.write("Time Global:<br/>" + timeAll);
            responseWriter.write("<br/>Finished With Error : " + e.getMessage());
            responseWriter.write("<br/> ---------------- <br />");
            responseWriter.flush();

            LOGGER.error("Error during import", e);
        } finally {
            isRunning = false;
        }
    }
}
