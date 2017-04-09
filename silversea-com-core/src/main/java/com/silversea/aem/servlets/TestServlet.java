package com.silversea.aem.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.importers.services.ExclusiveOffersImporter;
import com.silversea.aem.importers.services.HotelImporter;
import com.silversea.aem.importers.services.LandProgramImporter;
import com.silversea.aem.importers.services.ShoreExcursionsImporter;
import com.silversea.aem.importers.services.TravelAgenciesImporter;

@SlingServlet(paths = "/bin/test")
public class TestServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(TestServlet.class);

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

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        citiesImporter.importCities();
        shoreExcursionsImporter.importShoreExcursions();
        hotelImporter.importHotel();
        landProgramImporter.importLandProgram();
        travelAgenciesImporter.importTravelAgencies();
        exclusiveOffersImporter.importExclusiveOffers();

    }
}
