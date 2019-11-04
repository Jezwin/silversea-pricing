package com.silversea.aem.config;

import com.silversea.aem.content.ContentLoader;
import com.silversea.aem.models.AppSettingsModel;

public class ConfigurationManager {
    static final String CRX_NODE_PATH = "/content/silversea-com/settings";
    private final AppSettingsModel appSettings;

    public ConfigurationManager(ContentLoader contentLoader) throws Exception {
        this.appSettings = contentLoader.get(CRX_NODE_PATH, AppSettingsModel.class);
    }

    public AppSettingsModel getAppSettings() {
        return this.appSettings;
    }

}
