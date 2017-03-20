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
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ShoreExcursionsUpdateImporter;

import io.swagger.client.ApiException;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.model.Shorex77;

/**
 * Created by mbennabi on 17/03/2017.
 */
@Service
@Component(label = "Silversea.com - Shorexes Update importer")
public class ShoreExcursionsUpdateImporterImpl extends BaseImporter implements ShoreExcursionsUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShoreExcursionsUpdateImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicat;

    @Override
    public void importUpdateShoreExcursions() throws IOException, ReplicationException {
        final String authorizationHeader = getAuthorizationHeader("/api/v1/shoreExcursions");

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            // get parent content resource
            Resource resParent = resourceResolver.getResource(ImportersConstants.BASEPATH_PORTS);
            Date date = resParent.getChild("jcr:content").getValueMap().get("lastModificationDate", Date.class);

            // get last importing date
            String dateFormat = "yyyymmdd";
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String currentDate;
            if (date != null) {
                currentDate = formatter.format(date.getTime());

                ShorexesApi shorexesApi = new ShorexesApi();
                shorexesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

                List<Shorex77> shorexes;
                int i = 1;

                do {
                    shorexes = shorexesApi.shorexesGetChanges(currentDate, i, 100, null);

                    int j = 0;

                    for (Shorex77 shorex : shorexes) {
                        LOGGER.debug("Importing shorex: {}", shorex.getShorexCod());

                        Iterator<Resource> resources = resourceResolver.findResources(
                                "//element(*,cq:Page)[jcr:content/codeExcursion=\"" + shorex.getShorexCod() + "\"]",
                                "xpath");

                        Page excursionPage = null;

                        if (resources.hasNext()) {
                            excursionPage = resources.next().adaptTo(Page.class);
                            if (BooleanUtils.isTrue(shorex.getIsDeleted())) {
                                replicat.replicate(session, ReplicationActionType.DEACTIVATE, excursionPage.getPath());
                            }
                            LOGGER.debug("Shorex page {} with ID {} already exists", shorex.getShorexName(),
                                    shorex.getShorexId());
                        } else {
                            Integer cityId = shorex.getCities().size() > 0 ? shorex.getCities().get(0).getCityId()
                                    : null;

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
                                        excursionsPage = pageManager.create(portPage.getPath(), "excursions",
                                                "/apps/silversea/silversea-com/templates/page", "Excursions", false);
                                    }

                                    excursionPage = pageManager.create(excursionsPage.getPath(),
                                            JcrUtil.createValidChildName(excursionsPage.adaptTo(Node.class),
                                                    shorex.getShorexCod()),
                                            "/apps/silversea/silversea-com/templates/excursion", shorex.getShorexCod(),
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
                            excursionPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
                                    shorex.getShortDescription());
                            excursionPageContentNode.setProperty("codeExcursion", shorex.getShorexCod());
                            excursionPageContentNode.setProperty("apiLongDescription", shorex.getDescription());
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
                        // save migration date
                        Node rootNode = resParent.adaptTo(Node.class);
                        rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                        session.save();
                    } catch (RepositoryException e) {
                        session.refresh(false);
                    }
                }

                resourceResolver.close();
            }
        } catch (ApiException | WCMException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing shorexes", e);
        }
    }
}
