package com.silversea.aem.importers.servlets;

import com.silversea.aem.importers.services.CitiesImporter;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.servlet.ServletException;
import java.io.IOException;

@SlingServlet(paths = "/bin/cities-updater", extensions = "html")
public class CitiesUpdaterServlet extends SlingSafeMethodsServlet {

    @Reference
    private CitiesImporter citiesImporter;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        citiesImporter.updateCitiesAfterMigration();
    }
}
