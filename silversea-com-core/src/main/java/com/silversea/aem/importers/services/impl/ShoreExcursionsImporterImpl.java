package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.s7dam.set.MediaSet;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ShoreExcursionsImporter;
import com.silversea.aem.importers.utils.CruisesImportUtils;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.utils.StringsUtils;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.model.Shorex;
import io.swagger.client.model.Shorex77;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.mime.MimeTypeService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silversea.aem.constants.WcmConstants.PATH_DAM_SILVERSEA;

@Service
@Component
public class ShoreExcursionsImporterImpl implements ShoreExcursionsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShoreExcursionsImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private MimeTypeService mimeService;

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

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory
                .getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final ShorexesApi shorexesApi = new ShorexesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Cleaning existing excursions
            ImportersUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    +
                    "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/excursion\"]");

            // cities mapping

            final Map<Integer, Map<String, Page>> portsMapping = ImportersUtils
                    .getItemsPageMapping(resourceResolver,
                            "/jcr:root/content/silversea-com//element(*,cq:PageContent)"
                                    + "[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]",
                            "cityId");

            // features
            final Map<Integer, String> featuresMapping = CruisesImportUtils.getFeaturesMap(resourceResolver);

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
                        final Integer cityId = shorex.getCities().size() > 0 ? shorex.getCities().get(0).getCityId()
                                : null;

                        if (cityId == null) {
                            throw new ImporterException("Shore excursion have no city");
                        }

                        if (!portsMapping.containsKey(cityId)) {
                            throw new ImporterException("Cannot find city with id " + cityId);
                        }

                        for (Map.Entry<String, Page> portsPage : portsMapping.get(cityId).entrySet()) {
                            // Getting port page
                            Page portPage = portsPage.getValue();

                            if (portPage == null) {
                                throw new ImporterException("Error getting port page " + cityId);
                            }

                            LOGGER.trace("Found port {} with ID {}", portPage.getTitle(), cityId);

                            // Creating subpage "excursions" if not present
                            Page excursionsPage;
                            if (portPage.hasChild(WcmConstants.NN_EXCURSIONS)) {
                                excursionsPage = pageManager
                                        .getPage(portPage.getPath() + "/" + WcmConstants.NN_EXCURSIONS);
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
                            excursionPageContentNode.setProperty("note", shorex.getNote());


                            if (StringUtils.isNotBlank(shorex.getSymbols())) {
                                final String[] symbolsIDs = shorex.getSymbols().split(",");

                                if (symbolsIDs.length > 0) {
                                    List<String> features = new ArrayList<>();
                                    for (final String symbolId : symbolsIDs) {
                                        try {
                                            final int symbolIdInt = Integer.parseInt(symbolId);
                                            if (featuresMapping.containsKey(symbolIdInt)) {
                                                features.add(featuresMapping.get(symbolIdInt));
                                            }
                                        } catch (NumberFormatException ignored) {
                                        }
                                    }

                                    excursionPageContentNode.setProperty("cq:tags",
                                            features.toArray(new String[features.size()]));
                                }
                            }

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

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} shorex imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read shore excursions from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Cannot import excursions", e);
        }

        LOGGER.debug("Ending shore excursions import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult disactiveAllItemDeltaByAPI() {
        LOGGER.debug("Starting shore excursions disactive delta API");

        int successNumber = 0, errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (ResourceResolver resourceResolver = resourceResolverFactory
                .getServiceResourceResolver(authenticationParams)) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            ShorexesApi shorexesApi = new ShorexesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // excursions mapping
            Map<Integer, Map<String, Page>> excursionsMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)"
                            + "[sling:resourceType=\"silversea/silversea-com/components/pages/excursion\"]",
                    "shorexId");

            int itemsWritten = 0, page = 1, perPage = 100;
            Shorex shorexToDisactive = null;
            List<Shorex> excursionsAPI = shorexesApi.shorexesGet(null, page, perPage, null);
            List<Shorex> excursionsListAPI = new ArrayList<>();

            LOGGER.debug("Check all excursion in jcr: {}", excursionsMapping.size());

            while (excursionsAPI.size() != 0) {
                excursionsListAPI.addAll(excursionsAPI);
                page++;
                excursionsAPI = shorexesApi.shorexesGet(null, page, perPage, null);
            }

            for (Map.Entry<Integer, Map<String, Page>> excursions : excursionsMapping.entrySet()) {
                shorexToDisactive = null;
                Integer shorexID = excursions.getKey();
                LOGGER.trace("Check shorexID: {}", shorexID);

                for (Shorex eAPI : excursionsListAPI) {

                    if (shorexID.intValue() == (eAPI.getShorexId().intValue())) {
                        shorexToDisactive = eAPI;
                    }
                }
                if (shorexToDisactive == null) {

                    for (Map.Entry<String, Page> excursionsPages : excursionsMapping
                            .get(shorexID).entrySet()) {
                        Page excursionPage = excursionsPages.getValue();

                        LOGGER.debug("Updating excursion {}", shorexID);

                        if (excursionPage == null) {
                            throw new ImporterException(
                                    "Cannot set excursion page " + shorexID);
                        }

                        Node excursionContentNode = excursionPage.getContentResource().adaptTo(Node.class);

                        if (excursionContentNode == null) {
                            throw new ImporterException(
                                    "Cannot set properties for excursion " + shorexID);
                        }

                        excursionContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

                        LOGGER.trace("Excursion {} is marked to be deactivated", shorexID);
                    }
                    successNumber++;
                    itemsWritten++;

                }
                try {
                    if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                        try {
                            session.save();

                            LOGGER.debug("{} excursions imported, saving session", +itemsWritten);
                        } catch (RepositoryException e) {
                            session.refresh(true);
                        }
                    }
                } catch (RepositoryException e) {
                    errorNumber++;

                    LOGGER.warn("Import error {}", e.getMessage());
                }
            }


            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} shorex imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read excursions from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error writing data", e);
        }

        LOGGER.debug("Ending excursions disactive update, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    /**
     * TODO it seems a lot of elements are marked as modified on API, compare API
     * data against CRX data before update
     */
    @Override
    public ImportResult updateShoreExcursions() {
        LOGGER.debug("Starting shore excursions update");

        int successNumber = 0, errorNumber = 0, apiPage = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory
                .getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final ShorexesApi shorexesApi = new ShorexesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
                    "lastModificationDateShoreExcursions");

            LOGGER.debug("Last import date for shore excursions {}", lastModificationDate);

            // cities mapping
            final Map<Integer, Map<String, Page>> portsMapping = ImportersUtils
                    .getItemsPageMapping(resourceResolver,
                            "/jcr:root/content/silversea-com//element(*,cq:PageContent)"
                                    + "[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]",
                            "cityId");

            // excursions mapping
            final Map<Integer, Map<String, Page>> excursionsMapping = ImportersUtils.getItemsPageMapping(
                    resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)"
                            + "[sling:resourceType=\"silversea/silversea-com/components/pages/excursion\"]",
                    "shorexId");

            // features
            final Map<Integer, String> featuresMapping = CruisesImportUtils.getFeaturesMap(resourceResolver);

            int itemsWritten = 0;
            List<Shorex77> excursions;

            do {
                final ApiResponse<List<Shorex77>> apiResponse = shorexesApi
                        .shorexesGetChangesWithHttpInfo(lastModificationDate, apiPage, pageSize, null);
                excursions = apiResponse.getData();

                // TODO replace by header
                LOGGER.trace("Total excursions : {}, page : {}, excursions for this page : {}", excursions.size(),
                        apiPage, excursions.size());

                for (Shorex77 excursion : excursions) {
                    final String excursionName = excursion.getShorexName();

                    LOGGER.debug("Updating excursion: {}", excursionName);

                    try {
                        if (excursionsMapping.containsKey(excursion.getShorexId())) {
                            // if landPrograms are found, update it
                            for (Map.Entry<String, Page> excursionsPages : excursionsMapping
                                    .get(excursion.getShorexId()).entrySet()) {
                                final Page excursionPage = excursionsPages.getValue();

                                LOGGER.trace("Updating excursion {}", excursionName);

                                if (excursionPage == null) {
                                    throw new ImporterException("Cannot set excursion page " + excursionName);
                                }

                                // depending of the city status, mark the page to be activated or deactivated
                                if (BooleanUtils.isTrue(excursion.getIsDeleted())) {
                                    final Node excursionContentNode = excursionPage.getContentResource()
                                            .adaptTo(Node.class);

                                    if (excursionContentNode == null) {
                                        throw new ImporterException(
                                                "Cannot set properties for excursion " + excursionName);
                                    }
                                    excursionContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
                                    String destinationPath = assetPath(excursion);
                                    String imageDam = ImportersUtils
                                            .upsertAsset(session, resourceResolver, mimeService, excursion.getImageUrl(), destinationPath);
                                    String image2Dam = ImportersUtils
                                            .upsertAsset(session, resourceResolver, mimeService, excursion.getImageUrl2(), destinationPath);
                                    String image3Dam = ImportersUtils
                                            .upsertAsset(session, resourceResolver, mimeService, excursion.getImageUrl3(), destinationPath);
                                    String image4Dam = ImportersUtils
                                            .upsertAsset(session, resourceResolver, mimeService, excursion.getImageUrl4(), destinationPath);


                                    excursionContentNode.setProperty("image", imageDam);
                                    excursionContentNode.setProperty("image2", image2Dam);
                                    excursionContentNode.setProperty("image3", image3Dam);
                                    excursionContentNode.setProperty("image4", image4Dam);
                                    excursionContentNode.setProperty("assetSelectionReference_api",
                                            BaseImporter.createMediaSet(resourceResolver,
                                                    resourceResolver
                                                            .resolve(PATH_DAM_SILVERSEA + "/other-resources/shorex/"),
                                                    excursionName, imageDam, image2Dam, image3Dam, image4Dam).getPath());

                                    LOGGER.trace("Excursion {} is marked to be deactivated", excursionName);
                                } else {
                                    final Node excursionContentNode = updateExcursionContentNode(excursion,
                                            excursionPage, featuresMapping, resourceResolver, session);
                                    excursionContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                    LOGGER.trace("Excursion {} is marked to be activated", excursionName);
                                }
                            }
                        } else {
                            // Getting cities with the city id read from the excursion
                            Integer cityId = excursion.getCities().size() > 0 ? excursion.getCities().get(0).getCityId()
                                    : null;

                            if (cityId == null) {
                                throw new ImporterException("Land program have no city");
                            }

                            if (!portsMapping.containsKey(cityId)) {
                                throw new ImporterException("Cannot find city with id " + cityId);
                            }

                            for (Map.Entry<String, Page> portsPage : portsMapping.get(cityId).entrySet()) {
                                // Getting port page
                                Page portPage = portsPage.getValue().adaptTo(Page.class);

                                if (portPage == null) {
                                    throw new ImporterException("Error getting port page " + cityId);
                                }

                                LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                                // Creating subpage "excursion" if not present
                                Page excursionsPage;
                                if (portPage.hasChild(WcmConstants.NN_EXCURSIONS)) {
                                    excursionsPage = pageManager
                                            .getPage(portPage.getPath() + "/" + WcmConstants.NN_EXCURSIONS);
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

                                final Node excursionContentNode = updateExcursionContentNode(excursion, excursionPage,
                                        featuresMapping, resourceResolver, session);
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
                    } catch (RepositoryException | ImporterException | WCMException | PersistenceException e) {
                        errorNumber++;

                        LOGGER.warn("Import error {}", e.getMessage());
                    }
                }

                apiPage++;
            } while (excursions.size() > 0);

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateShoreExcursions", true);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} shorex imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read excursions from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error writing data", e);
        }

        LOGGER.debug("Ending excursions update, success: {}, error: {}, api calls: {}", +successNumber, +errorNumber,
                apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    private String assetPath(Shorex77 shorex77) {
        return PATH_DAM_SILVERSEA + "/other-resources/shorex/" + shorex77.getShorexName();
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
    private Node updateExcursionContentNode(final Shorex77 excursion, final Page excursionPage,
                                            Map<Integer, String> featuresMapping, ResourceResolver resourceResolver, Session session)
            throws ImporterException, PersistenceException {
        final Node excursionContentNode = excursionPage.getContentResource().adaptTo(Node.class);

        if (excursionContentNode == null) {
            throw new ImporterException("Cannot set properties for excursion " + excursion.getShorexName());
        }

        try {
            excursionContentNode.setProperty(JcrConstants.JCR_TITLE, excursion.getShorexName());
            excursionContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, excursion.getShortDescription());
            excursionContentNode.setProperty("codeExcursion", excursion.getShorexCod());
            excursionContentNode.setProperty("apiLongDescription", excursion.getDescription());
            excursionContentNode.setProperty("pois", excursion.getPointsOfInterests());
            excursionContentNode.setProperty("shorexId", excursion.getShorexId());
            excursionContentNode.setProperty("note", excursion.getNote());

            excursionContentNode.setProperty("okForDebarks", excursion.isOkForDebarks());
            excursionContentNode.setProperty("okForEmbarks", excursion.isOkForEmbarks());
            excursionContentNode.setProperty("shorexCategory", excursion.getShorexCategory());
            String destinationPath = assetPath(excursion);

            String imageDam = ImportersUtils
                    .upsertAsset(session, resourceResolver, mimeService, excursion.getImageUrl(), destinationPath);
            String image2Dam = ImportersUtils
                    .upsertAsset(session, resourceResolver, mimeService, excursion.getImageUrl2(), destinationPath);
            String image3Dam = ImportersUtils
                    .upsertAsset(session, resourceResolver, mimeService, excursion.getImageUrl3(), destinationPath);
            String image4Dam = ImportersUtils
                    .upsertAsset(session, resourceResolver, mimeService, excursion.getImageUrl4(), destinationPath);


            excursionContentNode.setProperty("image", imageDam);
            excursionContentNode.setProperty("image2", image2Dam);
            excursionContentNode.setProperty("image3", image3Dam);
            excursionContentNode.setProperty("image4", image4Dam);
            excursionContentNode
                    .setProperty("assetSelectionReference_api", BaseImporter.createMediaSet(resourceResolver,
                            resourceResolver.resolve(PATH_DAM_SILVERSEA + "/other-resources/shorex/"),
                            excursion.getShorexName(), imageDam, image2Dam, image3Dam, image4Dam).getPath());
            if (StringUtils.isNotBlank(excursion.getSymbols())) {
                final String[] symbolsIDs = excursion.getSymbols().split(",");

                if (symbolsIDs.length > 0) {
                    List<String> features = new ArrayList<>();
                    for (final String symbolId : symbolsIDs) {
                        try {
                            final int symbolIdInt = Integer.parseInt(symbolId);
                            if (featuresMapping.containsKey(symbolIdInt)) {
                                features.add(featuresMapping.get(symbolIdInt));
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    excursionContentNode.setProperty("cq:tags", features.toArray(new String[features.size()]));
                }
            }

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
