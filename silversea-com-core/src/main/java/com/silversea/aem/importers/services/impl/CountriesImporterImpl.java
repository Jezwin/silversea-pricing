package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.List;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CountriesImporter;

import io.swagger.client.api.CountriesApi;
import io.swagger.client.model.Country;

@Service
@Component(label = "Silversea.com - Cities importer")
public class CountriesImporterImpl extends BaseImporter implements CountriesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CountriesImporterImpl.class);
    private static final String COUNTRY_PATH = "/api/v1/countries";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importCities() throws IOException {
        final String authorizationHeader = getAuthorizationHeader(COUNTRY_PATH);
        CountriesApi countriesApi = new CountriesApi();
        countriesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            // Get root page of city
            //Page countryRootPage = tagManager.getTags(arg0)

            List<Country> listCountries;

            do {
                listCountries = countriesApi.countriesGet(null, null);
                for (Country country : listCountries) {
                    
                }

            } while (listCountries.size() > 0);

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public void importCity(String cityId) {
        // TODO Auto-generated method stub

    }

}
