package com.silversea.aem.components.displayProperties;

/**
 * Created by aurelienolivier on 05/03/2017.
 */
public class PortDisplayPropertiesUse extends DisplayPropertiesUse {

    @Override
    public void activate() throws Exception {
        defaultFallback = "apiLongDescription";
        defaultTarget = "longDescription";

        super.activate();
    }
}
