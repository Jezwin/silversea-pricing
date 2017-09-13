package com.silversea.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagConstants;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.sling.api.resource.Resource;

/**
 * Created by asiba on 14/03/2017.
 */
public class CookieDisclaimerUse extends WCMUsePojo {

    private Boolean showCookieMsg = false;

    private String description;

    @Override
    public void activate() throws Exception {
        // Getting context
        GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);

        if (geolocationTagService != null) {
            final String geolocationTagId = geolocationTagService.getTagIdFromRequest(getRequest());

            final Resource cookieDisclaimerResource = !getResource().getName().equals("cookie-disclaimer")
                    ? getResource().getChild("cookie-disclaimer") : getResource();

            // Getting inherited properties
            if (cookieDisclaimerResource != null) {
                final InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(cookieDisclaimerResource);
                description = properties.getInherited(JcrConstants.JCR_DESCRIPTION, String.class);

                final String[] tags = properties.getInherited(TagConstants.PN_TAGS, String[].class);
                if (tags != null) {
                    for (String tag : tags) {
                        if (tag.equals(geolocationTagId)) {
                            showCookieMsg = true;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return the showCookieMsg
     */
    public Boolean showCookieMsg() {
        return showCookieMsg;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}