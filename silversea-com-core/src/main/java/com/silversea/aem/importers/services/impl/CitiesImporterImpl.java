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
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.CitiesApi;
import io.swagger.client.model.City;

/**
 * @author aurelienolivier
 */
@Service
@Component(label = "Silversea.com - Cities importer")
public class CitiesImporterImpl extends BaseImporter implements CitiesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CitiesImporterImpl.class);

    private int errorNumber = 0;
    private int succesNumber = 0;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Override
    public void importData() throws IOException {

        // final String authorizationHeader =
        // getAuthorizationHeader("/api/v1/cities");
        /**
         * authentification pour le swagger
         */
        getAuthentification(apiConfig.getLogin(), apiConfig.getPassword());
        /**
         * Récuperation du domain de l'api Swager
         */
        getApiDomain(apiConfig.getApiBaseDomain());

        final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("citiesUrl"));
        CitiesApi citiesApi = new CitiesApi();
        citiesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

//            Page citiesRootPage = pageManager.getPage(ImportersConstants.BASEPATH_PORTS);
            Page citiesRootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));

            List<City> cities;
            int i = 1;

            do {
                cities = citiesApi.citiesGet(null, null, i, 100, null, null, null);

                int j = 0;

                for (City city : cities) {

                    try {
                        // TODO remove this conditions, just to test
                        // if(j==2){
                        // String test = null;
                        // test.toString();
                        // }

                        LOGGER.debug("Importing city: {}", city.getCityName());
                        String portFirstLetter = "";
                        if (city.getCityName() != null) {
                            portFirstLetter = String.valueOf(city.getCityName().charAt(0));
                        }

                        final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

                        Page portFirstLetterPage;

                        if (citiesRootPage.hasChild(portFirstLetterName)) {
                            portFirstLetterPage = pageManager
                                    .getPage(ImportersConstants.BASEPATH_PORTS + "/" + portFirstLetterName);

                            LOGGER.debug("Page {} already exists", portFirstLetterName);
                        } else {
                            portFirstLetterPage = pageManager.create(citiesRootPage.getPath(), portFirstLetterName,
                                    TemplateConstants.PATH_PAGE_PORT, portFirstLetter, false);

                            LOGGER.debug("Creating page {}", portFirstLetterName);
                        }

                        Iterator<Resource> resources = resourceResolver.findResources(
                                "//element(*,cq:Page)[jcr:content/cityId=\"" + city.getCityId() + "\"]", "xpath");

                        Page portPage;

                        if (resources.hasNext()) {
                            portPage = resources.next().adaptTo(Page.class);

                            LOGGER.debug("Port page {} with ID {} already exists", city.getCityName(),
                                    city.getCityCod());
                        } else {
                            portPage = pageManager.create(portFirstLetterPage.getPath(),
                                    JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
                                            StringHelper.getFormatWithoutSpecialCharcters(city.getCityName())),
                                    TemplateConstants.PATH_PORT,
                                    StringHelper.getFormatWithoutSpecialCharcters(city.getCityName()), false);

                            LOGGER.debug("Creating port {}", city.getCityName());
                        }

                        Node portPageContentNode = portPage.getContentResource().adaptTo(Node.class);

                        if (portPageContentNode.hasProperty(JcrConstants.JCR_TITLE) && portPageContentNode
                                .getProperty("jcr:title").getString().equals(city.getCityName())) {
                            portPageContentNode.setProperty(JcrConstants.JCR_TITLE, city.getCityName());
                        }

                        portPageContentNode.setProperty("apiTitle", city.getCityName());
                        portPageContentNode.setProperty("apiDescription", city.getShortDescription());
                        portPageContentNode.setProperty("apiLongDescription", city.getDescription());
                        portPageContentNode.setProperty("cityCode", city.getCityCod());
                        portPageContentNode.setProperty("cityId", city.getCityId());
                        portPageContentNode.setProperty("latitude", city.getLatitude());
                        portPageContentNode.setProperty("longitude", city.getLongitude());
                        portPageContentNode.setProperty("countryId", city.getCountryId());
                        portPageContentNode.setProperty("countryIso3", city.getCountryIso3());
                        succesNumber = succesNumber + 1;
                        j++;

                        if (j % 100 == 0) {
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
                        LOGGER.debug("Hotel error, number of faulures :", errorNumber);
                        j++;
                    }
                }

                i++;
            } while (cities.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    // save migration date
                    Node rootNode = citiesRootPage.getContentResource().adaptTo(Node.class);
                    rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            resourceResolver.close();
        } catch (ApiException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing cities", e);
        }
    }

    @Override
    public void importCity(final String cityId) {
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public int getSuccesNumber() {
        return succesNumber;
    }
}
