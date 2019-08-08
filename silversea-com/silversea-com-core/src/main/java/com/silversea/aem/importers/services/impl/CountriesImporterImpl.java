package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagException;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CountriesImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.GeolocationTagModel;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.services.GeolocationTagService;
import io.swagger.client.ApiException;
import io.swagger.client.api.CountriesApi;
import io.swagger.client.model.Country;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Component
@Service
public class CountriesImporterImpl implements CountriesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CountriesImporterImpl.class);

    private boolean importRunning;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private GeolocationTagService geolocationTagService;

    @Override
    public ImportResult importData(final boolean update) throws ImporterException {
        if (importRunning) {
            throw new ImporterException("Import is already running");
        }

        LOGGER.debug("Starting countries import");
        importRunning = true;

        final ImportResult importResult = new ImportResult();

        final Map<String, String> tagIdsMapping = geolocationTagService.getTagIdsMapping();

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final CountriesApi countriesApi = new CountriesApi(ImportersUtils.getApiClient(apiConfig));

            if (session == null || tagManager == null) {
                throw new ImporterException("Cannot initialize tagManager or session");
            }

            List<Country> countries = countriesApi.countriesGet(null, null);

            Set<String> existingCountries = new HashSet<>();

            for (Country country : countries) {
                LOGGER.debug("Importing country: {}", country.getCountryName());

                existingCountries.add(country.getCountryIso2());

                try {
                    // tag exists
                    if (tagIdsMapping.containsKey(country.getCountryIso2())) {
                        final String countryTagId = tagIdsMapping.get(country.getCountryIso2());
                        final GeolocationTagModel geolocationTagModel = geolocationTagService.getGeolocationTagModelFromCountryCode(resourceResolver, country.getCountryIso2());


                        final String countryPath = "/etc/tags/geotaggging/"
                                + StringUtils.defaultIfEmpty(country.getMarket(), "nomarket").toLowerCase()
                                + "/" + JcrUtil.createValidName(StringUtils.defaultIfEmpty(country.getRegion(), "noregion"), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING)
                                + "/" + country.getCountryIso2().toUpperCase();


                        // get or move the tag if modified
                        Tag countryTag;
                        if (geolocationTagModel != null
                                && (!geolocationTagModel.getMarket().equals(country.getMarket())
                                    || !geolocationTagModel.getRegion().equals(country.getMarket())
                                    || !geolocationTagModel.getPath().equals(countryPath))) {

                            Tag market;
                            if (StringUtils.isEmpty(country.getMarket())) {
                                market = tagManager.createTag("geotagging:nomarket", "No market", null, false);
                            } else {
                                market = tagManager.createTag("geotagging:" + country.getMarket().toLowerCase(),
                                        country.getMarket().toUpperCase(), null, false);
                            }

                            Tag region;
                            if (StringUtils.isEmpty(country.getRegion())) {
                                region = tagManager.createTag(market.getTagID() + "/noregion", "No region", null, false);
                            } else {
                                region = tagManager.createTag(market.getTagID() + "/" + JcrUtil.createValidName(country.getRegion(), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING),
                                        String.valueOf(country.getRegion()), null, false);
                            }

                            final Tag tag = tagManager.resolve(countryTagId);

                            countryTag = tagManager.moveTag(tag, region.getTagID() + "/" + country.getCountryIso2().toUpperCase());
                        } else {
                            countryTag = tagManager.resolve(countryTagId);
                        }

                        if (countryTag == null) {
                            throw new ImporterException("Cannot create or get tag for country ID " + countryTagId);
                        }

                        // update tags infos
                        final Node countryNode = countryTag.adaptTo(Node.class);
                        if (countryNode != null) {
                            countryNode.setProperty("id", country.getCountryId());
                            countryNode.setProperty("iso2", country.getCountryIso2());
                            countryNode.setProperty("iso3", country.getCountryIso3());
                            countryNode.setProperty("prefix", country.getCountryPrefix());
                            countryNode.setProperty("market", country.getMarket());
                            countryNode.setProperty("regionId", country.getRegionId());
                            countryNode.setProperty("region", country.getRegion());
                            countryNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
                        } else {
                            throw new ImporterException("Cannot get country node");
                        }

                        importResult.incrementSuccessNumber();
                    // tag not exists, creating it
                    } else {
                        Tag market;
                        if (StringUtils.isEmpty(country.getMarket())) {
                            market = tagManager.createTag("geotagging:nomarket", "No market", null, false);
                        } else {
                            market = tagManager.createTag("geotagging:" + country.getMarket().toLowerCase(),
                                    country.getMarket().toUpperCase(), null, false);
                        }

                        Tag region;
                        if (StringUtils.isEmpty(country.getRegion())) {
                            region = tagManager.createTag(market.getTagID() + "/noregion", "No region", null, false);
                        } else {
                            region = tagManager.createTag(market.getTagID() + "/" + JcrUtil.createValidName(country.getRegion(), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING),
                                    String.valueOf(country.getRegion()), null, false);
                        }

                        final Tag countryTag = tagManager.createTag(region.getTagID() + "/"
                                + country.getCountryIso2().toUpperCase(), country.getCountryName(), null, false);

                        final Node countryNode = countryTag.adaptTo(Node.class);
                        if (countryNode != null) {
                            countryNode.setProperty("id", country.getCountryId());
                            countryNode.setProperty("iso2", country.getCountryIso2());
                            countryNode.setProperty("iso3", country.getCountryIso3());
                            countryNode.setProperty("prefix", country.getCountryPrefix());
                            countryNode.setProperty("market", country.getMarket());
                            countryNode.setProperty("regionId", country.getRegionId());
                            countryNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
                        } else {
                            throw new ImporterException("Cannot get country node");
                        }

                        importResult.incrementSuccessNumber();
                    }
                } catch (TagException | InvalidTagFormatException | ImporterException e) {
                    LOGGER.error("Cannot create country {}", country.getCountryIso2(), e);

                    importResult.incrementErrorNumber();
                }
            }

            for (Map.Entry<String, String> tagIdMapping : tagIdsMapping.entrySet()) {
                if (!existingCountries.contains(tagIdMapping.getKey())) {
                    final Tag tag = tagManager.resolve(tagIdMapping.getValue());

                    if (tag != null) {
                        tagManager.deleteTag(tag, false);
                    }
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
            importRunning = false;
        }

        LOGGER.debug("Ending countries import, success: {}, error: {}", +importResult.getSuccessNumber(), +importResult.getErrorNumber());

        return importResult;
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
