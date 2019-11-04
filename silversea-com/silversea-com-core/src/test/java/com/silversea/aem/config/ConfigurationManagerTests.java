package com.silversea.aem.config;

import com.silversea.aem.content.FakeContentLoader;
import com.silversea.aem.models.AppSettingsModel;
import com.silversea.aem.newcruisedisclaimer.NewCruiseDisclaimerChecker;
import com.silversea.aem.newcruisedisclaimer.NewCruiseDisclaimerModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationManagerTests {
    @Test(expected = Exception.class)
    public void throwsIfNodeDoesNotExist() throws Exception {
        FakeContentLoader contentLoader = new FakeContentLoader();
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);
        configurationManager.getAppSettings();
    }

    @Test
    public void returnsSettingsIfNodeExists() throws Exception {
        FakeContentLoader contentLoader = new FakeContentLoader();
        AppSettingsModel node = new AppSettingsModel();
        contentLoader.addNode(ConfigurationManager.CRX_NODE_PATH, node);
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);

        AppSettingsModel result = configurationManager.getAppSettings();

        Assert.assertTrue(EqualsBuilder.reflectionEquals(node, result));
    }

}