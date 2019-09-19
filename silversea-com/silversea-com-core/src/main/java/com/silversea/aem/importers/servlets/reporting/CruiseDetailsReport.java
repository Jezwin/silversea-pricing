package com.silversea.aem.importers.servlets.reporting;

import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.services.ApiConfigurationService;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.jcr.Session;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@SlingServlet(paths = "/bin/report-cruisedetails")
public class CruiseDetailsReport extends SlingSafeMethodsServlet {

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

            final List<CruiseModel> cruiseModels = getCruiseDetails(resourceResolver);
            for (CruiseModel cruiseModel: cruiseModels) {
                List<String> output = new ArrayList<>();
                output.add(cruiseModel.getCruiseCode());
                output.add(cruiseModel.getBigItineraryMap());
                output.add(cruiseModel.getDescription());
                out.println(String.join(",", output));
            }


            out.close();
            // Iterate over exclusive offers from API
        } catch (LoginException | ImporterException e) {
        }


        int numBytesRead = 10;
        out.write(numBytesRead);
    }
    private List<CruiseModel> getCruiseDetails(ResourceResolver resourceResolver) {
        final String query = "/jcr:root/content/silversea-com//element(*,cq:Page)" +
                "[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/destination\"]";

        final Iterator<Resource> resources = resourceResolver.findResources(query,"xpath");

        final List<CruiseModel> cruiseModels = new ArrayList<>();
        while (resources.hasNext()) {
            final Resource itinerary = resources.next();
            final CruiseModel itineraryModel = itinerary.adaptTo(CruiseModel.class);

            if (itineraryModel != null) {
                cruiseModels.add(itineraryModel);
            }
        }
        return cruiseModels;
    }
}
