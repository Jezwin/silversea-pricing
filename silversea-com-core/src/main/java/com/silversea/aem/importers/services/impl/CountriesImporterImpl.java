package com.silversea.aem.importers.services.impl;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CountriesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.CountriesApi;
import io.swagger.client.model.Country;
import org.apache.cxf.common.util.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Service
public class CountriesImporterImpl implements CountriesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CountriesImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Override
    public ImportResult importData() throws IOException {
        LOGGER.debug("Starting countries import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final CountriesApi countriesApi = new CountriesApi(ImporterUtils.getApiClient(apiConfig));

            if (session == null || tagManager == null) {
                throw new ImporterException("Cannot initialize tagManager or session");
            }

            List<Country> countries = countriesApi.countriesGet(null, null);

            for (Country country : countries) {
                LOGGER.debug("Importing country: {}", country.getCountryName());

                try {
                    Tag market;
                    if (StringUtils.isEmpty(country.getMarket())) {
                        market = tagManager.createTag("geotagging:nomarket",
                                "No market", null, false);
                    } else {
                        market = tagManager.createTag("geotagging:" + country.getMarket().toLowerCase(),
                                country.getMarket().toUpperCase(), null, false);
                    }

                    Tag regionId;
                    if (StringUtils.isEmpty(market.getTagID())) {
                        regionId = tagManager.createTag(market.getTagID() + "/noregion",
                                "No region", null, false);
                    } else {
                        regionId = tagManager.createTag(market.getTagID() + "/"
                                        + String.valueOf(country.getRegionId()).toLowerCase(),
                                String.valueOf(country.getRegionId()), null, false);
                    }

                    Tag countryTag = tagManager.createTag(regionId.getTagID() + "/"
                            + country.getCountryIso2().toUpperCase(), country.getCountryName(), null, false);

                    final Node countryNode = countryTag.adaptTo(Node.class);

                    if (countryNode != null) {
                        countryNode.setProperty("id", country.getCountryId());
                        countryNode.setProperty("iso2", country.getCountryIso2());
                        countryNode.setProperty("iso3", country.getCountryIso3());
                        countryNode.setProperty("prefix", country.getCountryPrefix());
                        countryNode.setProperty("market", country.getMarket());
                        countryNode.setProperty("regionId", country.getRegionId());
                    } else {
                        throw new ImporterException("Cannot get country node");
                    }

                    successNumber++;
                } catch (InvalidTagFormatException | ImporterException e) {
                    LOGGER.error("Cannot create country {}", country.getCountryIso2(), e);

                    errorNumber++;
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read countries from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Cannot save modifications", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public void importCountry(String id) {

    }

    @Override
    public JSONObject getCountriesMapping() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        JSONObject jsonObject = new JSONObject();

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);

            Iterator<Resource> tags = resourceResolver.findResources("/jcr:root/etc/tags/geotagging//element(*,cq:Tag)", "xpath");

            while (tags.hasNext()) {
                Resource tagResource = tags.next();

                final ValueMap tagProperties = tagResource.adaptTo(ValueMap.class);

                if (tagProperties != null && tagProperties.containsKey("id")) {
                    try {
                        jsonObject.put(tagProperties.get("id", String.class),
                                tagResource.getPath());
                    } catch (JSONException e) {
                        LOGGER.error("Cannot add country {} with path {} to countries mapping",
                                tagProperties.get("id", String.class), tagResource.getPath(), e);
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

}
