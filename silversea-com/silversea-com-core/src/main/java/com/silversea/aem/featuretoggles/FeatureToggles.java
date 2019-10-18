package com.silversea.aem.featuretoggles;

import com.silversea.aem.content.ContentLoader;

import java.util.HashMap;

public class FeatureToggles {
    static final String CRX_NODE_PATH = "/content/silversea-com/feature-toggles";

    private ContentLoader contentLoader;

    public FeatureToggles(ContentLoader contentLoader) {
        this.contentLoader = contentLoader;
    }

    public boolean isEnabled(String key) {
        HashMap<String, Object> node;

        try {
            node = contentLoader.get(CRX_NODE_PATH);
        } catch (Exception e) {
            //todo log error
            return false;
        }

        if (node == null) return false;

        Object propertyValue = node.get(key);

        return propertyValue instanceof Boolean
                && (Boolean) propertyValue;
    }
}
