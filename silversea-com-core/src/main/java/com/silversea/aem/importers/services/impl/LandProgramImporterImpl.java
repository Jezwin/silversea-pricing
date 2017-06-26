package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.LandProgramImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.model.Land;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Service
@Component(label = "Silversea.com - Land Program importer")
public class LandProgramImporterImpl extends BaseImporter implements LandProgramImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(LandProgramImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private ApiCallService apiCallService;

    @Activate
    protected void activate(final ComponentContext context) {
        if (apiConfig.getSessionRefresh() != 0) {
            sessionRefresh = apiConfig.getSessionRefresh();
        }

        if (apiConfig.getPageSize() != 0) {
            pageSize = apiConfig.getPageSize();
        }
    }

    @Override
    public ImportResult importAllLandPrograms() {
        LOGGER.debug("Starting land programs import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            // Session initialization
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            List<Land> landPrograms;
            int i = 1, j = 0;

            LOGGER.debug("Importing land programs");

            do {
                landPrograms = apiCallService.getLandProgram(i, pageSize);

                for (Land landProgram : landPrograms) {
                    LOGGER.trace("Importing land program: {}", landProgram.getLandName());

                    try {
                        // Getting cities with the city id read from the land program
                        Integer cityId = landProgram.getCities().size() > 0 ? landProgram.getCities().get(0).getCityId() : null;

                        if (cityId == null) {
                            throw new ImporterException("Land program have no city");
                        }

                        Iterator<Resource> portsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com"
                                        + "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId
                                        + "\"]", "xpath");

                        if (!portsResources.hasNext()) {
                            throw new ImporterException("Cannot find city with id " + cityId);
                        }

                        while (portsResources.hasNext()) {
                            // Getting port page
                            Page portPage = portsResources.next().adaptTo(Page.class);

                            if (portPage == null) {
                                throw new ImporterException("Error getting port page " + portPage.getPath());
                            }

                            LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                            // Creating subpage "land-programs" if not present
                            Page landProgramsPage;
                            if (portPage.hasChild("land-programs")) {
                                landProgramsPage = pageManager.getPage(portPage.getPath() + "/land-programs");
                            } else {
                                landProgramsPage = pageManager.create(portPage.getPath(), "land-programs",
                                        "/apps/silversea/silversea-com/templates/page", "Land Programs",
                                        false);

                                LOGGER.trace("{} page is not existing, creating it", landProgramsPage.getPath());
                            }

                            final Page landProgramPage = pageManager.create(landProgramsPage.getPath(),
                                    JcrUtil.createValidChildName(landProgramsPage.adaptTo(Node.class),
                                            StringHelper.getFormatWithoutSpecialCharcters(
                                                    landProgram.getLandName())),
                                    TemplateConstants.PATH_LANDPROGRAM, StringHelper
                                            .getFormatWithoutSpecialCharcters(landProgram.getLandName()),
                                    false);

                            LOGGER.trace("Creating land program {} in city {}", landProgram.getLandName(), portPage.getPath());

                            // If land program is created, set the properties
                            if (landProgramPage == null) {
                                throw new ImporterException("Cannot create land program page for landprogram " + landProgram.getLandName());
                            }

                            Node landProgramPageContentNode = landProgramPage.getContentResource().adaptTo(Node.class);

                            if (landProgramPageContentNode == null) {
                                throw new ImporterException("Cannot set properties for land program " + landProgram.getLandName());
                            }

                            landProgramPageContentNode.setProperty(JcrConstants.JCR_TITLE, landProgram.getLandName());
                            landProgramPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, landProgram.getDescription());
                            landProgramPageContentNode.setProperty("landId", landProgram.getLandId());
                            landProgramPageContentNode.setProperty("landCode", landProgram.getLandCod());

                            LOGGER.trace("Land program {} successfully created", landProgramPage.getPath());

                            successNumber++;
                            j++;
                        }

                        if (j % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} land programs imported, saving session", +j);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (WCMException | RepositoryException | ImporterException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    }
                }

                i++;
            } while (landPrograms.size() > 0);

            setLastModificationDate(pageManager, session,
                    apiConfig.apiRootPath("citiesUrl"), "lastModificationDateLandPrograms");

            resourceResolver.close();
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read land programs from API", e);
        }

        LOGGER.debug("Ending land programs import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateLandPrograms() {
        return null;
    }

    @Override
    public void importOneLandProgram(String landProgramId) {

    }
}
