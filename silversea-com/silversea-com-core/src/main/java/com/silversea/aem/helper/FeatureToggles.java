package com.silversea.aem.helper;

import com.silversea.aem.helper.content.ContentLoader;

import java.util.HashMap;

class FeatureToggles {
    static final String CRX_NODE_PATH = "/content/silversea-com/jcr:content/feature-toggles";

    private ContentLoader contentLoader;

    FeatureToggles(ContentLoader contentLoader) {
        this.contentLoader = contentLoader;
    }

    boolean IsEnabled(String key) throws Exception {
        HashMap<String, Object> node = contentLoader.get(CRX_NODE_PATH);

        if (node == null) return false;

        Object propertyValue = node.get(key);

        return propertyValue instanceof Boolean
                && (Boolean) propertyValue;
    }
}
