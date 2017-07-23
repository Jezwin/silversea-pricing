/**
 *
 */
package com.silversea.aem.helper;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class used to deal with geolocation, mainly via country selector
 */
public class GeolocationHelper {

    private static final String SELECTOR_COUNTRY_PREFIX = "country_";

    /**
     * @param request the current http request
     * @return the country code passed as selector in the request path
     */
    public static String getCountryCode(final SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();

        for (String selector : selectors) {
            if (selector.startsWith(SELECTOR_COUNTRY_PREFIX)) {
                return selector.replace(SELECTOR_COUNTRY_PREFIX, "");
            }
        }

        return null;
    }

    @Deprecated
    public static List<Tag> getGeomarketCode(TagManager tagManager, List<String> geolocationTags) {
        List<Tag> tags = new ArrayList<>();

        for (String geolocationTag : geolocationTags) {
            if (tagManager.resolve(WcmConstants.GEOLOCATION_TAGS_PREFIX + geolocationTag.toLowerCase()) != null) {
                tags.add(tagManager.resolve(WcmConstants.GEOLOCATION_TAGS_PREFIX + geolocationTag.toLowerCase()));
            }
        }
        return tags;
    }

    // TODO clean this
    // TODO move to com.silversea.aem.services.GeolocationTagService
    public static String getGeoMarket(TagManager tagManager, String geolocationTag) {
        Iterator<Tag> tags = tagManager.resolve(WcmConstants.GEOLOCATION_TAGS_PREFIX).listChildren();

        while (tags.hasNext()) {
            Tag tag = tags.next();

            Iterator<Tag> tagsChild = tag.listChildren();
            while (tagsChild.hasNext()) {
                Tag tag2 = tagsChild.next();

                if (tagManager.resolve(tag2.getPath() + "/" + geolocationTag) != null) {
                    return tagManager.resolve(tag2.getPath() + "/" + geolocationTag)
                            .getParent().getParent().getName();
                }
            }
        }

        return null;
    }
}
