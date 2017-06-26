package com.silversea.aem.importers.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.City;

@Service
@Component(label = "Silversea.com - Cities importer")
public class CitiesImporterImpl implements CitiesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CitiesImporterImpl.class);

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
            LOGGER.debug("Cities importer login exception ", e);
        }
    }

    @Override
    public ImportResult importAllCities() {
    	init();
        LOGGER.debug("Starting cities import");

        int successNumber = 0;
        int errorNumber = 0;

        try {

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Getting paths to import data
            LOGGER.trace("Getting root page : {}", apiConfig.apiRootPath("citiesUrl"));
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final List<String> locales = ImporterUtils.getSiteLocales(pageManager);

            // Iterating over locales to import cities
            for (String locale : locales) {
                final Page citiesRootPage = ImporterUtils.getPagePathByLocale(pageManager, rootPage, locale);

                LOGGER.debug("Cleaning already imported cities");

                Iterator<Page> children = citiesRootPage.listChildren();
                while (children.hasNext()) {
                    final Page child = children.next();

                    try {
                        LOGGER.trace("trying to remove {}", child.getPath());

                        session.removeItem(child.getPath());
                        session.save();
                    } catch (RepositoryException e) {
                        LOGGER.error("Cannot clean already existing cities");
                    }
                }

                LOGGER.debug("Importing cities for locale \"{}\"", locale);

                int j = 0;

                if (citiesRootPage != null) {
                    List<City> cities;
                    int i = 1;

                    do {
                        cities = apiCallService.getCities(i, pageSize);

                        for (City city : cities) {
                            LOGGER.trace("Importing city: {}", city.getCityName());

                            try {
                                String portFirstLetter;

                                if (city.getCityName() == null) {
                                    throw new ImporterException("City name is null");
                                }

                                // Port parent page initialization
                                portFirstLetter = String.valueOf(city.getCityName().charAt(0));

                                final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

                                Page portFirstLetterPage = pageManager
                                        .getPage(citiesRootPage.getPath() + "/" + portFirstLetterName);

                                if (portFirstLetterPage == null) {
                                    portFirstLetterPage = pageManager.create(citiesRootPage.getPath(),
                                            portFirstLetterName, TemplateConstants.PATH_PAGE_PORT, portFirstLetter,
                                            false);

                                    LOGGER.trace("{} page is not existing, creating it", portFirstLetterName);
                                }

                                // Creating port page
                                final Page portPage = pageManager.create(portFirstLetterPage.getPath(),
                                        JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
                                                StringHelper.getFormatWithoutSpecialCharcters(city.getCityName())),
                                        TemplateConstants.PATH_PORT,
                                        StringHelper.getFormatWithoutSpecialCharcters(city.getCityName()), false);

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

                                LOGGER.trace("Port {} successfully created", portPage.getPath());

                                successNumber++;
                                j++;

                                if (j % sessionRefresh == 0 && session.hasPendingChanges()) {
                                    try {
                                        session.save();

                                        LOGGER.debug("{} cities imported, saving session", +j);
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
                    } while (cities.size() > 0 && cities != null);

                    ImporterUtils.setLastModificationDate(pageManager, session,
                            apiConfig.apiRootPath("citiesUrl"), "lastModificationDate");
                }
            }

            resourceResolver.close();
        } catch (ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cities from API", e);
        }

        LOGGER.debug("Ending cities import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public void updateCities() {
    }

    @Override
    public void importOneCity(final String cityId) {
    }
}
