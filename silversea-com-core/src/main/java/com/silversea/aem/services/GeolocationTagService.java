package com.silversea.aem.services;

import com.silversea.aem.models.GeolocationTagModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Map;

/**
 * Service allowing to get a tag identified by it's country code ID in ISO_3166-1 format (2 characters)
 */
public interface GeolocationTagService {

    /**
     * @return the mapping between country code (ISO2) and tag id
     */
    Map<String, String> getTagIdsMapping();

    /**
     * Get a tag id from country ID (country code ISO2)
     *
     * @param countryId
     * @return tag id
     */
    String getTagIdFromCountryId(final String countryId);

    /**
     * Get a tag id from ISO 3 country code
     *
     * @param countryCodeIso3
     * @return tag id
     */
    String getTagIdFromCountryCodeIso3(final String countryCodeIso3);

    /**
     * Get a list of tag IDs from a list if country codes ISO3
     *
     * @param countryCodesIso3
     * @return
     */
    List<String> getTagIdsFromCountryCodeIso3(final List<String> countryCodesIso3);

    /**
     * Get a tag id from the sling request
     *
     * @param request
     * @return tag id
     */
    String getTagIdFromRequest(final SlingHttpServletRequest request);

    /**
     * Get geolocation tag from request
     *
     * @param request the request
     * @return geolocation tag
     */
    GeolocationTagModel getGeolocationTagModelFromRequest(final SlingHttpServletRequest request);

    /**
     * TODO use resource resolver instead of request
     * <p>
     * Get geolocation tag from country code
     *
     * @param countryCode the country code
     * @return geolocation tag
     */
    GeolocationTagModel getGeolocationTagModelFromCountryCode(final ResourceResolver resourceResolver, final String countryCode);
}
