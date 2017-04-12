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

import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.importers.services.CountriesImporter;
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
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        prepareDoc(response.getWriter());
        
        //To Extract
        String modeParam = request.getParameter("mode");
        Boolean all = modeParam == null || (modeParam != null && ("".equals(modeParam) || "ALL".equals(modeParam))) ;
        Mode mode = null;
        if(!all) {
        	try {
        	mode = Mode.valueOf(modeParam);
        	}
        	catch(IllegalArgumentException e) {
        		LOGGER.error("the mode parameter must be among the values : {}", 
        				StringUtils.join(Mode.values(), ", "));
        		response.getWriter().write("the mode parameter must be among the values : "
        				+ StringUtils.join(Mode.values(), ", "));
        		response.getWriter().flush();
        		response.getWriter().close();
        		return;
        	}
        }
        //End To Extract 
        
        if(all || mode.equals(Mode.cities)) {
        	citiesImporter.importCities();
        	response.getWriter().write("cities import Done<br/>");
        	response.getWriter().flush();
        }
        
        if(all || mode.equals(Mode.ex)) {
        	shoreExcursionsImporter.importShoreExcursions();
        	response.getWriter().write("ShoreExcursions import Done<br/>");
        	response.getWriter().flush();
        }
        
        if(all || mode.equals(Mode.hotels)) {
        	hotelImporter.importHotel();
        	response.getWriter().write("Hotels import Done<br/>");
        	response.getWriter().flush();
        }
        
        if(all || mode.equals(Mode.lp)) {
        	landProgramImporter.importLandProgram();
        	response.getWriter().write("LandPrograms import Done<br/>");
        	response.getWriter().flush();
        }
        
        if(all || mode.equals(Mode.ta)) {
        	travelAgenciesImporter.importTravelAgencies();
        	response.getWriter().write("TravelAgencies import Done<br/>");
        	response.getWriter().flush();
        }
        
        if(all || mode.equals(Mode.eo)) {
        	exclusiveOffersImporter.importExclusiveOffers();
        	response.getWriter().write("ExclusiveOffers import Done<br/>");
        	response.getWriter().flush();
        }
        
        if(all || mode.equals(Mode.countries)) {
        	countriesImporter.importCountries();
        	response.getWriter().write("Countries import Done<br/>");
        	response.getWriter().flush();
        }
        
        if(all || mode.equals(Mode.ships)) {
        	shipsImporter.importShips();
        	response.getWriter().write("Ships import Done<br/>");
        	response.getWriter().flush();
        }
        
        if(all || mode.equals(Mode.ft)) {
        	featuresImporter.importFeatures();
        	response.getWriter().write("Feature import Done<br/>");
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
    	cities,ex,hotels,lp,ta,eo,countries,ft,ships;
    	
    }
}
