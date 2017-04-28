package com.silversea.aem.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.importers.services.BrochuresImporter;
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.importers.services.CountriesImporter;
import com.silversea.aem.importers.services.CruisesImporter;
import com.silversea.aem.importers.services.ExclusiveOffersImporter;
import com.silversea.aem.importers.services.FeaturesImporter;
import com.silversea.aem.importers.services.HotelImporter;
import com.silversea.aem.importers.services.LandProgramImporter;
import com.silversea.aem.importers.services.ShipsImporter;
import com.silversea.aem.importers.services.ShoreExcursionsImporter;
import com.silversea.aem.importers.services.TravelAgenciesImporter;

@SlingServlet(paths = "/bin/api-import-test")
public class TestServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(TestServlet.class);

    private int nbrError = 0;

    private int nbrSucces = 0;

    @Reference
    private CitiesImporter citiesImporter;

    @Reference
    private ShoreExcursionsImporter shoreExcursionsImporter;

    @Reference
    private HotelImporter hotelImporter;

    @Reference
    private LandProgramImporter landProgramImporter;

    @Reference
    private TravelAgenciesImporter travelAgenciesImporter;

    @Reference
    ExclusiveOffersImporter exclusiveOffersImporter;

    @Reference
    CountriesImporter countriesImporter;

    @Reference
    FeaturesImporter featuresImporter;

    @Reference
    ShipsImporter shipsImporter;

    @Reference
    BrochuresImporter brochuresImporter;

    @Reference
    CruisesImporter cruisesImporter;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        prepareDoc(response.getWriter());

        // To Extract
        String modeParam = request.getParameter("mode");
        Boolean all = modeParam == null || (modeParam != null && ("".equals(modeParam) || "ALL".equals(modeParam)));
        Mode mode = null;
        if (!all) {
            try {
                mode = Mode.valueOf(modeParam);
            } catch (IllegalArgumentException e) {
                LOGGER.error("the mode parameter must be among the values : {}", StringUtils.join(Mode.values(), ", "));
                response.getWriter().write(
                        "the mode parameter must be among the values : " + StringUtils.join(Mode.values(), ", "));
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }
        }
        // End To Extract

        if (all || mode.equals(Mode.cities)) {
            citiesImporter.importCities();
            nbrError = citiesImporter.getErrorNumber();
            nbrSucces = citiesImporter.getSuccesNumber();
            response.getWriter().write("Cities import failure number : <p>" + nbrError + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("Cities import succes number : <p>" + nbrSucces + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("cities import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.ex)) {
            shoreExcursionsImporter.importShoreExcursions();
            nbrError = shoreExcursionsImporter.getErrorNumber();
            nbrSucces = shoreExcursionsImporter.getSuccesNumber();
            response.getWriter().write("Shorex import failure number : <p>" + nbrError + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("Shorex import succes number : <p>" + nbrSucces + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("ShoreExcursions import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.hotels)) {
            hotelImporter.importHotel();
            nbrError = hotelImporter.getErrorNumber();
            nbrSucces = hotelImporter.getSuccesNumber();
            response.getWriter().write("Hotels import failure number : <p>" + nbrError + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("hotels import succes number : <p>" + nbrSucces + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("Hotels import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.lp)) {
            landProgramImporter.importLandProgram();
            nbrError = landProgramImporter.getErrorNumber();
            nbrSucces = landProgramImporter.getSuccesNumber();
            response.getWriter().write("Land program import failure number : <p>" + nbrError + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("land program import succes number : <p>" + nbrSucces + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("LandPrograms import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.ta)) {
            travelAgenciesImporter.importTravelAgencies();
            nbrError = travelAgenciesImporter.getErrorNumber();
            nbrSucces = travelAgenciesImporter.getSuccesNumber();
            response.getWriter().write("Travel agencies import failure number : <p>" + nbrError + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("Travel agencies import succes number : <p>" + nbrSucces + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("TravelAgencies import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.eo)) {
            exclusiveOffersImporter.importExclusiveOffers();
            nbrError = exclusiveOffersImporter.getErrorNumber();
            nbrSucces = exclusiveOffersImporter.getSuccesNumber();
            response.getWriter().write("Exclusive offers import failure number : <p>" + nbrError + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("Exclusives offers import succes number : <p>" + nbrSucces + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("ExclusiveOffers import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.ships)) {
            shipsImporter.importShips();
            nbrError = shipsImporter.getErrorNumber();
            nbrSucces = shipsImporter.getSuccesNumber();
            response.getWriter().write("Ships import Done<br/>");
            response.getWriter().write("Ships import failure number : <p>" + nbrError + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().write("Ships import succes number : <p>" + nbrSucces + "</p>");
            response.getWriter().write("<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.countries)) {
            countriesImporter.importCountries();
            response.getWriter().write("Countries import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.ft)) {
            featuresImporter.importFeatures();
            response.getWriter().write("Feature import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.brochures)) {
            brochuresImporter.importBrochures();
            response.getWriter().write("Brochures import Done<br/>");
            response.getWriter().flush();
        }

        if (all || mode.equals(Mode.cruises)) {
            cruisesImporter.importCruises();
            response.getWriter().write("Cruises import Done<br/>");
            response.getWriter().flush();
        }

        closeDocument(response.getWriter());

    }

    private void prepareDoc(PrintWriter writer) {
        writer.write("<html><head><title>Launcher Test Importer</title></head>"
                + "<body><H1>Initial import...</H1><div class=\'content\'>");
        writer.flush();
    }

    private void closeDocument(PrintWriter writer) {

        writer.write("<H2>All done.</H2></div></body></html>");
        writer.close();
    }

    enum Mode {
        cities, ex, hotels, lp, ta, eo, countries, ft, ships, brochures, cruises;

    }
}
