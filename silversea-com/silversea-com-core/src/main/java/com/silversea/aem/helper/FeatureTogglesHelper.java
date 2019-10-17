package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.helper.content.CrxContentLoader;

public class FeatureTogglesHelper extends WCMUsePojo {

    private boolean isEnabled;

    @Override
    public void activate() throws Exception {
        String key = super.get("key", String.class);
        FeatureToggles featureToggles = new FeatureToggles(new CrxContentLoader(super.getResourceResolver()));

        this.isEnabled = featureToggles.isEnabled(key);
    }

    public Boolean getIsEnabled() {
        return this.isEnabled;
    }
}