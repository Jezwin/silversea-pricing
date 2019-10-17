package com.silversea.aem.helper;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

public class FeatureTogglesTests {

    @Test
    public void returnsFalseIfNodeDoesNotExist() throws Exception {
        FakeContentLoader contentLoader = new FakeContentLoader();
        FeatureToggles featureToggles = new FeatureToggles(contentLoader);

        Assert.assertFalse(featureToggles.IsEnabled("myFeature"));
    }

    @Test
    public void returnsFalseIfKeyDoesNotExist() throws Exception {
        FakeContentLoader contentLoader = new FakeContentLoader();
        FeatureToggles featureToggles = new FeatureToggles(contentLoader);

        Assert.assertFalse(featureToggles.IsEnabled("myFeature"));
    }

    @Test
    public void returnsTrueIfValueIsTrue() throws Exception {
        HashMap<String, Object> nodeProperties = new HashMap<>();
        nodeProperties.put("myFeature", true);

        FakeContentLoader contentLoader = new FakeContentLoader();
        contentLoader.addNode(FeatureToggles.CRX_NODE_PATH, nodeProperties);
        FeatureToggles featureToggles = new FeatureToggles(contentLoader);

        Assert.assertTrue(featureToggles.IsEnabled("myFeature"));
    }

    @Test
    public void returnsFalseIfValueIsFalse() throws Exception {
        HashMap<String, Object> nodeProperties = new HashMap<>();
        nodeProperties.put("myFeature", false);

        FakeContentLoader contentLoader = new FakeContentLoader();
        contentLoader.addNode(FeatureToggles.CRX_NODE_PATH, nodeProperties);
        FeatureToggles featureToggles = new FeatureToggles(contentLoader);

        Assert.assertFalse(featureToggles.IsEnabled("myFeature"));
    }

    @Test
    public void returnsFalseIfValueIsInvalid() throws Exception {
        HashMap<String, Object> nodeProperties = new HashMap<>();
        nodeProperties.put("myFeature", "hgfhdjsfgj");

        FakeContentLoader contentLoader = new FakeContentLoader();
        contentLoader.addNode(FeatureToggles.CRX_NODE_PATH, nodeProperties);
        FeatureToggles featureToggles = new FeatureToggles(contentLoader);

        Assert.assertFalse(featureToggles.IsEnabled("myFeature"));
    }
}