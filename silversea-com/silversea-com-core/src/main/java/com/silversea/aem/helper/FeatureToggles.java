package com.silversea.aem.helper;

import com.silversea.aem.helper.crx.ContentLoader;

public class FeatureToggles {
    public static final String CRX_NODE_PATH = "/content/silversea-com/jcr:content/featureToggles";

    private ContentLoader contentLoader;

    public FeatureToggles(ContentLoader contentLoader) {

        this.contentLoader = contentLoader;
    }

    public boolean IsEnabled(String key) {
        //contentLoader.get(CRX_NODE_PATH);

        return false;
    }
}
