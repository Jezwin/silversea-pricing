package com.silversea.aem.helper;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.ValueTypeBean;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Map;

public class CruiseCodeHelper {

    public static final String SELECTOR_CRUISE_CODE_PREFIX = "cruise_code_";

    public static String getCruiseCode(final Page currentPage, final SlingHttpServletRequest request, Map<String, ValueTypeBean> tokensAndStyle) {
        String cruiseCode = getCruiseCode(currentPage);

        if (cruiseCode != null) {
            return cruiseCode;
        }
        cruiseCode = getCruiseCode(request);
        if (cruiseCode != null) {
            return cruiseCode;
        }
        return getDefaultCruiseCode(tokensAndStyle);
    }

    private static String getDefaultCruiseCode(Map<String, ValueTypeBean> tokensAndStyle) {
        if(tokensAndStyle.containsKey("default_voyage_code")){
            String token = tokensAndStyle.get("default_voyage_code").getValue();
            if(token.equals(""))
                return null;
            return token;
        }
        return null;
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
