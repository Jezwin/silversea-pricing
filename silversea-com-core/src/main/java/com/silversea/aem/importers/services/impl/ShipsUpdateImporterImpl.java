package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
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
import com.silversea.aem.components.beans.ImporterStatus;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.services.ShipsImporter;
import com.silversea.aem.importers.services.ShipsUpdateImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.api.ShipsApi;
import io.swagger.client.model.Ship;

@Component(immediate = true, label = "Silversea.com - Ship importer", metatype = true)
@Service(value = ShipsUpdateImporter.class)
public class ShipsUpdateImporterImpl extends BaseImporter implements ShipsUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipsUpdateImporterImpl.class);
    // private static final String SHIP_PATH = "/api/v1/ships";

    // private int errorNumber = 0;
    // private int succesNumber = 0;
    private int sessionRefresh = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private ApiConfigurationService apiConfig;
    @Reference
    private Replicator replicat;

    @Override
    public ImporterStatus updateImporData() throws IOException {

        ImporterStatus status = new ImporterStatus();

        Set<Integer> diff = new HashSet<Integer>();

        int errorNumber = 0;
        int succesNumber = 0;
        LOGGER.debug("Début de l'import");

        try {
            /**
             * authentification pour le swagger
             */
            getAuthentification(apiConfig.getLogin(), apiConfig.getPassword());
            /**
             * Récuperation du domain de l'api Swager
             */
            getApiDomain(apiConfig.getApiBaseDomain());
            /**
             * Récuperation de la session refresh
             */
            if (apiConfig.getSessionRefresh() != 0) {
                sessionRefresh = apiConfig.getSessionRefresh();
            }

            final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("shipUrl"));
            // final String authorizationHeader = getAuthorizationHeader(url);
            ShipsApi shipsApi = new ShipsApi();
            shipsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            Session session = resourceResolver.adaptTo(Session.class);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            // Page shipsRootPage =
            // pageManager.getPage(ImportersConstants.BASEPATH_SHIP);
            Page shipsRootPage = pageManager.getPage(apiConfig.apiRootPath("shipUrl"));
            List<Ship> listShips;
            listShips = shipsApi.shipsGet(null);
            int i = 0;
            for (Ship ship : listShips) {
                try {
                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/shipId=\"" + ship.getShipId() + "\"]", "xpath");
                    Page shipPage = null;

                    if (resources.hasNext()) {
                        shipPage = resources.next().adaptTo(Page.class);
                    } else {
                        shipPage = pageManager.create(shipsRootPage.getPath(),
                                JcrUtil.createValidChildName(shipsRootPage.adaptTo(Node.class),
                                        StringHelper.getFormatWithoutSpecialCharcters(ship.getShipName())),
                                TemplateConstants.PATH_SHIP,
                                StringHelper.getFormatWithoutSpecialCharcters(ship.getShipName()), false);
                    }
                    diff.add(ship.getShipId());
                    if (shipPage != null) {
                        Node shipPageContentNode = shipPage.getContentResource().adaptTo(Node.class);
                        if (shipPageContentNode != null) {
                            shipPageContentNode.setProperty(JcrConstants.JCR_TITLE, ship.getShipName());
                            shipPageContentNode.setProperty("shipId", ship.getShipId());
                            shipPageContentNode.setProperty("shipCode", ship.getShipCod());
                            shipPageContentNode.setProperty("shipName", ship.getShipName());
                            shipPageContentNode.setProperty("shipType", ship.getShipType());
                            shipPageContentNode.setProperty("shipUrl", ship.getShipUrl());
                            session.save();
                            LOGGER.debug("Updated ship with {} ", ship.getShipCod());
                        }
                    }
                    if (!replicat.getReplicationStatus(session, shipsRootPage.getPath()).isActivated()) {
                        replicat.replicate(session, ReplicationActionType.ACTIVATE, shipPage.getPath());
                    }
                    LOGGER.debug("Check ship with {} ", ship.getShipCod());
                    i++;
                    succesNumber = succesNumber + 1;
                    if (i % sessionRefresh == 0) {
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
                    LOGGER.debug("Ship import error, number of faulures :", errorNumber);
                    i++;
                }
            }

            // TODO duplication des pages supprimé dans le retour d'api
            Iterator<Page> resourcess = shipsRootPage.listChildren();
            while (resourcess.hasNext()) {
                Page page = resourcess.next();

                if (page.getContentResource().getValueMap().get("shipId") != null &&!diff
                        .contains(Integer.parseInt(page.getContentResource().getValueMap().get("shipId").toString()))) {
                    try {
                        replicat.replicate(session, ReplicationActionType.DEACTIVATE, page.getPath());
                    } catch (ReplicationException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    // save migration date
                    Node rootNode = shipsRootPage.getContentResource().adaptTo(Node.class);
                    rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
            LOGGER.debug("Fin de l'import");

            resourceResolver.close();
        } catch (Exception e) {
            String errorMessage = "Import Ship Errors : {} ";
            LOGGER.error(errorMessage, e);
        }

        status.setErrorNumber(errorNumber);
        status.setSuccesNumber(succesNumber);
        return status;
    }

    // public int getErrorNumber() {
    // return errorNumber;
    // }
    //
    // public int getSuccesNumber() {
    // return succesNumber;
    // }

}
