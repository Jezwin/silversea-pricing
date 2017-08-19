package com.silversea.aem.utils;

import com.adobe.granite.confmgr.Conf;
import org.apache.sling.api.resource.Resource;

import java.util.Locale;

public class PathUtils {

    /**
     * Constructor.
     */
    private PathUtils() {

    }

    /**
     * @param resource
     * @param locale
     * @return local path to request quote page path from /conf
     */
    public static String getRequestQuotePagePath(final Resource resource, final Locale locale) {
        final Conf confRes = resource.adaptTo(Conf.class);
        if (confRes != null) {
            final Resource requestQuotePageConf = confRes.getItemResource("/requestquotepage/page");

            if (requestQuotePageConf != null) {
                final String pagePath = "/content/silversea-com/" + locale.getLanguage()
                        + requestQuotePageConf.getValueMap().get("reference", String.class);
                if (resource.getResourceResolver().getResource(pagePath) != null) {
                    return pagePath;
                }
            }
        }

        return null;
    }
}