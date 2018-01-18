package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;

public class UrlHelper {

    public static String getProperUrl(String url) {
        String properUrl = url;

        if (StringUtils.isNotBlank(url)) {
            if (url.startsWith("/content") && !url.startsWith("/content/dam")) {
                // add the code to split the URL so that .html can be added
                // at proper place and then query string will be appended
                String newUrl;

                if (url.contains("?")) {
                    String[] urlSplit = url.split("\\?");
                    newUrl = urlSplit[0] + ".html?" + urlSplit[1];
                } else if (url.contains("#")) {
                    String[] urlSplit = url.split("#");
                    newUrl = urlSplit[0] + ".html#" + urlSplit[1];
                } else {
                    newUrl = url + WcmConstants.HTML_SUFFIX;
                }

                if (StringUtils.isNotBlank(newUrl)) {
                    properUrl = newUrl;
                }
            }
        }

        return properUrl;
    }
    
    public static String[] createSuffixAndSelectorUrl(Page currentPage) {
    	String selectorUrl = null, suffixUrl = null;
    	String cqTemplate = currentPage.getProperties().get(NameConstants.NT_TEMPLATE.toLowerCase(), String.class);
		switch (cqTemplate) {
		case WcmConstants.PAGE_TEMPLATE_DESTINATION:
			suffixUrl = WcmConstants.SELECTOR_SINGLE_DESTINATION;
			selectorUrl = currentPage.getProperties().get(WcmConstants.PN_DESTINATION_ID, String.class)
					+ WcmConstants.HTML_SUFFIX;
			break;
		case WcmConstants.PAGE_TEMPLATE_SHIP:
			suffixUrl = WcmConstants.SELECTOR_SINGLE_SHIP;
			selectorUrl = currentPage.getProperties().get(WcmConstants.PN_SHIP_ID, String.class)
					+ WcmConstants.HTML_SUFFIX;
			break;
		case WcmConstants.PAGE_TEMPLATE_EXCLUSIVE_OFFER:
			suffixUrl = WcmConstants.SELECTOR_EXCLUSIVE_OFFER;
			selectorUrl = currentPage.getProperties().get(WcmConstants.PN_EXCLUSIVE_OFFER_ID, String.class)
					+ WcmConstants.HTML_SUFFIX;
			break;
		}
		return new String[]{suffixUrl, selectorUrl};
    }

}
