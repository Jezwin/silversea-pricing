package com.silversea.aem.featuretoggles;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.content.CrxContentLoader;

public class FeatureToggleHelper extends WCMUsePojo {

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