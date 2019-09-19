package com.silversea.aem.importers.servlets.reporting;

import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.jcr.Session;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SlingServlet(paths = "/bin/report-itinerary")
public class ItineraryReport extends SlingSafeMethodsServlet {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-disposition", "attachment; filename=sample.txt");

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        PrintWriter out = response.getWriter();
        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final List<ItineraryModel> itineraries = ImportersUtils.getItineraries(resourceResolver);
            for (ItineraryModel itinerary: itineraries) {
                List<String> output = new ArrayList<>();
                output.add(itinerary.getCruiseId().toString());
                output.add(itinerary.getItineraryId().toString());
                output.add(itinerary.getNumberDays().toString());
                out.println(String.join(",", output));
            }

            out.close();
            // Iterate over exclusive offers from API
        } catch (LoginException | ImporterException e) {
        }


        int numBytesRead = 10;
        out.write(numBytesRead);
    }
}
