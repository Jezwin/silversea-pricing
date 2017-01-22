package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.PageManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private PageManager pageManager;

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

                for (City city : result) {
                    LOGGER.debug("City: {}", city.getCityName());

                    /*Page citiesRootPage = pageManager.getPage(ImportersConstants.CITIES_BASE_PATH);
                    pageManager.create(citiesRootPage.getPath(),
                            JcrUtil.createValidChildName(citiesRootPage.adaptTo(Node.class), city.getCityName()),
                            "",
                            city.getCityName(),
                            false);*/
                }
            } catch (ApiException e) {
                LOGGER.error("Exception importing cities", e);
            }/* catch (RepositoryException e) {
                e.printStackTrace();
            } catch (WCMException e) {
                e.printStackTrace();
            }*/
        }
    }

    @Override
    public void importCity(String cityId) {

    }
}
