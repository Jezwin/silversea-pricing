package com.silversea.aem.components.external;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.config.ConfigurationManager;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.models.AppSettingsModel;
import org.apache.sling.api.resource.ResourceResolver;

public class ExternalPageHelper extends WCMUsePojo {
    static final String TERMS_AND_CONDITIONS_PATH_REGEX = "/content/silversea-com/[^\\/]+/terms-and-conditions";

    private AppSettingsModel appSettings;

    public Boolean isTermsAndConditionsPage() {
        return appSettings.isTermsAndConditionsExternalUiEnabled()
                && this.getCurrentPage().getPath().matches(TERMS_AND_CONDITIONS_PATH_REGEX);
    }

    @Override
    public void activate() throws Exception {
        ResourceResolver resourceResolver = super.getResourceResolver();
        CrxContentLoader contentLoader = new CrxContentLoader(resourceResolver);
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);
        this.appSettings = configurationManager.getAppSettings();
    }
}