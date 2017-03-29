package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.services.CountriesImporter;

import io.swagger.client.api.CountriesApi;
import io.swagger.client.model.Country;

@Component(immediate = true, label = "Silversea.com - Cities importer")
@Service(value = CountriesImporter.class)
public class CountriesImporterImpl extends BaseImporter implements CountriesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CountriesImporterImpl.class);
    private static final String COUNTRY_PATH = "/api/v1/countries";
    private static final String GEOTAGGING_PATH = "/etc/tags/geotagging";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private QueryBuilder builder;

    @Override
    public void importCountries() throws IOException {
        final String authorizationHeader = getAuthorizationHeader(COUNTRY_PATH);
        CountriesApi countriesApi = new CountriesApi();
        countriesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        Session session = getResourceResolver().adaptTo(Session.class);
        try {
            List<Country> listCountries = null;
            listCountries = countriesApi.countriesGet(null, null);
            for (Country country : listCountries) {
                importCountry(country.getCountryIso2(), country,session);
            }
            session.save();
        } catch (Exception e) {
            String errorMessage = "Some issues are happened for import builder ()";
            LOGGER.error(errorMessage, e);
        } finally {
            getResourceResolver().close();
        }

    }

    private void importCountry(String iso2, Country country, Session session) {
        Map<String, String> map = new HashMap<>();
        Node currentNode = null;
        try {
            // Create the query builder to get node by country name - it means
            // iso2
            map.put(WcmConstants.SEARCH_KEY_PATH, GEOTAGGING_PATH);
            map.put(WcmConstants.SEARCH_KEY_TYPE, WcmConstants.DEFAULT_KEY_CQ_TAG);
            map.put(WcmConstants.SEARCH_NODE_NAME, iso2);
            Query query = builder.createQuery(PredicateGroup.create(map), session);
            SearchResult searchResult = query.getResult();
            Iterator<Node> nodes = searchResult.getNodes();
            // Set result to current node
            if (nodes.hasNext()) {
                while (nodes.hasNext()) {
                    Node node = nodes.next();
                    if (node.getDepth() == 6) {
                        currentNode = node;
                    }
                }
            }
            // Add some properties for current node
            if (null != currentNode) {
                currentNode.setProperty("country_id", country.getCountryId());
                currentNode.setProperty("country_url", country.getCountryUrl());
                currentNode.setProperty("country_iso2", country.getCountryIso2());
                currentNode.setProperty("country_iso3", country.getCountryIso3());
                currentNode.setProperty("country_name", country.getCountryName());
                currentNode.setProperty("country_prefix", country.getCountryPrefix());
                currentNode.setProperty("market", country.getMarket());
                currentNode.setProperty("region_id", country.getRegionId());
            }
        } catch (Exception e) {
            LOGGER.error("Bugs: ()", e);
        }
    }

    private ResourceResolver getResourceResolver() {
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
        } catch (LoginException e) {
            String errorMessage = "Some issues are happened ()";
            LOGGER.error(errorMessage, e);
        }
        return resourceResolver;
    }

    @Override
    public void importCountry(String id) {

    }

}
