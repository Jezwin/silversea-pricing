package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ShoreExcursionsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.utils.StringsUtils;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.model.Shorex;
import io.swagger.client.model.Shorex77;
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
public class ShoreExcursionsImporterImpl implements ShoreExcursionsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShoreExcursionsImporterImpl.class);

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
    public ImportResult importAllShoreExcursions() {
        LOGGER.debug("Starting shore excursions import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationPrams = new HashMap<>();
        authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            // Session initialization
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final ShorexesApi shorexesApi = new ShorexesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Cleaning existing excursions
            ImportersUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/excursion\"]");

            // Importing excursions
            List<Shorex> shorexes;
            int apiPage = 1, itemsWritten = 0;

            LOGGER.debug("Importing shore excursions");

            do {
                shorexes = shorexesApi.shorexesGet(null, apiPage, pageSize, null);

                for (Shorex shorex : shorexes) {
                    LOGGER.trace("Importing shore excursion: {}", shorex.getShorexName());

                    try {
                        // Getting cities with the city id read from the shore
                        // excursion
                        Integer cityId = shorex.getCities().size() > 0 ? shorex.getCities().get(0).getCityId() : null;

                        if (cityId == null) {
                            throw new ImporterException("Shore excursion have no city");
                        }

                        // TODO create cache of cityId / Resource in order to speed up the process
                        Iterator<Resource> portsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com//element(*,cq:Page)[" +
                                                "jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/port\" " +
                                                "and jcr:content/cityId=\"" + cityId + "\"]",
                                        "xpath");

                        if (!portsResources.hasNext()) {
                            throw new ImporterException("Cannot find city with id " + cityId);
                        }

                        while (portsResources.hasNext()) {
                            // Getting port page
                            Page portPage = portsResources.next().adaptTo(Page.class);

                            if (portPage == null) {
                                throw new ImporterException("Error getting port page " + cityId);
                            }

                            LOGGER.trace("Found port {} with ID {}", portPage.getTitle(), cityId);

                            // Creating subpage "excursions" if not present
                            Page excursionsPage;
                            if (portPage.hasChild(WcmConstants.NN_EXCURSIONS)) {
                                excursionsPage = pageManager.getPage(portPage.getPath() + "/" + WcmConstants.NN_EXCURSIONS);
                            } else {
                                excursionsPage = pageManager.create(portPage.getPath(), WcmConstants.NN_EXCURSIONS,
                                        "/apps/silversea/silversea-com/templates/page", "Excursions", false);

                                LOGGER.trace("{} page is not existing, creating it", excursionsPage.getPath());
                            }

                            final Page excursionPage = pageManager.create(excursionsPage.getPath(),
                                    JcrUtil.createValidChildName(excursionsPage.adaptTo(Node.class),
                                            StringsUtils.getFormatWithoutSpecialCharacters(shorex.getShorexName())),
                                    WcmConstants.PAGE_TEMPLATE_EXCURSION,
                                    StringsUtils.getFormatWithoutSpecialCharacters(shorex.getShorexName()), false);

                            LOGGER.trace("Creating excursion {} in city {}", shorex.getShorexName(),
                                    portPage.getPath());

                            // If excursion is created, set the properties
                            if (excursionPage == null) {
                                throw new ImporterException(
                                        "Cannot create excursion page for shore excursion " + shorex.getShorexName());
                            }

                            Node excursionPageContentNode = excursionPage.getContentResource().adaptTo(Node.class);

                            if (excursionPageContentNode == null) {
                                throw new ImporterException(
                                        "Cannot set properties for shore excursion " + shorex.getShorexName());
                            }

                            excursionPageContentNode.setProperty(JcrConstants.JCR_TITLE, shorex.getShorexName());
                            excursionPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
                                    shorex.getShortDescription());
                            excursionPageContentNode.setProperty("codeExcursion", shorex.getShorexCod());
                            excursionPageContentNode.setProperty("apiLongDescription", shorex.getDescription());
                            excursionPageContentNode.setProperty("pois", shorex.getPointsOfInterests());
                            excursionPageContentNode.setProperty("shorexId", shorex.getShorexId());

                            // Set livecopy mixin
                            if (!LanguageHelper.getLanguage(portPage).equals("en")) {
                                excursionPageContentNode.addMixin("cq:LiveRelationship");
                            }

                            LOGGER.trace("Shore excursion {} successfully created", excursionPage.getPath());

                            successNumber++;
                            itemsWritten++;

                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} shore excursions imported, saving session", +itemsWritten);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }
                        }
                    } catch (WCMException | RepositoryException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    } catch (ImporterException e) {
                        errorNumber++;

                        LOGGER.warn("Import error {}", e.getMessage());
                    }
                }

                apiPage++;
            } while (shorexes.size() > 0);

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateShoreExcursions");
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read shore excursions from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Cannot import excursions", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending shore excursions import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    /**
     * TODO it seems a lot of elements are marked as modified on API, compare API data against CRX data before update
     */
    @Override
    public ImportResult updateShoreExcursions() {
        LOGGER.debug("Starting shore excursions update");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final ShorexesApi shorexesApi = new ShorexesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage, "lastModificationDateShoreExcursions");

            LOGGER.debug("Last import date for shore excursions {}", lastModificationDate);

            int itemsWritten = 0, apiPage = 1;
            List<Shorex77> excursions;

            do {
                final ApiResponse<List<Shorex77>> apiResponse = shorexesApi.shorexesGetChangesWithHttpInfo(lastModificationDate,
                        apiPage, pageSize, null);
                excursions = apiResponse.getData();

                // TODO replace by header
                LOGGER.trace("Total excursions : {}, page : {}, excursions for this page : {}", excursions.size(), apiPage, excursions.size());

                for (Shorex77 excursion : excursions) {
                    final String excursionName = excursion.getShorexName();

                    LOGGER.debug("Updating excursion: {}", excursionName);

                    try {
                        // Getting all the excursion pages with the current shorexId
                        // TODO create cache of shorexId / Resource in order to speed up the process
                        Iterator<Resource> excursionsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com//element(*,cq:Page)[" +
                                        "jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/excursion\"" +
                                        " and jcr:content/shorexId=\"" + excursion.getShorexId() + "\"]", "xpath");

                        if (excursionsResources.hasNext()) {
                            // if excursions are found, update it
                            while (excursionsResources.hasNext()) {
                                final Resource excursionResource = excursionsResources.next();
                                final Page excursionPage = excursionResource.adaptTo(Page.class);

                                LOGGER.trace("Updating excursion {}", excursionName);

                                if (excursionPage == null) {
                                    throw new ImporterException("Cannot set excursion page " + excursionName);
                                }

                                // depending of the city status, mark the page to be activated or deactivated
                                if (BooleanUtils.isTrue(excursion.getIsDeleted())) {
                                    final Node excursionContentNode = excursionPage.getContentResource().adaptTo(Node.class);

                                    if (excursionContentNode == null) {
                                        throw new ImporterException("Cannot set properties for excursion " + excursionName);
                                    }

                                    excursionContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

                                    LOGGER.trace("Excursion {} is marked to be deactivated", excursionName);
                                } else {
                                    final Node excursionContentNode = updateExcursionContentNode(excursion, excursionPage);
                                    excursionContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                    LOGGER.trace("Excursion {} is marked to be activated", excursionName);
                                }
                            }
                        } else {
                            // Getting cities with the city id read from the excursion
                            Integer cityId = excursion.getCities().size() > 0 ? excursion.getCities().get(0).getCityId() : null;

                            if (cityId == null) {
                                throw new ImporterException("Land program have no city");
                            }

                            // else create excursion page for each language
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

                                // Creating subpage "excursion" if not present
                                Page excursionsPage;
                                if (portPage.hasChild(WcmConstants.NN_EXCURSIONS)) {
                                    excursionsPage = pageManager.getPage(portPage.getPath() + "/" + WcmConstants.NN_EXCURSIONS);
                                } else {
                                    excursionsPage = pageManager.create(portPage.getPath(), WcmConstants.NN_EXCURSIONS,
                                            WcmConstants.PAGE_TEMPLATE_PAGE, "Excursions", false);

                                    LOGGER.trace("{} page is not existing, creating it", excursionsPage.getPath());
                                }

                                // Creating excursion page
                                final Page excursionPage = pageManager.create(excursionsPage.getPath(),
                                        JcrUtil.createValidChildName(excursionsPage.adaptTo(Node.class),
                                                StringsUtils.getFormatWithoutSpecialCharacters(excursionName)),
                                        WcmConstants.PAGE_TEMPLATE_EXCURSION,
                                        StringsUtils.getFormatWithoutSpecialCharacters(excursionName), false);

                                LOGGER.trace("Creating excursion {} in city {}", excursionName, portPage.getPath());

                                // If excursion is created, set the properties
                                if (excursionPage == null) {
                                    throw new ImporterException(
                                            "Cannot create excursion page for excursion " + excursionName);
                                }

                                final Node excursionContentNode = updateExcursionContentNode(excursion, excursionPage);
                                excursionContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                LOGGER.trace("Excursion {} successfully created", excursionPage.getPath());
                            }
                        }

                        successNumber++;
                        itemsWritten++;

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} excursions imported, saving session", +itemsWritten);
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
            } while (excursions.size() > 0);

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateShoreExcursions");
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read excursions from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public void importOneShoreExcursion(String shoreExcursionId) {
        // TODO implement
    }

    /**
     * Update excursion properties from API
     *
     * @param excursion     excursion object from API
     * @param excursionPage page of the excursion
     * @return the content node of the excursion page, updated
     * @throws ImporterException if the excursion page cannot be updated
     */
    private Node updateExcursionContentNode(final Shorex77 excursion, final Page excursionPage) throws ImporterException {
        final Node excursionContentNode = excursionPage.getContentResource().adaptTo(Node.class);

        if (excursionContentNode == null) {
            throw new ImporterException("Cannot set properties for excursion " + excursion.getShorexName());
        }

        try {
            excursionContentNode.setProperty(JcrConstants.JCR_TITLE, excursion.getShorexName());
            excursionContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
                    excursion.getShortDescription());
            excursionContentNode.setProperty("codeExcursion", excursion.getShorexCod());
            excursionContentNode.setProperty("apiLongDescription", excursion.getDescription());
            excursionContentNode.setProperty("pois", excursion.getPointsOfInterests());
            excursionContentNode.setProperty("shorexId", excursion.getShorexId());

            // Set livecopy mixin
            if (!LanguageHelper.getLanguage(excursionPage).equals("en")) {
                excursionContentNode.addMixin("cq:LiveRelationship");
            }
        } catch (RepositoryException e) {
            throw new ImporterException("Cannot set properties for excursion " + excursion.getShorexName(), e);
        }

        return excursionContentNode;
    }
}
