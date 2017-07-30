package com.silversea.aem.services;

import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

/**
 * Service allowing to get a tag identified by
 * it's country code ID in ISO_3166-1 format (2 characters)
 */
public interface GeolocationTagService {

    String PATH_TAGS_GEOLOCATION = "/etc/tags/geotagging";

    /**
     * TODO rename tag -> tagId
     *
     * Get a tag id from country ID
     * @param countryId
     * @return tag id
     */
    String getTagFromCountryId(final String countryId);

    /**
     * Get a tag id from ISO 3 country code
     * @param countryCodeIso3
     * @return tag id
     */
    String getTagIdFromCountryCodeIso3(final String countryCodeIso3);

    /**
     * Get a list of tag IDs from a list if county codes ISO3
     * @param countryCodesIso3
     * @return
     */
    List<String> getTagIdsFromCountryCodeIso3(final List<String> countryCodesIso3);

    /**
     * TODO rename tag -> tagId
     *
     * Get a tag id from the sling request
     * @param request
     * @return tag id
     */
    String getTagFromRequest(final SlingHttpServletRequest request);
}
