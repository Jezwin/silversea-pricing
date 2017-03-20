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

import com.day.cq.replication.ReplicationException;
import com.silversea.aem.importers.services.CitiesUpdateImporter;
import com.silversea.aem.importers.services.HotelUpdateImporter;
import com.silversea.aem.importers.services.LandProgramUpdateImporter;
import com.silversea.aem.importers.services.ShoreExcursionsUpdateImporter;

@SlingServlet(paths = "/bin/testUpdate")
public class TestUpdateServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(TestUpdateServlet.class);

//    @Reference
//    private CitiesImporter citiesImporter;
//
//    @Reference
//    private ShoreExcursionsImporter shoreExcursionsImporter;
//
//    @Reference
//    private HotelImporter hotelImporter;
//
//    @Reference
//    private LandProgramImporter landProgramImporter;
//
//    @Reference
//    private TravelAgenciesImporter travelAgenciesImporter;
//
//    @Reference
//    private ExclusiveOffersImporter exclusiveOffersImporter;

    @Reference
    private HotelUpdateImporter hotelUpdateImporter;
    
    @Reference
    private ShoreExcursionsUpdateImporter shorexExcursionsUpdateImporter;
    
    @Reference
    private CitiesUpdateImporter citiesUpdateImporter;
    
    @Reference
    private LandProgramUpdateImporter landProgramUpdateImporter;
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        // citiesImporter.importCities();
//        shoreExcursionsImporter.importShoreExcursions();
//        hotelImporter.importHotel();
//        landProgramImporter.importLandProgram();
//        travelAgenciesImporter.importTravelAgencies();
//        exclusiveOffersImporter.importExclusiveOffers();
//        
        try {
            citiesUpdateImporter.importUpdateCities();
        } catch (ReplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        try {
//            hotelUpdateImporter.importUpdateHotel();
//        } catch (ReplicationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        try {
//            shorexExcursionsUpdateImporter.importUpdateShoreExcursions();
//        } catch (ReplicationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//        try {
//            landProgramUpdateImporter.importUpdateLandProgram();
//        } catch (ReplicationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }


    }
}
