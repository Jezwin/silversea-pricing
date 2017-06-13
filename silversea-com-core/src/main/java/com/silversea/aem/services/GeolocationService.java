package com.silversea.aem.services;

import org.apache.sling.api.SlingHttpServletRequest;

import com.silversea.aem.components.beans.GeoLocation;

public interface GeolocationService {
    GeoLocation initGeolocation(SlingHttpServletRequest request);
}
