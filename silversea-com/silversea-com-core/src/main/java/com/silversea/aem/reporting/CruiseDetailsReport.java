package com.silversea.aem.reporting;

import com.day.cq.wcm.api.PageManager;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
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

            final List<CruiseDetailsBean> cruiseModels = getCruiseDetails(resourceResolver);

            StatefulBeanToCsv<CruiseDetailsBean> beanToCsv = new StatefulBeanToCsvBuilder<CruiseDetailsBean>(out).build();

            beanToCsv.write(cruiseModels);

            out.close();

        } catch (LoginException | ImporterException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

    private List<CruiseDetailsBean> getCruiseDetails(ResourceResolver resourceResolver) {
        List<String> split = Arrays.asList(restrictList.split(","));

        final String query = "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]";

        final Iterator<Resource> resources = resourceResolver.findResources(query, "xpath");

        final List<CruiseDetailsBean> cruiseModels = new ArrayList<>();
        while (resources.hasNext()) {
            try {
                final Resource resource = resources.next();
                CruiseDetailsBean bean = new CruiseDetailsBean(resource);
                if (split.contains(bean.getCruiseCode())) {
                    cruiseModels.add(bean);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return cruiseModels;
    }

    private final String restrictList = "SL210418007,SL210425010,SL210505007,SL210512007,SL210519007,SL210526015,SL210610012,SL210622007,SL210629007,SL210706010,SL210716007,SL210723007,SL210730010,SL210809007,SL210816007,SL210823011,SL210903012,SL210915012,SL210927011,SL211008012,SL211020010,SL211030010,SL211109010,SL211119009,SL211128011,SL211209012,SS210510007,SS210517007,SS210528010,SS210607010,SS210617012,SS210706007,SS210713007,SS210720007,SS210727010,SS210806010,SS210816011,SS210827012,SS210908012,SS210920015,SS211005010,SS211015009,SS211024012,MO210413009,MO210422012,MO210504007,MO210517007,MO210524007,MO210531007,MO210614007,MO210621007,MO210628007,MO210705011,MO210716010,MO210726007,MO210802007,MO210809007,MO210816007,MO210823010,MO210902007,MO210909007,MO210916007,MO210923007,MO210930007,MO211007007,MO211014009,MO211023009,MO211101016,DA210922009,DA211001012,DA211013010,DA211023010,DA211102013,SM210520007,SM210527007,SM210603007,SM210610007,SM210617007,SM210624007,SM210701007,SM210708007,SM210715007,SM210722007,SM210729010,SM210808011,SM210819010,SM210829013,SM210912013,WH210606014,WH210620014,WH210704010,WH210714012,WH210726012,WH210807014,WH210821010,WH210831016,WH210916010,WH210926011,WH211007010,WH211017011,WH211028014,WH211111009,E4210716013,E4210729010,E4210808016,E4210824024,E4210917014,E4211001009,E4211010018,E4211028017,E4211114016,E4211130011,E4211211010,E4211221015,E4220105010,E4220115010,E4220125010,E4220204010,E4220214010,E4220224021,E4220317024,E1210321019,E1210409019,E1210428019,E1210607017,E1210624010,E1210704010,E1210714010,E1210724010,E1210803010,E1210813010,E1210823018,E1210910016,E1210926012,E1211008012,E1211019022,E1211110013,E1211123008,E1211201006,E1211207006,E1211213008,E1211221008,E1211229006,E1220104006,E1220110008,E1220118008,E1220126006,E1220201006,E1220207008,E1220215018,WI210330018,WI210417018,WI210505012,WI210517014,WI210531014,WI210614014,WI210628009,WI210707013,WI210720010,WI210730010,WI210809013,WI210822009,WI210831015,WI210915014,WI210929013,WI211012017,WI211029022,WI211120022,WI211212010,WI211222018,WI22010,4220,5732,6004";
}
