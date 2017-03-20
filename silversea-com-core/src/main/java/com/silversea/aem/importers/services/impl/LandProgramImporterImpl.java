package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.LandProgramImporter;

import io.swagger.client.ApiException;
import io.swagger.client.api.LandsApi;
import io.swagger.client.model.Hotel;
import io.swagger.client.model.Land;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Service
@Component(label = "Silversea.com - Land Program importer")
public class LandProgramImporterImpl extends BaseImporter implements LandProgramImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(LandProgramImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importLandProgram() throws IOException {
        final String authorizationHeader = getAuthorizationHeader("/api/v1/landAdventures");

        // get authentification to the Land API
        LandsApi landsApi = new LandsApi();
        landsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);
            Page citiesRootPage = pageManager.getPage(ImportersConstants.BASEPATH_PORTS);

            int i = 1;

            List<Land> lands;

            do {

                // gets all lands
                lands = landsApi.landsGet(null, i, 100, null);

                int j = 0;

                for (Land land : lands) {

                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/landCode=\"" + land.getLandCod() + "\"]", "xpath");

                    Page landPage = null;

                    if (resources.hasNext()) {
                        landPage = resources.next().adaptTo(Page.class);
                    } else {
                        Integer cityId = land.getCities().size() > 0 ? land.getCities().get(0).getCityId() : null;

                        if (cityId != null) {
                            Iterator<Resource> portsResources = resourceResolver.findResources(
                                    "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

                            if (portsResources.hasNext()) {
                                Page portPage = portsResources.next().adaptTo(Page.class);

                                LOGGER.debug("Found port {} with ID {}", portPage.getTitle(), cityId);

                                Page landsPage;
                                if (portPage.hasChild("landprogram")) {
                                    landsPage = pageManager.getPage(portPage.getPath() + "/landprogram");
                                } else {
                                    landsPage = pageManager.create(portPage.getPath(), "landprogram",
                                            "/apps/silversea/silversea-com/templates/page", "Land Program", false);
                                }

                                landPage = pageManager.create(landsPage.getPath(),
                                        JcrUtil.createValidChildName(landsPage.adaptTo(Node.class), land.getLandName()),
                                        "/apps/silversea/silversea-com/templates/landprogram", land.getLandName(),
                                        false);

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
                        j++;
                    }

                    if (j % 100 == 0) {
                        if (session.hasPendingChanges()) {
                            try {
                                session.save();
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    }
                }

                i++;
            } while (lands.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    // save migration date
                    Node rootNode = citiesRootPage.getContentResource().adaptTo(Node.class); 
                    rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            resourceResolver.close();
        } catch (ApiException | WCMException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing shorexes", e);
        }
    }
}
