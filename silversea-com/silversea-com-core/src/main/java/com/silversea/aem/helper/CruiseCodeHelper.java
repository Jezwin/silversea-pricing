package com.silversea.aem.helper;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;

public class CruiseCodeHelper {

    public static final String SELECTOR_CRUISE_CODE_PREFIX = "cruise_code_";

    public static String getCruiseCode(final Page currentPage, final SlingHttpServletRequest request) {
        String cruiseCode = getCruiseCode(currentPage);

        if (cruiseCode != null) {
            return cruiseCode;
        }
        return getCruiseCode(request);
    }

    public static String getCruiseCode(final Page currentPage) {
        return (String)currentPage.getProperties().get("cruiseCode");
    }

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
