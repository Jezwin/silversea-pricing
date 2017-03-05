package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.ValueMap;

/**
 * Created by aurelienolivier on 05/03/2017.
 */
public class DisplayPropertiesUse extends WCMUsePojo {

    private String displayProperty = null;

    @Override
    public void activate() throws Exception {
        ValueMap pageProperties = getCurrentPage().getProperties();

        String target = getProperties().get("target", String.class);
        String fallback = getProperties().get("fallback", String.class);

        if (target != null) {
            displayProperty = pageProperties.get(target, String.class);
        }

        if (displayProperty == null && fallback != null) {
            displayProperty = pageProperties.get(fallback, String.class);
        }
    }

    public String getDisplayProperty() {
        return displayProperty;
    }
}
