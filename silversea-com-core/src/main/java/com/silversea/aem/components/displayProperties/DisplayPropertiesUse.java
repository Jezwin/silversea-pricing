package com.silversea.aem.components.displayProperties;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by aurelienolivier on 05/03/2017.
 */
public class DisplayPropertiesUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisplayPropertiesUse.class);

    private String displayProperty = null;

    protected String defaultTarget = null;

    protected String defaultFallback = null;

    @Override
    public void activate() throws Exception {
        ValueMap pageProperties = getCurrentPage().getProperties();

        LOGGER.debug("defaultTarget: {}", defaultTarget);
        LOGGER.debug("defaultFallback: {}", defaultFallback);

        String target = getProperties().get("target", defaultTarget);
        String fallback = getProperties().get("fallback", defaultFallback);

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
