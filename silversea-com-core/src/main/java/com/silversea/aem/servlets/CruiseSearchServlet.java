package com.silversea.aem.servlets;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.components.beans.SearchParameter;
import com.silversea.aem.components.beans.SearchResultData;
import com.silversea.aem.services.CruiseSearchService;
import com.silversea.aem.services.GeolocationService;
import com.silversea.aem.technical.json.JsonMapper;

@SlingServlet(methods="GET", paths="/bin/cruises/search")
public class CruiseSearchServlet extends ScServlet{
    
    private static final long serialVersionUID = 1L;
    private static final  Logger LOGGER = LoggerFactory.getLogger(CruiseSearchServlet.class);
    
    @Reference
    private CruiseSearchService cruiseSearchService;
    
    @Reference
    private GeolocationService geolocationService;

   
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException {
        String parameters = geParametersFromRequest(request);
        GeoLocation geoLocation = geolocationService.initGeolocation(request);
        LOGGER.debug("Cruise search service request -- {}", parameters);
        SearchParameter searchParameter= JsonMapper.getDomainObject(parameters, SearchParameter.class);
        searchParameter.setGeolocation(geoLocation);
        SearchResultData  searchResultData = cruiseSearchService.search(searchParameter);
        //LOGGER.debug("Cruise search service response -- {}", JsonMapper.getJson(searchResultData));
        writeDomainObject(response, searchResultData);
    }

}
