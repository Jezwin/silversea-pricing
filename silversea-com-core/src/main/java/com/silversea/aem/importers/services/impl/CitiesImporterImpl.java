package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CitiesImporter;
import io.swagger.client.ApiException;
import io.swagger.client.api.CitiesApi;
import io.swagger.client.model.City;
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
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author aurelienolivier
 */
@Service
@Component(label = "Silversea.com - Cities importer")
public class CitiesImporterImpl extends BaseImporter implements CitiesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CitiesImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importCities() throws IOException {
        final String authorizationHeader = getAuthorizationHeader("/api/v1/cities");

        CitiesApi citiesApi = new CitiesApi();
        citiesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            Page citiesRootPage = pageManager.getPage(ImportersConstants.BASEPATH_PORTS);

            List<City> cities;
            int i = 1;

            do {
                cities = citiesApi.citiesGet(null, null, i, 100, null, null, null);

                int j = 0;

                for (City city : cities) {
                    LOGGER.debug("Importing city: {}", city.getCityName());

                    final String portFirstLetter = String.valueOf(city.getCityName().charAt(0));
                    final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

                    Page portFirstLetterPage;

                    if (citiesRootPage.hasChild(portFirstLetterName)) {
                        portFirstLetterPage = pageManager
                                .getPage(ImportersConstants.BASEPATH_PORTS + "/" + portFirstLetterName);

                        LOGGER.debug("Page {} already exists", portFirstLetterName);
                    } else {
                        portFirstLetterPage = pageManager.create(citiesRootPage.getPath(), portFirstLetterName,
                                "/apps/silversea/silversea-com/templates/page", portFirstLetter, false);

                        LOGGER.debug("Creating page {}", portFirstLetterName);
                    }

                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/cityCode=\"" + city.getCityCod() + "\"]", "xpath");

                    Page portPage;

                    if (resources.hasNext()) {
                        portPage = resources.next().adaptTo(Page.class);

                        LOGGER.debug("Port page {} with ID {} already exists", city.getCityName(), city.getCityCod());
                    } else {
                        portPage = pageManager.create(portFirstLetterPage.getPath(),
                                JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
                                        city.getCityName()),
                                "/apps/silversea/silversea-com/templates/port", city.getCityName(), false);

                        LOGGER.debug("Creating port {}", city.getCityName());
                    }

                    Node portPageContentNode = portPage.getContentResource().adaptTo(Node.class);

                    if (portPageContentNode.hasProperty(JcrConstants.JCR_TITLE)
                            && portPageContentNode.getProperty("jcr:title").getString().equals(city.getCityName())) {
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
        } catch (ApiException | WCMException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing cities", e);
        }
    }

    @Override
    public void importCity(final String cityId) {
    }
}
