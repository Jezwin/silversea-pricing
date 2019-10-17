package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.helper.content.CrxContentLoader;

public class FeatureTogglesHelper extends WCMUsePojo {

    private FeatureToggles featureToggles;

    @Override
    public void activate() throws Exception {
        this.featureToggles = new FeatureToggles(new CrxContentLoader(super.getResourceResolver()));
    }

    public Boolean isEnabled(String key) throws Exception {
        return this.featureToggles.isEnabled(key);
    }
}