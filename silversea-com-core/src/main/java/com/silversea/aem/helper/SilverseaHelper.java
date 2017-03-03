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
public class SilverseaHelper {

    public static final String CONTENT = "/content/";

    public static String getSiteNameFronRequest(SlingHttpServletRequest request) {
        return getSiteNameFromPathInfo(request.getPathInfo());
    }

    public static String getSiteNameFromPathInfo(String pathInfo) {
        String result = StringUtils.removeStart(pathInfo, CONTENT);
        return result.split("/")[0];
    }

}
