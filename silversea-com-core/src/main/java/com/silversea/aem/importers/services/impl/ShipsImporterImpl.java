package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ShipsImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.api.ShipsApi;
import io.swagger.client.model.Ship;

@Component(immediate = true, label = "Silversea.com - Ship importer", metatype = true)
@Service(value = ShipsImporter.class)
public class ShipsImporterImpl extends BaseImporter implements ShipsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipsImporterImpl.class);
    // private static final String SHIP_PATH = "/api/v1/ships";

    private int errorNumber = 0;
    private int succesNumber = 0;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private ApiConfigurationService apiConfig;

//    /** URL récupérer l url de l API. **/
//    @Property(value = "/api/v1/ships", label = "Api Path", description = "path to the ship api")
//    private static String URL = "url";
//    /** URL récupérer les codes groupements service. **/
//    private String url;

    /**
     * Methode activate permettant de récupérer les valeurs des propriétés
     * 
     * @param compContext
     */
//    @Activate
//    @Modified
//    protected void activate(ComponentContext compContext) {
//
//        LOGGER.debug("Activation service configuration");
//
//        // Récupération des propriétés
//        @SuppressWarnings("unchecked")
//        Dictionary<String, String> properties = compContext.getProperties();
//
//        // Récupération de la propriété SERVICE_URL
//        url = PropertiesUtil.toString(properties.get(URL), "/api/v1/ships");
//    }

    @Override
    public void importData() throws IOException {
        LOGGER.debug("Début de l'import");

        try {
            final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("shipUrl"));
//            final String authorizationHeader = getAuthorizationHeader(url);
            ShipsApi shipsApi = new ShipsApi();
            shipsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            Session session = resourceResolver.adaptTo(Session.class);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page shipsRootPage = pageManager.getPage(ImportersConstants.BASEPATH_SHIP);
            List<Ship> listShips;
            listShips = shipsApi.shipsGet(null);
            int i = 0;

            for (Ship ship : listShips) {
                try {
                  //TODO remove this conditions, just to test 
//                  if(i==2){
//                      String test = null;
//                      test.toString();
//                  }
                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/shipId=\"" + ship.getShipId() + "\"]", "xpath");
                    Page shipPage = null;

                    if (resources.hasNext()) {
                        shipPage = resources.next().adaptTo(Page.class);
                    } else {
//                        shipPage = pageManager.create(shipsRootPage.getPath(), ship.getShipName(),
//                                TemplateConstants.PATH_SHIP, ship.getShipName(),false);
                        
                        shipPage = pageManager.create(shipsRootPage.getPath(),
                                JcrUtil.createValidChildName(shipsRootPage.adaptTo(Node.class),
                                        ship.getShipName()),
                                        TemplateConstants.PATH_SHIP, ship.getShipName(), false);
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
                    succesNumber = succesNumber+1;
                    if (i % 100 == 0) {
                        if (session.hasPendingChanges()) {
                            try {
                                session.save();
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }

                    }
                } catch (Exception e) {
                    errorNumber = errorNumber+1;
                    LOGGER.debug("Ship import error, number of faulures :", errorNumber);
                    i++;
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
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public int getSuccesNumber() {
        return succesNumber;
    }
    

}
