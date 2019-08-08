package com.silversea.aem.components.displayProperties;

import com.adobe.cq.sightly.WCMUsePojo;
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
        String target = getProperties().get("target", defaultTarget);
        String fallback = getProperties().get("fallback", defaultFallback);

        if (target != null) {
            displayProperty = getPageProperties().get(target, String.class);
        }

        if (displayProperty == null && fallback != null) {
            displayProperty = getPageProperties().get(fallback, String.class);
        }
    }

    public String getDisplayProperty() {
        return displayProperty;
    }
}