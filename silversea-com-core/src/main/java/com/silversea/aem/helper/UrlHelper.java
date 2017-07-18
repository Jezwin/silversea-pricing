package com.silversea.aem.helper;

import com.silversea.aem.constants.WcmConstants;
import org.apache.commons.lang3.StringUtils;

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

}
