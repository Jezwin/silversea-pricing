package com.silversea.aem.components.editorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

public class HeroBannerUse extends WCMUsePojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeroBannerUse.class);

    Boolean hasButton1;
    Boolean hasButton2;

    @Override
    public void activate() throws Exception {
        LOGGER.error("======> {}", getCurrentPage().getPath());
    }

    /**
     * @return the hasButton1
     */
    public Boolean hasButton1() {
        return hasButton1;
    }

    /**
     * @return the hasButton2
     */
    public Boolean hasButton2() {
        return hasButton2;
    }
}