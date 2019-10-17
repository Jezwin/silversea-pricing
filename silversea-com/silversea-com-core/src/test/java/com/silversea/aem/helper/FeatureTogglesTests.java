package com.silversea.aem.helper;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

public class FeatureTogglesTests {

    @Test
    public void returnsFalseIfNodeDoesNotExist() {
        FeatureToggles featureToggles = build();
        Assert.assertFalse(featureToggles.IsEnabled("myFeature"));
    }

    @Test
    public void returnsFalseIfKeyDoesNotExist() {
        FeatureToggles featureToggles = build();
        Assert.assertFalse(featureToggles.IsEnabled("myFeature"));
    }

    @Test @Ignore
    public void returnsTrueIfValueIsTrue() {
        HashMap<String, Object> nodes = new HashMap<String, Object>();
        //nodes.put(FeatureToggles.CRX_NODE_PATH, new Object());
        FeatureToggles featureToggles = build(nodes);
        Assert.assertTrue(featureToggles.IsEnabled("myFeature"));
    }

    private FeatureToggles build() {
        return build(new HashMap<>());
    }

    private FeatureToggles build(HashMap<String, Object> nodes) {
        FakeContentLoader contentLoader = new FakeContentLoader(nodes);
        return new FeatureToggles(contentLoader);
    }
}