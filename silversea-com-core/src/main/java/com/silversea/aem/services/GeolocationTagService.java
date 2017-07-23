package com.silversea.aem.services;

import org.apache.sling.api.SlingHttpServletRequest;

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
     * @return tag
     */
    String getTagFromCountryId(final String countryId);

    /**
     * TODO rename tag -> tagId
     *
     * Get a tag id from the sling request
     * @param request
     * @return tag
     */
    String getTagFromRequest(final SlingHttpServletRequest request);
}
