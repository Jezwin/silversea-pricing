package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.LandProgramsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.utils.StringsUtils;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.LandsApi;
import io.swagger.client.model.Land;
import io.swagger.client.model.Land77;
import org.apache.commons.lang3.BooleanUtils;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Component
public class LandProgramsImporterImpl implements LandProgramsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(LandProgramsImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

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

        Map<String, Object> authenticationPrams = new HashMap<>();
        authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final LandsApi landsApi = new LandsApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            List<Land> landPrograms;
            int apiPage = 1, itemsWritten = 0;

            LOGGER.debug("Importing land programs");

            do {
                landPrograms = landsApi.landsGet(null, apiPage, pageSize, null);

                for (Land landProgram : landPrograms) {
                    LOGGER.trace("Importing land program: {}", landProgram.getLandName());

                    try {
                        // Getting cities with the city id read from the land
                        // program
                        Integer cityId = landProgram.getCities().size() > 0 ? landProgram.getCities().get(0).getCityId()
                                : null;

                        if (cityId == null) {
                            throw new ImporterException("Land program have no city");
                        }

                        // TODO create cache of cityId / Resource in order to speed up the process
                        Iterator<Resource> portsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com"
                                        + "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

                        if (!portsResources.hasNext()) {
                            throw new ImporterException("Cannot find city with id " + cityId);
                        }

                        while (portsResources.hasNext()) {
                            // Getting port page
                            Page portPage = portsResources.next().adaptTo(Page.class);

                            if (portPage == null) {
                                throw new ImporterException("Error getting port page " + cityId);
                            }

                            LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                            // Creating subpage "land-programs" if not present
                            Page landProgramsPage;
                            if (portPage.hasChild(WcmConstants.NN_LAND_PROGRAMS)) {
                                landProgramsPage = pageManager.getPage(portPage.getPath() + "/" + WcmConstants.NN_LAND_PROGRAMS);
                            } else {
                                landProgramsPage = pageManager.create(portPage.getPath(), WcmConstants.NN_LAND_PROGRAMS,
                                        WcmConstants.PAGE_TEMPLATE_PAGE, "Land Programs", false);

                                LOGGER.trace("{} page is not existing, creating it", landProgramsPage.getPath());
                            }

                            final Page landProgramPage = pageManager.create(landProgramsPage.getPath(),
                                    JcrUtil.createValidChildName(landProgramsPage.adaptTo(Node.class),
                                            StringsUtils.getFormatWithoutSpecialCharcters(landProgram.getLandName())),
                                    WcmConstants.PAGE_TEMPLATE_LAND_PROGRAM,
                                    StringsUtils.getFormatWithoutSpecialCharcters(landProgram.getLandName()), false);

                            LOGGER.trace("Creating land program {} in city {}", landProgram.getLandName(),
                                    portPage.getPath());

                            // If land program is created, set the properties
                            if (landProgramPage == null) {
                                throw new ImporterException(
                                        "Cannot create land program page for landprogram " + landProgram.getLandName());
                            }

                            Node landProgramPageContentNode = landProgramPage.getContentResource().adaptTo(Node.class);

                            if (landProgramPageContentNode == null) {
                                throw new ImporterException(
                                        "Cannot set properties for land program " + landProgram.getLandName());
                            }

                            landProgramPageContentNode.setProperty(JcrConstants.JCR_TITLE, landProgram.getLandName());
                            landProgramPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
                                    landProgram.getDescription());
                            landProgramPageContentNode.setProperty("landId", landProgram.getLandId());
                            landProgramPageContentNode.setProperty("landCode", landProgram.getLandCod());

                            // Set livecopy mixin
                            if (!LanguageHelper.getLanguage(portPage).equals("en")) {
                                landProgramPageContentNode.addMixin("cq:LiveRelationship");
                            }

                            LOGGER.trace("Land program {} successfully created", landProgramPage.getPath());

                            successNumber++;
                            itemsWritten++;
                        }

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} land programs imported, saving session", +itemsWritten);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (WCMException | RepositoryException | ImporterException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    }
                }

                apiPage++;
            } while (landPrograms.size() > 0);

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateLandPrograms");
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read land programs from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending land programs import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    /**
     * TODO it seems a lot of elements are marked as modified on API, compare API data against CRX data before update
     */
    @Override
    public ImportResult updateLandPrograms() {
        LOGGER.debug("Starting land programs update");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final LandsApi landsApi = new LandsApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final String lastModificationDate = ImporterUtils.getDateFromPageProperties(rootPage, "lastModificationDateLandPrograms");

            LOGGER.debug("Last import date for land programs {}", lastModificationDate);

            int itemsWritten = 0, apiPage = 1;
            List<Land77> landPrograms;

            do {
                final ApiResponse<List<Land77>> apiResponse = landsApi.landsGetChangesWithHttpInfo(lastModificationDate, null,
                        apiPage , pageSize, null);
                landPrograms = apiResponse.getData();

                // TODO replace by header
                LOGGER.trace("Total landPrograms : {}, page : {}, landPrograms for this page : {}", landPrograms.size(), apiPage, landPrograms.size());

                for (Land77 landProgram : landPrograms) {
                    final String landProgramName = landProgram.getLandName();

                    LOGGER.debug("Updating landProgram: {}", landProgramName);

                    try {
                        // Getting all the landProgram pages with the current landId
                        // TODO create cache of land id / Resource in order to speed up the process
                        Iterator<Resource> landProgramsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com//element(*,cq:Page)[" +
                                        "jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/landprogram\"" +
                                        " and jcr:content/landId=\"" + landProgram.getLandId() + "\"]", "xpath");

                        if (landProgramsResources.hasNext()) {
                            // if landPrograms are found, update it
                            while (landProgramsResources.hasNext()) {
                                final Resource landProgramResource = landProgramsResources.next();
                                final Page landProgramPage = landProgramResource.adaptTo(Page.class);

                                LOGGER.trace("Updating landProgram {}", landProgramName);

                                if (landProgramPage == null) {
                                    throw new ImporterException("Cannot set landProgram page " + landProgramName);
                                }

                                // depending of the city status, mark the page to be activated or deactivated
                                if (BooleanUtils.isTrue(landProgram.getIsDeleted())) {
                                    final Node landProgramContentNode = landProgramPage.getContentResource().adaptTo(Node.class);

                                    if (landProgramContentNode == null) {
                                        throw new ImporterException("Cannot set properties for landProgram " + landProgramName);
                                    }

                                    landProgramContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

                                    LOGGER.trace("Land program {} is marked to be deactivated", landProgramName);
                                } else {
                                    final Node landProgramContentNode = updateLandProgramContentNode(landProgram, landProgramPage);
                                    landProgramContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                    LOGGER.trace("Land program {} is marked to be activated", landProgramName);
                                }
                            }
                        } else {
                            // Getting cities with the city id read from the landProgram
                            Integer cityId = landProgram.getCities().size() > 0 ? landProgram.getCities().get(0).getCityId() : null;

                            if (cityId == null) {
                                throw new ImporterException("Land program have no city");
                            }

                            // else create land program page for each language
                            // TODO create cache of cityId / Resource in order to speed up the process
                            Iterator<Resource> portsResources = resourceResolver
                                    .findResources("/jcr:root/content/silversea-com//element(*,cq:Page)[" +
                                            "jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/port\"" +
                                            " and jcr:content/cityId=\"" + cityId + "\"]", "xpath");

                            if (!portsResources.hasNext()) {
                                throw new ImporterException("Cannot find city with id " + cityId);
                            }

                            while (portsResources.hasNext()) {
                                // Getting port page
                                Page portPage = portsResources.next().adaptTo(Page.class);

                                if (portPage == null) {
                                    throw new ImporterException("Error getting port page " + cityId);
                                }

                                LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                                // Creating subpage "landProgram" if not present
                                Page landProgramsPage;
                                if (portPage.hasChild(WcmConstants.NN_LAND_PROGRAMS)) {
                                    landProgramsPage = pageManager.getPage(portPage.getPath() + "/" + WcmConstants.NN_LAND_PROGRAMS);
                                } else {
                                    landProgramsPage = pageManager.create(portPage.getPath(), WcmConstants.NN_LAND_PROGRAMS,
                                            WcmConstants.PAGE_TEMPLATE_PAGE, "Land programs", false);

                                    LOGGER.trace("{} page is not existing, creating it", landProgramsPage.getPath());
                                }

                                // Creating landProgram page
                                final Page landProgramPage = pageManager.create(landProgramsPage.getPath(),
                                        JcrUtil.createValidChildName(landProgramsPage.adaptTo(Node.class),
                                                StringsUtils.getFormatWithoutSpecialCharcters(landProgramName)),
                                        WcmConstants.PAGE_TEMPLATE_LAND_PROGRAM,
                                        StringsUtils.getFormatWithoutSpecialCharcters(landProgramName), false);

                                LOGGER.trace("Creating landProgram {} in city {}", landProgramName, portPage.getPath());

                                // If landProgram is created, set the properties
                                if (landProgramPage == null) {
                                    throw new ImporterException(
                                            "Cannot create landProgram page for landProgram " + landProgramName);
                                }

                                final Node landProgramContentNode = updateLandProgramContentNode(landProgram, landProgramPage);
                                landProgramContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                LOGGER.trace("Land program {} successfully created", landProgramPage.getPath());
                            }
                        }

                        successNumber++;
                        itemsWritten++;

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} landPrograms imported, saving session", +itemsWritten);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (RepositoryException | ImporterException | WCMException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    }
                }

                apiPage++;
            } while (landPrograms.size() > 0);

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateLandPrograms");
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read land programs from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public void importOneLandProgram(String landProgramId) {
        // TODO implement
    }

    /**
     * Update land program properties from API
     *
     * @param landProgram landProgram object from API
     * @param landProgramPage page of the landProgram
     * @return the content node of the landProgram page, updated
     * @throws ImporterException if the landProgram page cannot be updated
     */
    private Node updateLandProgramContentNode(Land77 landProgram, Page landProgramPage) throws ImporterException {
        final Node landProgramPageContentNode = landProgramPage.getContentResource().adaptTo(Node.class);

        if (landProgramPageContentNode == null) {
            throw new ImporterException("Cannot set properties for landProgram " + landProgram.getLandName());
        }

        try {
            landProgramPageContentNode.setProperty(JcrConstants.JCR_TITLE, landProgram.getLandName());
            landProgramPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
                    landProgram.getDescription());
            landProgramPageContentNode.setProperty("landId", landProgram.getLandId());
            landProgramPageContentNode.setProperty("landCode", landProgram.getLandCod());

            // Set livecopy mixin
            if (!LanguageHelper.getLanguage(landProgramPage).equals("en")) {
                landProgramPageContentNode.addMixin("cq:LiveRelationship");
            }
        } catch (RepositoryException e) {
            throw new ImporterException("Cannot set properties for landProgram " + landProgram.getLandName(), e);
        }

        return landProgramPageContentNode;
    }
}
