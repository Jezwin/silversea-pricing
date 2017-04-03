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
import io.swagger.client.model.Feature;
import io.swagger.client.model.Ship;

@Component(immediate = true, label = "Silversea.com - Cities importer")
@Service(value = ShipsImporter.class)
public class ShipsImporterImpl extends BaseImporter implements ShipsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipsImporterImpl.class);
    private static final String SHIP_PATH = "/api/v1/ships";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importShips() throws IOException {
        final String authorizationHeader = getAuthorizationHeader(SHIP_PATH);
        ShipsApi shipsApi = new ShipsApi();
        shipsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            Session session = resourceResolver.adaptTo(Session.class);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page shipsRootPage = pageManager.getPage(ImportersConstants.BASEPATH_SHIP);
            List<Ship> listShips;
            listShips = shipsApi.shipsGet(null);
            int i = 0;
            for (Ship ship : listShips) {
                Iterator<Resource> resources = resourceResolver.findResources(
                        "//element(*,cq:Page)[jcr:content/shipCode=\"" + ship.getShipCod() + "\"]", "xpath");
                Page shipPage = null;

                if (resources.hasNext()) {
                    shipPage = resources.next().adaptTo(Page.class);
                } else {
                    shipPage = pageManager.create(shipsRootPage.getPath(), ship.getShipCod().toLowerCase(),
                            "/apps/silversea/silversea-com/templates/ship", ship.getShipName());
                }

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
                LOGGER.debug("Check ship with {} ", ship.getShipCod());
                i++;
                if (i % 100 == 0) {
                    if (session.hasPendingChanges()) {
                        try {
                            session.save();
                        } catch (RepositoryException e) {
                            session.refresh(true);
                        }
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
            resourceResolver.close();
        } catch (Exception e) {
            String errorMessage = "Import Ship Errors : {} ";
            LOGGER.error(errorMessage, e);
        }
    }

}
