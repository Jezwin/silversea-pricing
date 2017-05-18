package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.exceptions.UpdateImporterExceptions;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.LandProgramUpdateImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.LandsApi;
import io.swagger.client.model.Land;
import io.swagger.client.model.Land77;

/**
 * Created by mbennabi on 17/03/2017.
 */
@Service
@Component(label = "Silversea.com - Land Program Update importer")
public class LandProgramUpdateImporterImpl extends BaseImporter implements LandProgramUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(LandProgramUpdateImporterImpl.class);

    private int errorNumber = 0;
    private int succesNumber = 0;
    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicat;

    @Override
    public void updateImporData() throws IOException, ReplicationException, UpdateImporterExceptions {

        
        // final String authorizationHeader =
        // getAuthorizationHeader("/api/v1/landAdventures");

        try {
            final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("landProgramUrl"));
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            // get authentification to the Land API
            LandsApi landsApi = new LandsApi();
            landsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

            // get parent content resource
            Page citiesRootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            // Resource resParent =
            // resourceResolver.getResource(ImportersConstants.BASEPATH_PORTS);
            Resource resParent = citiesRootPage.adaptTo(Resource.class);
            Date date = resParent.getChild("jcr:content").getValueMap().get("lastModificationDate", Date.class);

            // get last importing date
            String dateFormat = "yyyyMMdd";
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String currentDate;
            if (date != null) {
                currentDate = formatter.format(date.getTime()).toString();

                int i = 1;

                List<Land77> lands;

                do {

                    // gets all lands
                    lands = landsApi.landsGetChanges(currentDate, null, i, pageSize, null);

                    int j = 0;

                    for (Land77 land : lands) {
                        try {

                            Iterator<Resource> resources = resourceResolver.findResources(
                                    "//element(*,cq:Page)[jcr:content/landId=\"" + land.getLandId() + "\"]", "xpath");

                            Page landPage = null;

                            if (resources.hasNext()) {
                                landPage = resources.next().adaptTo(Page.class);

                                // désactivation de la page si le boolean est a
                                // true
                                if (BooleanUtils.isTrue(land.getIsDeleted())) {
                                    replicat.replicate(session, ReplicationActionType.DEACTIVATE, landPage.getPath());
                                }
                            } else {
                                Integer cityId = land.getCities().size() > 0 ? land.getCities().get(0).getCityId()
                                        : null;

                                if (cityId != null) {
                                    Iterator<Resource> portsResources = resourceResolver.findResources(
                                            "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

                                    if (portsResources.hasNext()) {
                                        Page portPage = portsResources.next().adaptTo(Page.class);

                                        LOGGER.debug("Found port {} with ID {}", portPage.getTitle(), cityId);

                                        Page landsPage;
                                        if (portPage.hasChild("landprogram")) {
                                            landsPage = pageManager.getPage(portPage.getPath() + "/land-programs");
                                        } else {
                                            landsPage = pageManager.create(portPage.getPath(), "land-programs",
                                                    "/apps/silversea/silversea-com/templates/page", "Land Program",
                                                    false);
                                        }

                                        landPage = pageManager.create(landsPage.getPath(),
                                                JcrUtil.createValidChildName(landsPage.adaptTo(Node.class),
                                                        land.getLandName()),
                                                TemplateConstants.PATH_LANDPROGRAM, land.getLandName(), false);

                                        LOGGER.debug("Creating land {}", land.getLandCod());
                                    } else {
                                        LOGGER.debug("No city found with id {}", cityId);
                                    }
                                } else {
                                    LOGGER.debug("Excursion have no city attached, not imported");
                                }
                            }

                            if (landPage != null) {
                                Node hotelPageContentNode = landPage.getContentResource().adaptTo(Node.class);
                                hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE, land.getLandName());
                                hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, land.getDescription());
                                hotelPageContentNode.setProperty("landId", land.getLandId());
                                hotelPageContentNode.setProperty("landCode", land.getLandCod());
                                succesNumber = succesNumber + 1;
                                j++;
                            }

                            if (j % sessionRefresh == 0) {
                                if (session.hasPendingChanges()) {
                                    try {
                                        session.save();
                                    } catch (RepositoryException e) {
                                        session.refresh(true);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            errorNumber = errorNumber + 1;
                            LOGGER.debug("cities error, number of faulures :", errorNumber);
                            j++;
                        }
                    }

                    i++;
                } while (lands.size() > 0);

                if (session.hasPendingChanges()) {
                    try {
                        // save migration date
                        Node rootNode = resParent.getChild(JcrConstants.JCR_CONTENT).adaptTo(Node.class);
                        rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                        session.save();
                    } catch (RepositoryException e) {
                        session.refresh(false);
                    }
                }

                resourceResolver.close();
            }else{
                throw new UpdateImporterExceptions();
            }
        } catch (ApiException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing shorexes", e);
        }
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public int getSuccesNumber() {
        return succesNumber;
    }
}
