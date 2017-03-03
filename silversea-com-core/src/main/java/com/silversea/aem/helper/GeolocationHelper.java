/**
 * 
 */
package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;

/**
 * @author mjedli
 *
 */
public class GeolocationHelper {

    public static final String LANGUAGE_PREFIX = "language_";
    public static final String GEOLOCATION_COUNTRY_PREFIX = "country_";

    public static String getCoutryCodeFromSelector(String[] selectors) {
        for (int i = 0; i < selectors.length; i++) {
            if (selectors[i].startsWith(GEOLOCATION_COUNTRY_PREFIX)) {
                return StringUtils.removeStart(selectors[i], GEOLOCATION_COUNTRY_PREFIX).toString().toLowerCase();
            }
        }
        return "";
    }

    public static String getLanguageCodeFromSelector(String[] selectors) {
        for (int i = 0; i < selectors.length; i++) {
            if (selectors[i].startsWith(LANGUAGE_PREFIX)) {
                return StringUtils.removeStart(selectors[i], LANGUAGE_PREFIX);
            }
        }
        return "";
    }
    
    public static String getLanguageSelector(String[] selectors) {
        for (int i = 0; i < selectors.length; i++) {
            if (selectors[i].startsWith(LANGUAGE_PREFIX)) {
                return selectors[i];
            }
        }
        return "";
    }
    
    public static String getCountryCodeSelector(String[] selectors) {
        for (int i = 0; i < selectors.length; i++) {
            if (selectors[i].startsWith(GEOLOCATION_COUNTRY_PREFIX)) {
                return selectors[i];
            }
        }
        return "";
    }
}
