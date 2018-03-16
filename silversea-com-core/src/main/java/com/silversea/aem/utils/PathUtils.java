package com.silversea.aem.utils;

import com.adobe.granite.confmgr.Conf;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.helper.LanguageHelper;
import org.apache.sling.api.resource.Resource;

public class PathUtils {

    /**
     * Constructor.
     */
    private PathUtils() {

    }

    /**
     * @param resource
     * @param page
     * @return local path to request quote page path from /conf
     */
    public static String getRequestQuotePagePath(final Resource resource, final Page page) {
        final Conf confRes = resource.adaptTo(Conf.class);
        if (confRes != null && page != null) {
            final Resource requestQuotePageConf = confRes.getItemResource("/requestquotepage/page");

            if (requestQuotePageConf != null) {
                final String pagePath = "/content/silversea-com/" + LanguageHelper.getLanguage(page)
                        + requestQuotePageConf.getValueMap().get("reference", String.class);
                if (resource.getResourceResolver().getResource(pagePath) != null) {
                    return pagePath;
                }
            }
        }

        return null;
    }
    
    /**
     * @param resource
     * @param page
     * @return local path to request brochures page path from /conf
     */
    public static String getBrochuresPagePath(final Resource resource, final Page page) {
        final Conf confRes = resource.adaptTo(Conf.class);
        if (confRes != null && page != null) {
            final Resource brochuresPageConf = confRes.getItemResource("/brochurespage/page");

            if (brochuresPageConf != null) {
                final String pagePath = "/content/silversea-com/" + LanguageHelper.getLanguage(page)
                        + brochuresPageConf.getValueMap().get("reference", String.class);
                if (resource.getResourceResolver().getResource(pagePath) != null) {
                    return pagePath;
                }
            }
        }

        return null;
    }
}