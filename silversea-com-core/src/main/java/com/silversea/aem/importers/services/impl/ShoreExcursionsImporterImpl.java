package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.services.ShoreExcursionsImporter;
import io.swagger.client.ApiException;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.model.Shorex;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by aurelienolivier on 13/02/2017.
 */
@Service
@Component(label = "Silversea.com - Shorexes importer")
public class ShoreExcursionsImporterImpl extends BaseImporter implements ShoreExcursionsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShoreExcursionsImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importShoreExcursions() throws IOException {
        final String authorizationHeader = getAuthorizationHeader("/api/v1/shoreExcursions");

        ShorexesApi shorexesApi = new ShorexesApi();
        shorexesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            List<Shorex> shorexes;
            int i = 1;

            do {
                shorexes = shorexesApi.shorexesGet(null, i, 100, null);

                int j = 0;

                for (Shorex shorex : shorexes) {
                    LOGGER.debug("Importing shorex: {}", shorex.getShorexCod());

                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/codeExcursion=\"" + shorex.getShorexCod() + "\"]", "xpath");

                    Page excursionPage = null;

                    if (resources.hasNext()) {
                        excursionPage = resources.next().adaptTo(Page.class);

                        LOGGER.debug("Shorex page {} with ID {} already exists", shorex.getShorexName(), shorex.getShorexId());
                    } else {
                        Integer cityId = shorex.getCities().size() > 0 ? shorex.getCities().get(0).getCityId() : null;

                        if (cityId != null) {
                            Iterator<Resource> portsResources = resourceResolver.findResources(
                                    "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

                            if (portsResources.hasNext()) {
                                Page portPage = portsResources.next().adaptTo(Page.class);

                                LOGGER.debug("Found port {} with ID {}", portPage.getTitle(), cityId);

                                Page excursionsPage;
                                if (portPage.hasChild("excursions")) {
                                    excursionsPage = pageManager.getPage(portPage.getPath() + "/excursions");
                                } else {
                                    excursionsPage = pageManager.create(portPage.getPath(),
                                        "excursions",
                                        "/apps/silversea/silversea-com/templates/page",
                                        "Excursions",
                                        false);
                                }

                                excursionPage = pageManager.create(excursionsPage.getPath(),
                                        JcrUtil.createValidChildName(excursionsPage.adaptTo(Node.class), shorex.getShorexCod()),
                                        "/apps/silversea/silversea-com/templates/excursion",
                                        shorex.getShorexCod(),
                                        false);

                                LOGGER.debug("Creating excursion {}", shorex.getShorexCod());
                            } else {
                                LOGGER.debug("No city found with id {}", cityId);
                            }
                        } else {
                            LOGGER.debug("Excursion have no city attached, not imported");
                        }
                    }

                    if (excursionPage != null) {
                        Node excursionPageContentNode = excursionPage.getContentResource().adaptTo(Node.class);

                        excursionPageContentNode.setProperty(JcrConstants.JCR_TITLE, shorex.getShorexName());
                        excursionPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, shorex.getDescription());
                        excursionPageContentNode.setProperty("codeExcursion", shorex.getShorexCod());
                        excursionPageContentNode.setProperty("apiDescription", shorex.getDescription());
                        excursionPageContentNode.setProperty("pois", shorex.getPointsOfInterests());

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
            } while (shorexes.size() > 0);

            if (session.hasPendingChanges()) {
                try {
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
