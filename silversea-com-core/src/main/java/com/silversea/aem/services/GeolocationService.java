package com.silversea.aem.services;

import com.silversea.aem.components.beans.GeoLocation;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * TODO javadoc
 */
@Deprecated
public interface GeolocationService {

    GeoLocation initGeolocation(SlingHttpServletRequest request);

    String getLocalizedPhone(SlingHttpServletRequest request);

    String getLocalizedCurrency(SlingHttpServletRequest request);
}
