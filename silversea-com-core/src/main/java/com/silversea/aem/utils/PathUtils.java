package com.silversea.aem.utils;

import java.util.Locale;

import org.apache.sling.api.resource.Resource;

import com.adobe.granite.confmgr.Conf;

/**
 * TODO NPE
 */
public class PathUtils {

    /**
     * Constructor.
     */
    private PathUtils() {

    }

    /**
     * Return local path to request quote page path from /conf
     */
    public static String getRequestQuotePagePath(Resource resource, Locale locale) {
        String pagePath;
        // Get width from configuration
        Resource confRes = resource.adaptTo(Conf.class).getItemResource("/requestquotepage/page");

        if (confRes != null) {
            pagePath = "/content/silversea-com/" + locale.getLanguage() + confRes.getValueMap().get("reference", String.class);
            if (resource.getResourceResolver().getResource(pagePath) != null) {
                return pagePath;
            }
        }

        return null;
    }
}