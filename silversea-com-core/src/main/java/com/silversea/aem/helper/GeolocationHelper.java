/**
 * 
 */
package com.silversea.aem.helper;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Helper class used to deal with geolocation, mainly via country selector
 */
public class GeolocationHelper {

    public static final String SELECTOR_COUNTRY_PREFIX = "country_";

    public static final String getCountryCode(final SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();

        for (String selector : selectors) {
            if (selector.startsWith(SELECTOR_COUNTRY_PREFIX)) {
                return selector.replace(SELECTOR_COUNTRY_PREFIX, "");
            }
        }

        return null;
    }
}
