/**
 * 
 */
package com.silversea.aem.tools;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mjedli
 *
 */
public class GeolocationHelper {

    private static final String GEOLOCATION_COUNTRY_PREFIX = "country_";

    public static String getCoutryCodeFromSelector(String selector)
    {
        if (selector.startsWith(GEOLOCATION_COUNTRY_PREFIX)) {
            return StringUtils.removeStart(selector, GEOLOCATION_COUNTRY_PREFIX);
        }
        return selector;
    }
    
}
