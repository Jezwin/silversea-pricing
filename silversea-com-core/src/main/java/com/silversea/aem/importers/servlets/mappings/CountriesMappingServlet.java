package com.silversea.aem.importers.servlets.mappings;

import com.silversea.aem.importers.services.CountriesImporter;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONObject;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author aurelienolivier
 */
@SlingServlet(paths = "/bin/countries-mapping", extensions = "json")
public class CountriesMappingServlet extends SlingSafeMethodsServlet {

    @Reference
    private CountriesImporter countriesImporter;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        JSONObject countriesMapping = countriesImporter.getCountriesMapping();

        response.setContentType("application/json");
        response.getWriter().write(countriesMapping.toString());
    }
}
