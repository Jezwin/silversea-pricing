package com.silversea.aem.components.included;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;

public class VariationAssetDisplay extends WCMUsePojo {
    private Resource resourceImage;

    @Override
    public void activate() throws Exception {
        Resource resourceImage = getResourceResolver().getResource(getProperties().get("plan", String.class));
    }

    public Resource getResourceImage() {
        return resourceImage;
    }

}