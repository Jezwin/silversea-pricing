package com.silversea.aem.components.displayProperties;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

/**
 * Created by ilazaar.
 */
public class DiningVariationDisplayPropertiesUse extends WCMUsePojo {

    Resource dining;

    static final String DINING_REFERENCE = "diningReference";
    
    public void activate() throws Exception {
    	
        String pathDining =  getCurrentPage().getProperties().get(DINING_REFERENCE, String.class);
        if (StringUtils.isNotEmpty(pathDining)) {
        	dining = getResourceResolver().getResource(pathDining);
        }
    }

    public Page getDiningPage() {
        if (dining != null) {
            return dining.adaptTo(Page.class);
        }
        return null;
    }
}
