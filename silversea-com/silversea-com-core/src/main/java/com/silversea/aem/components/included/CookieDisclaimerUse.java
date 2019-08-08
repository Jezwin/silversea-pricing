package com.silversea.aem.components.included;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagConstants;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.resource.Resource;

/**
 * Created by asiba on 14/03/2017.
 */
public class CookieDisclaimerUse extends AbstractGeolocationAwareUse {

    private Boolean showCookieMsg = false;

    private String description;

    @Override
    public void activate() throws Exception {
        // Getting context
    		super.activate();
        	Boolean takeChild = false;

            Resource cookieDisclaimerResource = !getResource().getName().equals("cookie-disclaimer")
                    ? getResource().getChild("cookie-disclaimer") : getResource();
                    
            if(cookieDisclaimerResource == null) {
            	cookieDisclaimerResource = getResource();
            	takeChild = true;
            }

            // Getting inherited properties
            if (cookieDisclaimerResource != null) {
                final InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(cookieDisclaimerResource);
                if(takeChild) {
                	description = properties.getInherited("cookie-disclaimer/"+JcrConstants.JCR_DESCRIPTION, String.class);
                }else {
                	description = properties.getInherited(JcrConstants.JCR_DESCRIPTION, String.class);
                }
                
                String[] tags = properties.getInherited("cookie-disclaimer/"+TagConstants.PN_TAGS, String[].class);
                if(!takeChild) {
                	tags = properties.getInherited(TagConstants.PN_TAGS, String[].class);
                }
                if (tags != null) {
                    for (String tag : tags) {
                    	tag = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
                        if (super.isBestMatch(tag)) {
                            showCookieMsg = true;
                            break;
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