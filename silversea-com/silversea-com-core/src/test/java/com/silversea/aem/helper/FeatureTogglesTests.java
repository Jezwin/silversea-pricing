package com.silversea.aem.helper;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class FeatureTogglesTests {

    @Test
    public void returnsFalseIfNodeDoesNotExist() {
        Assert.assertFalse(new FeatureToggles().IsEnabled("myFeature"));
    }

    @Test
    public void returnsFalseIfKeyDoesNotExist() {
        Assert.assertFalse(new FeatureToggles().IsEnabled("myFeature"));
    }

    @Test @Ignore
    public void returnsTrueIfValueIsTrue() {
        Assert.assertTrue(new FeatureToggles().IsEnabled("myFeature"));
    }
}