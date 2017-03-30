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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ShipsImporter;

import io.swagger.client.api.ShipsApi;
import io.swagger.client.model.Ship;

@Component(immediate = true, label = "Silversea.com - Cities importer")
@Service(value = ShipsImporter.class)
public class ShipsImporterImpl extends BaseImporter implements ShipsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipsImporterImpl.class);
    private static final String SHIP_PATH = "/api/v1/ships";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private Session session;

    @Override
    public void importShips() throws IOException {
        final String authorizationHeader = getAuthorizationHeader(SHIP_PATH);
        ShipsApi shipsApi = new ShipsApi();
        shipsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        try {
            PageManager pageManager = getResourceResolver().adaptTo(PageManager.class);
            Page shipsRootPage = pageManager.getPage(ImportersConstants.BASEPATH_SHIP);

            List<Ship> listShips = shipsApi.shipsGet(null);
            for (Ship ship : listShips) {
                Iterator<Resource> resources = getResourceResolver().findResources(
                        "//element(*,cq:Page)[jcr:content/shipCode=\"" + ship.getShipCod() + "\"]", "xpath");
                Page shipPage = null;

                if (resources.hasNext()) {
                    shipPage = resources.next().adaptTo(Page.class);
                } else {
                    shipPage = pageManager.create(shipsRootPage.getPath(), ship.getShipCod().toLowerCase(),
                            "/apps/silversea/silversea-com/templates/ship", ship.getShipName());
                    LOGGER.debug("Creating ship {} ", ship.getShipCod());
                }

                if (shipPage != null) {
                    Node shipPageContentNode = shipPage.getContentResource().adaptTo(Node.class);
                    updateShipNode(shipPageContentNode, ship);
                }

            }
            updateRoot(shipsRootPage);
        } catch (Exception e) {
            String errorMessage = "Import Ship Errors : {} ";
            LOGGER.error(errorMessage, e);
        }
    }

    private void updateShipNode(Node shipPageContentNode, Ship ship) {
        try {
            session = getResourceResolver().adaptTo(Session.class);
            if (shipPageContentNode != null) {
                shipPageContentNode.setProperty(JcrConstants.JCR_TITLE, ship.getShipName());
                shipPageContentNode.setProperty("shipId", ship.getShipId());
                shipPageContentNode.setProperty("shipCode", ship.getShipCod());
                shipPageContentNode.setProperty("shipName", ship.getShipName());
                shipPageContentNode.setProperty("shipType", ship.getShipType());
                shipPageContentNode.setProperty("shipUrl", ship.getShipUrl());
                session.save();
            }
            session.logout();
        } catch (LoginException | RepositoryException e) {
            String errorMessage = "Update Ship Errors : {} ";
            LOGGER.error(errorMessage, e);
        }
    }

    private void updateRoot(Page page) {
        try {
            session = getResourceResolver().adaptTo(Session.class);
            // save migration date
            if (page != null) {
                Node rootShipNode = page.getContentResource().adaptTo(Node.class);
                rootShipNode.setProperty("lastModificationDate", Calendar.getInstance());
                session.save();
            }
            session.logout();
        } catch (RepositoryException | LoginException e) {
            String errorMessage = "Update Root Node Modification Date : {} ";
            LOGGER.error(errorMessage, e);
        }
    }

    private ResourceResolver getResourceResolver() throws LoginException {
        return resourceResolverFactory.getAdministrativeResourceResolver(null);
    }

}
