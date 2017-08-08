package com.silversea.aem.importers.servlets.mappings;

import com.silversea.aem.importers.services.CruisesImporter;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONObject;

import javax.servlet.ServletException;
import java.io.IOException;

@SlingServlet(paths = "/bin/cruises-mapping", extensions = "json")
public class CruisesMappingServlet extends SlingSafeMethodsServlet {

    @Reference
    private CruisesImporter cruisesImporter;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        JSONObject cruisesMapping = cruisesImporter.getJsonMapping();

        response.setContentType("application/json");
        response.getWriter().write(cruisesMapping.toString());
    }
}
