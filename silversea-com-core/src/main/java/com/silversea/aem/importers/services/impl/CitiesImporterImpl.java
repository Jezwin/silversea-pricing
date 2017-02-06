package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.authentication.DigestAuthenticationInfos;
import com.silversea.aem.importers.services.CitiesImporter;
import io.swagger.client.ApiException;
import io.swagger.client.api.CitiesApi;
import io.swagger.client.model.City;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.List;

/**
 * @author aurelienolivier
 */
@Service
@Component(label = "Silversea.com - Cities importer")
public class CitiesImporterImpl implements CitiesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CitiesImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importCities() throws IOException {
        // Get server data used to generate digest authentication
        HttpClient client = new HttpClient();

        GetMethod get = new GetMethod("https://shop.silversea.com/api/v1/cities");
        client.executeMethod(get);

        Header wwwAuthenticateHeader = get.getResponseHeader("WWW-Authenticate");

        if (wwwAuthenticateHeader != null) {
            DigestAuthenticationInfos digestAuthenticationInfos = new DigestAuthenticationInfos(wwwAuthenticateHeader.getValue());
            digestAuthenticationInfos.setCredentials("auolivier@sqli.com", "123qweASD");
            digestAuthenticationInfos.setUri("/api/v1/cities");

            final String authorizationHeader = digestAuthenticationInfos.getHeaderValue();

            LOGGER.error("Authorization header : {}", authorizationHeader);

            CitiesApi citiesApi = new CitiesApi();
            citiesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

            try {
                List<City> result = citiesApi.citiesGet(null, null, null,
                        null, null, null, null);

                ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                Session session = resourceResolver.adaptTo(Session.class);

                Page citiesRootPage = pageManager.getPage(ImportersConstants.BASEPATH_PORTS);

                int i = 0;

                for (City city : result) {
                    LOGGER.error("City: {}", city.getCityName());

                    final String portFirstLetter = String.valueOf(city.getCityName().charAt(0));
                    final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

                    Page portFirstLetterPage;

                    if (citiesRootPage.hasChild(portFirstLetterName)) {
                        portFirstLetterPage = pageManager.getPage(ImportersConstants.BASEPATH_PORTS
                                + "/" + portFirstLetterName);
                    } else {
                        portFirstLetterPage = pageManager.create(citiesRootPage.getPath(),
                                portFirstLetterName,
                                "/apps/silversea/silversea-com/templates/page",
                                portFirstLetter,
                                false);
                    }

                    pageManager.create(portFirstLetterPage.getPath(),
                            JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class), city.getCityName()),
                            "/apps/silversea/silversea-com/templates/page",
                            city.getCityName(),
                            false);

                    i++;

                    if (i%100 == 0) {
                        session.save();
                    }
                }

                session.save();

            } catch (ApiException | WCMException | LoginException | RepositoryException e) {
                LOGGER.error("Exception importing cities", e);
            }
        }
    }

    @Override
    public void importCity(String cityId) {

    }
}
