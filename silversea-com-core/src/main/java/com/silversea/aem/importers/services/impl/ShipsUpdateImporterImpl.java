package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.ImporterStatus;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ShipsUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.model.Ship;
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
import java.util.*;

@Deprecated
@Component(immediate = true, label = "Silversea.com - Ship importer Update", metatype = true)
@Service(value = ShipsUpdateImporter.class)
public class ShipsUpdateImporterImpl implements ShipsUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipsUpdateImporterImpl.class);
    private int sessionRefresh = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private ApiConfigurationService apiConfig;
    @Reference
    private Replicator replicat;
    @Reference
    private ApiCallService apiCallService;

    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    private Session session;

    public void init() {
        try {
            Map<String, Object> authenticationPrams = new HashMap<String, Object>();
            authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
            pageManager = resourceResolver.adaptTo(PageManager.class);
            session = resourceResolver.adaptTo(Session.class);
        } catch (LoginException e) {
            LOGGER.debug("Ships update importer login exception ", e);
        }
    }

    @Override
    public ImporterStatus updateImporData() throws IOException {
        init();

        ImporterStatus status = new ImporterStatus();

        Set<Integer> diff = new HashSet<Integer>();

        int errorNumber = 0;
        int succesNumber = 0;
        LOGGER.debug("Début de l'import");

        try {

            if (apiConfig.getSessionRefresh() != 0) {
                sessionRefresh = apiConfig.getSessionRefresh();
            }

            Page shipsRootPage = pageManager.getPage(apiConfig.apiRootPath("shipUrl"));
            List<Ship> listShips;

            listShips = apiCallService.getShips();

            Page rootPathByLocal;
            List<Page> shipPages = new ArrayList<Page>();
            List<String> local = new ArrayList<>();

            int i = 0;
            for (Ship ship : listShips) {
                try {
                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/shipId=\"" + ship.getShipId() + "\"]", "xpath");

                    local = ImporterUtils.finAllLanguageCopies(resourceResolver);

                    if (resources.hasNext()) {
                        shipPages = ImporterUtils.findPagesById(resources);
                    } else {
                        shipPages = ImporterUtils.createPagesALLLanguageCopies(pageManager, resourceResolver,
                                shipsRootPage, local, TemplateConstants.PATH_SHIP, ship.getShipName());
                        LOGGER.debug("create ships with {} ", ship.getShipId());
                    }
                    diff.add(ship.getShipId());

                    for (Page shiPage : shipPages) {
                        if (shiPage != null) {
                            Node shipPageContentNode = shiPage.getContentResource().adaptTo(Node.class);
                            if (shipPageContentNode != null) {
                                shipPageContentNode.setProperty(JcrConstants.JCR_TITLE, ship.getShipName());
                                shipPageContentNode.setProperty("shipId", ship.getShipId());
                                shipPageContentNode.setProperty("shipCode", ship.getShipCod());
                                shipPageContentNode.setProperty("shipName", ship.getShipName());
                                shipPageContentNode.setProperty("shipType", ship.getShipType());
                                shipPageContentNode.setProperty("shipUrl", ship.getShipUrl());
                                session.save();
                                ImporterUtils.updateReplicationStatus(replicat, session, false, shiPage.getPath());
                                LOGGER.debug("Updated ship with {} ", shiPage.getPath());
                            }
                        }
                    }

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
                    LOGGER.debug("Ship import error, number of failures : {}", errorNumber);
                    i++;
                }
            }

            for (String loc : local) {
                rootPathByLocal = ImporterUtils.findPagesLanguageCopies(pageManager, resourceResolver, shipsRootPage,
                        loc);
                Iterator<Page> resourcess = rootPathByLocal.listChildren();
                while (resourcess.hasNext()) {
                    Page page = resourcess.next();
                    Integer id = Integer
                            .parseInt(Objects.toString(page.getContentResource().getValueMap().get("shipId")));
                    if (id != null && !diff.contains(id)) {
                        ImporterUtils.updateReplicationStatus(replicat, session, true, page.getPath());
                    }
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    Node rootNode = shipsRootPage.getContentResource().adaptTo(Node.class);
                    rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
            LOGGER.debug("End of import for ships");

            resourceResolver.close();
        } catch (Exception e) {
            LOGGER.error("Import Ship Errors : {} ", e);
        }

        status.setErrorNumber(errorNumber);
        status.setSuccesNumber(succesNumber);
        return status;
    }

}
