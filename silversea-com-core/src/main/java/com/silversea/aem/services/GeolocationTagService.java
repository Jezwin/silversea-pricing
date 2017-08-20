package com.silversea.aem.services;

import com.silversea.aem.models.GeolocationTagModel;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

/**
 * Service allowing to get a tag identified by it's country code ID in ISO_3166-1 format (2 characters)
 */
public interface GeolocationTagService {

    /**
     * TODO rename tag -> tagId
     * <p>
     * Get a tag id from country ID
     *
     * @param countryId
     *
     * @return tag id
     */
    String getTagFromCountryId(final String countryId);

    /**
     * Get a tag id from ISO 3 country code
     *
     * @param countryCodeIso3
     *
     * @return tag id
     */
    String getTagIdFromCountryCodeIso3(final String countryCodeIso3);

    /**
     * Get a list of tag IDs from a list if county codes ISO3
     *
     * @param countryCodesIso3
     *
     * @return
     */
    List<String> getTagIdsFromCountryCodeIso3(final List<String> countryCodesIso3);

    /**
     * TODO rename tag -> tagId
     * <p>
     * Get a tag id from the sling request
     *
     * @param request
     *
     * @return tag id
     */
    String getTagIdFromRequest(final SlingHttpServletRequest request);

    /**
     * Get geolocation tag from request
     * @param request the request
     * @return geolocation tag
     */
    GeolocationTagModel getGeolocationTagModelFromRequest(final SlingHttpServletRequest request);
}
