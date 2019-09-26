package com.silversea.aem.reporting;

import com.day.cq.wcm.api.PageManager;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.reporting.models.read.CruiseModelCrx;
import com.silversea.aem.reporting.models.write.CruiseDetailsBean;
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
        response.setHeader("Content-disposition", "attachment; filename=sample.csv");

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        PrintWriter out = response.getWriter();
        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final List<CruiseModelCrx> cruiseModels = getCruiseDetails(resourceResolver);

            StatefulBeanToCsv<CruiseDetailsBean> beanToCsv = new StatefulBeanToCsvBuilder<CruiseDetailsBean>(out).build();

            for (CruiseModelCrx cruiseModelCrx : cruiseModels) {
                CruiseDetailsBean bean = new CruiseDetailsBean(cruiseModelCrx);
                beanToCsv.write(bean);
            }
            out.close();

        } catch (LoginException | ImporterException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }
    private List<CruiseModelCrx> getCruiseDetails(ResourceResolver resourceResolver) {
           final String query = "/jcr:root/content/silversea-com//element(*,cq:Page)" +
                "[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/destination\"]";

        final Iterator<Resource> resources = resourceResolver.findResources(query,"xpath");

        final List<CruiseModelCrx> cruiseModels = new ArrayList<>();
        while (resources.hasNext()) {
            final Resource resource = resources.next();
            final CruiseModelCrx cruiseModelCrx = resource.adaptTo(CruiseModelCrx.class);

            if (cruiseModelCrx != null) {
                cruiseModels.add(cruiseModelCrx);
            }
        }
        return cruiseModels;
    }
}
