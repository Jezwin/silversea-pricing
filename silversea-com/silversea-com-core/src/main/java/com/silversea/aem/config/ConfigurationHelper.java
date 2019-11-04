package com.silversea.aem.config;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.models.AppSettingsModel;

public class ConfigurationHelper extends WCMUsePojo {

    private AppSettingsModel appSettings;

    @Override
    public void activate() throws Exception {
        CrxContentLoader contentLoader = new CrxContentLoader(super.getResourceResolver());
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);

        this.appSettings = configurationManager.getAppSettings();
    }

    public AppSettingsModel getAppSettings() {
        return this.appSettings;
    }
}