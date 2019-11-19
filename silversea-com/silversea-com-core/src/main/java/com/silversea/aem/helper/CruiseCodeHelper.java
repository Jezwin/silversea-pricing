package com.silversea.aem.helper;

import org.apache.sling.api.SlingHttpServletRequest;

public class CruiseCodeHelper {

    public static final String SELECTOR_CRUISE_CODE_PREFIX = "cruise_code_";

    public static String getCruiseCode(final SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();

        for (String selector : selectors) {
            if (selector.startsWith(SELECTOR_CRUISE_CODE_PREFIX)) {
                return selector.replace(SELECTOR_CRUISE_CODE_PREFIX, "");
            }
        }
        return null;
    }

    public static String urlIdentifierFor(String cruiseCode) {
        return SELECTOR_CRUISE_CODE_PREFIX + cruiseCode;
    }
}
