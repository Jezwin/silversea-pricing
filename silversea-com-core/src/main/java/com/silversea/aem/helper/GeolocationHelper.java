/**
 * 
 */
package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * @author mjedli
 *
 */
public class GeolocationHelper {

    public static final String SELECTOR_COUNTRY_PREFIX = "country_";

    public static final String SELECTOR_LANGUAGE_PREFIX = "language_";

    public static final String getCountryCode(final SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();

        for (String selector : selectors) {
            if (selector.startsWith(SELECTOR_COUNTRY_PREFIX)) {
                return selector.replace(SELECTOR_COUNTRY_PREFIX, "");
            }
        }

        return null;
    }













    public static String getCoutryCodeFromSelector(String[] selectors) {
        for (int i = 0; i < selectors.length; i++) {
            if (selectors[i].startsWith(SELECTOR_COUNTRY_PREFIX)) {
                return StringUtils.removeStart(selectors[i], SELECTOR_COUNTRY_PREFIX).toString().toLowerCase();
            }
        }
        return "";
    }

    public static String getLanguageCodeFromSelector(String[] selectors) {
        for (int i = 0; i < selectors.length; i++) {
            if (selectors[i].startsWith(SELECTOR_LANGUAGE_PREFIX)) {
                return StringUtils.removeStart(selectors[i], SELECTOR_LANGUAGE_PREFIX);
            }
        }
        return "";
    }
    
    public static String getLanguageSelector(String[] selectors) {
        for (int i = 0; i < selectors.length; i++) {
            if (selectors[i].startsWith(SELECTOR_LANGUAGE_PREFIX)) {
                return selectors[i];
            }
        }
        return "";
    }
    
    public static String getCountryCodeSelector(String[] selectors) {
        for (int i = 0; i < selectors.length; i++) {
            if (selectors[i].startsWith(SELECTOR_COUNTRY_PREFIX)) {
                return selectors[i];
            }
        }
        return "";
    }
}
