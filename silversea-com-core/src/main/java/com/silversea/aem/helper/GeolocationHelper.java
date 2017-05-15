/**
 * 
 */
package com.silversea.aem.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

/**
 * Helper class used to deal with geolocation, mainly via country selector
 */
public class GeolocationHelper {

    public static final String SELECTOR_COUNTRY_PREFIX = "country_";

    public static final String getCountryCode(final SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();

        for (String selector : selectors) {
            if (selector.startsWith(SELECTOR_COUNTRY_PREFIX)) {
                return selector.replace(SELECTOR_COUNTRY_PREFIX, "");
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    public static List<Tag> getGeoMarketCode(TagManager tagManager, List<String> geolocationTag) {
        String geoMarketCode = null;
        List<Tag> tags = new ArrayList<>();

//        for (String tag : geolocationTag) {
//            if(tagManager.resolve(tag.toLowerCase()) != null){
//                tags.add(tagManager.resolve(tag));
//            }
//        }
        for (int i = 0; i < geolocationTag.size(); i++) {
            if(tagManager.getResourceResolver().resolve(geolocationTag.get(i).toLowerCase()) != null){ //resolveByTitle(geolocationTag.get(i).toLowerCase()) != null){
                tags.add(tagManager.resolveByTitle(geolocationTag.get(i).toLowerCase()));
            }
        }
//        Tag tag = tagManager.resolve(geolocationTag);
//        if (tag != null) {
//            geoMarketCode = tag.getParent().getParent().getName();
//        }
        return tags;
    }
}
