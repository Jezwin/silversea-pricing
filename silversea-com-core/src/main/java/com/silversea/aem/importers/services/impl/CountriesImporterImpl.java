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

import io.swagger.client.ApiException;
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
        try {
            List<Country> listCountries = null;
            listCountries = countriesApi.countriesGet(null, null);
            for (Country country : listCountries) {
                importCountry(country);
            }
            getResourceResolver().close();
            LOGGER.debug("Data was imported completely ...");
        } catch (LoginException | ApiException e) {
            LOGGER.error("Import countries has some issues ()", e);
        }

    }

    private void importCountry(Country country) {
        Map<String, String> map = new HashMap<>();
        Iterator<Node> nodes = null;
        try {
            // Create the query builder to get node by country name - it means
            // iso2
            map.put(WcmConstants.SEARCH_KEY_PATH, GEOTAGGING_PATH);
            map.put(WcmConstants.SEARCH_KEY_TYPE, WcmConstants.DEFAULT_KEY_CQ_TAG);
            map.put(WcmConstants.SEARCH_NODE_NAME, country.getCountryIso2());
            Session session = getResourceResolver().adaptTo(Session.class);
            Query query = builder.createQuery(PredicateGroup.create(map), session);
            SearchResult searchResult = query.getResult();
            nodes = searchResult.getNodes();
            // Set result to current node
            while (nodes.hasNext()) {
                Node node = nodes.next();
                if (node.getDepth() == 6) {
                    updateNode(node, country);
                }
            }
            session.logout();
            session = null;
        } catch (LoginException | RepositoryException | IOException e) {
            LOGGER.error("Error on save node: ()", e);
        }
    }

    private void updateNode(Node node, Country country) throws IOException {
        try {
            Session session = getResourceResolver().adaptTo(Session.class);
            if (null != node) {
                node.setProperty("country_id", country.getCountryId());
                node.setProperty("country_url", country.getCountryUrl());
                node.setProperty("country_iso2", country.getCountryIso2());
                node.setProperty("country_iso3", country.getCountryIso3());
                node.setProperty("country_name", country.getCountryName());
                node.setProperty("country_prefix", country.getCountryPrefix());
                node.setProperty("market", country.getMarket());
                node.setProperty("region_id", country.getRegionId());
                session.save();
                LOGGER.debug("Country with iso 2 : " + country.getCountryIso2() + " added.");
            }
            session.logout();
            session = null;
        } catch (LoginException | RepositoryException e) {
            LOGGER.error("Silversea - Have some issues with session. Try to reconnect ...");
            importCountries();
        }
    }

    private ResourceResolver getResourceResolver() throws LoginException {
        return resourceResolverFactory.getAdministrativeResourceResolver(null);
    }

    @Override
    public void importCountry(String id) {

    }

}
