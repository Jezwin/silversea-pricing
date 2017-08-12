package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.utils.StringsUtils;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.CitiesApi;
import io.swagger.client.model.City;
import io.swagger.client.model.City77;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Service
@Component
public class CitiesImporterImpl implements CitiesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CitiesImporterImpl.class);

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
    public ImportResult importAllItems() {
        LOGGER.debug("Starting cities import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final CitiesApi citiesApi = new CitiesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            ImportersUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/portslist\"]");

            // Getting paths to import data
            LOGGER.trace("Getting root page : {}", apiConfig.apiRootPath("citiesUrl"));
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final List<String> locales = ImportersUtils.getSiteLocales(pageManager);

            // Iterating over locales to import cities
            for (String locale : locales) {
                final Page citiesRootPage = ImportersUtils.getPagePathByLocale(pageManager, rootPage, locale);

                if (citiesRootPage == null) {
                    throw new ImporterException("Cities root page does not exists");
                }

                LOGGER.debug("Importing cities for locale \"{}\"", locale);

                int itemsWritten = 0, apiPage = 1;
                List<City> cities;

                do {
                    cities = citiesApi.citiesGet(null, null, apiPage, pageSize, null, null, null);

                    for (City city : cities) {
                        LOGGER.trace("Importing city: {}", city.getCityName());

                        try {
                            final Page portPage = createPortPage(pageManager, citiesRootPage, city.getCityName());

                            LOGGER.trace("Creating port {}", city.getCityName());

                            // If port is created, set the properties
                            if (portPage == null) {
                                throw new ImporterException("Cannot create port page for city " + city.getCityName());
                            }

                            Node portPageContentNode = portPage.getContentResource().adaptTo(Node.class);

                            if (portPageContentNode == null) {
                                throw new ImporterException("Cannot set properties for city " + city.getCityName());
                            }

                            portPageContentNode.setProperty(JcrConstants.JCR_TITLE, city.getCityName());
                            portPageContentNode.setProperty("apiTitle", city.getCityName());
                            portPageContentNode.setProperty("apiDescription", city.getShortDescription());
                            portPageContentNode.setProperty("apiLongDescription", city.getDescription());
                            portPageContentNode.setProperty("cityCode", city.getCityCod());
                            portPageContentNode.setProperty("cityId", city.getCityId());
                            portPageContentNode.setProperty("latitude", city.getLatitude());
                            portPageContentNode.setProperty("longitude", city.getLongitude());
                            portPageContentNode.setProperty("countryId", city.getCountryId());
                            portPageContentNode.setProperty("countryIso2", city.getCountryIso2());
                            portPageContentNode.setProperty("countryIso3", city.getCountryIso3());

                            // Set livecopy mixin
                            if (!locale.equals("en")) {
                                portPageContentNode.addMixin("cq:LiveRelationship");
                            }

                            LOGGER.trace("Port {} successfully created", portPage.getPath());

                            successNumber++;
                            itemsWritten++;

                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} cities imported, saving session", +itemsWritten);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }
                        } catch (RepositoryException | ImporterException e) {
                            errorNumber++;

                            LOGGER.error("Import error", e);
                        }
                    }

                    apiPage++;
                } while (cities.size() > 0);
            }

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDate");
        } catch (LoginException | ImporterException | RepositoryException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cities from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending cities import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    /**
     * TODO it seems a lot of elements are marked as modified on API, compare API data against CRX data before update
     */
    @Override
    public ImportResult updateItems() {
        LOGGER.debug("Starting cities update");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final CitiesApi citiesApi = new CitiesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final String lastModificationDate = ImporterUtils.getDateFromPageProperties(rootPage, "lastModificationDate");

            LOGGER.debug("Last import date for ports {}", lastModificationDate);

            final List<String> locales = ImportersUtils.getSiteLocales(pageManager);

            int itemsWritten = 0, apiPage = 1;
            List<City77> cities;

            do {
                final ApiResponse<List<City77>> apiResponse = citiesApi.citiesGetChangesWithHttpInfo(lastModificationDate, apiPage, pageSize, null, null, null);
                cities = apiResponse.getData();

                // TODO replace by header
                LOGGER.trace("Total cities : {}, page : {}, cities for this page : {}", cities.size(), apiPage, cities.size());

                for (City77 city : cities) {
                    LOGGER.debug("Updating city: {}", city.getCityName());

                    try {
                        // Getting all the port pages with the current cityId
                        // TODO create cache of cityId / Resource in order to speed up the process
                        Iterator<Resource> portsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com//element(*,cq:Page)[" +
                                        "jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/port\"" +
                                        " and jcr:content/cityId=\"" + city.getCityId() + "\"]", "xpath");

                        if (portsResources.hasNext()) {
                            // if ports are found, update it
                            while (portsResources.hasNext()) {
                                final Resource portResource = portsResources.next();
                                final Page portPage = portResource.adaptTo(Page.class);

                                LOGGER.trace("Updating port {}", city.getCityName());

                                if (portPage == null) {
                                    throw new ImporterException("Cannot set port page " + city.getCityName());
                                }

                                // depending of the city status, mark the page to be activated or deactivated
                                if (BooleanUtils.isTrue(city.getIsDeleted())) {
                                    final Node portContentNode = portPage.getContentResource().adaptTo(Node.class);

                                    if (portContentNode == null) {
                                        throw new ImporterException("Cannot set properties for city " + city.getCityName());
                                    }

                                    portContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

                                    LOGGER.trace("Port {} is marked to be deactivated", city.getCityName());
                                } else {
                                    final Node portContentNode = updatePortContentNode(city, portPage);

                                    portContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                    LOGGER.trace("Port {} is marked to be activated", city.getCityName());
                                }
                            }
                        } else {
                            // else create port page for each language
                            for (final String locale : locales) {
                                final Page citiesRootPage = ImportersUtils
                                        .getPagePathByLocale(pageManager, rootPage, locale);
                                final Page portPage = createPortPage(pageManager, citiesRootPage, city.getCityName());

                                LOGGER.trace("Creating port {}", city.getCityName());

                                // If port is created, set the properties
                                if (portPage == null) {
                                    throw new ImporterException("Cannot create port page for city " + city.getCityName());
                                }

                                final Node portContentNode = updatePortContentNode(city, portPage);
                                portContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                LOGGER.trace("Port {} successfully created", portPage.getPath());
                            }
                        }

                        successNumber++;
                        itemsWritten++;

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} cities imported, saving session", +itemsWritten);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (RepositoryException | ImporterException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    }
                }

                apiPage++;
            } while (cities.size() > 0);

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDate");
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cities from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending cities update, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public void importOneItem(final String cityId) {
        // TODO
    }

    @Override
    public JSONObject getJsonMapping() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        JSONObject jsonObject = new JSONObject();

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);

            Iterator<Resource> cities = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "xpath");

            while (cities.hasNext()) {
                Resource city = cities.next();

                Resource childContent = city.getChild(JcrConstants.JCR_CONTENT);

                if (childContent != null) {
                    ValueMap childContentProperties = childContent.getValueMap();
                    String cityId = childContentProperties.get("cityId", String.class);

                    if (cityId != null) {
                        try {
                            if (jsonObject.has(cityId)) {
                                final JSONArray jsonArray = jsonObject.getJSONArray(cityId);
                                jsonArray.put(city.getPath());

                                jsonObject.put(cityId, jsonArray);
                            } else {
                                jsonObject.put(cityId, Collections.singletonList(city.getPath()));
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Cannot add city {} with path {} to cities array", cityId, city.getPath(), e);
                        }
                    }
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        return jsonObject;
    }

    /**
     * Create a port page, based on path convention
     * /cities/root/page/first-port-name-letter/port-name
     *
     * @param pageManager pageManager used to create the pages
     * @param citiesRootPage root page of the cities in the content tree
     * @param cityName the name of the city
     * @return the port created page
     * @throws ImporterException if the page cannot be created
     */
    private Page createPortPage(PageManager pageManager, Page citiesRootPage, String cityName) throws ImporterException {
        String portFirstLetter;

        try {
            // Port parent page initialization
            portFirstLetter = String.valueOf(cityName.charAt(0));

            final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

            Page portFirstLetterPage = pageManager
                    .getPage(citiesRootPage.getPath() + "/" + portFirstLetterName);

            if (portFirstLetterPage == null) {
                portFirstLetterPage = pageManager.create(citiesRootPage.getPath(),
                        portFirstLetterName, TemplateConstants.PATH_PAGE_PORT, portFirstLetter,
                        false);

                // Set livecopy mixin
                if (!LanguageHelper.getLanguage(citiesRootPage).equals("en")) {
                    portFirstLetterPage.getContentResource().adaptTo(Node.class).addMixin("cq:LiveRelationship");
                }

                LOGGER.trace("{} page is not existing, creating it", portFirstLetterName);
            }

            // Creating port page
            return pageManager.create(portFirstLetterPage.getPath(),
                    JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
                            StringsUtils.getFormatWithoutSpecialCharcters(cityName)),
                    WcmConstants.PAGE_TEMPLATE_PORT,
                    StringsUtils.getFormatWithoutSpecialCharcters(cityName), false);
        } catch (RepositoryException | WCMException e) {
            throw new ImporterException("Port page cannot be created", e);
        }
    }

    /**
     * Update port properties from API
     *
     * @param city city object from API
     * @param portPage page of the port
     * @return the content node of the port page, updated
     * @throws ImporterException if the port page cannot be updated
     */
    private Node updatePortContentNode(City77 city, Page portPage) throws ImporterException {
        final Node portContentNode = portPage.getContentResource().adaptTo(Node.class);

        if (portContentNode == null) {
            throw new ImporterException("Cannot set properties for city " + city.getCityName());
        }

        try {
            portContentNode.setProperty(JcrConstants.JCR_TITLE, city.getCityName());
            portContentNode.setProperty("apiTitle", city.getCityName());
            portContentNode.setProperty("apiDescription", city.getShortDescription());
            portContentNode.setProperty("apiLongDescription", city.getDescription());
            portContentNode.setProperty("cityCode", city.getCityCod());
            portContentNode.setProperty("cityId", city.getCityId());
            portContentNode.setProperty("latitude", city.getLatitude());
            portContentNode.setProperty("longitude", city.getLongitude());
            portContentNode.setProperty("countryId", city.getCountryId());
            portContentNode.setProperty("countryIso2", city.getCountryIso2());
            portContentNode.setProperty("countryIso3", city.getCountryIso3());

            // Set livecopy mixin
            if (!LanguageHelper.getLanguage(portPage).equals("en")) {
                portContentNode.addMixin("cq:LiveRelationship");
            }
        } catch (RepositoryException e) {
            throw new ImporterException("Cannot set properties for city " + city.getCityName(), e);
        }

        return portContentNode;
    }
}
