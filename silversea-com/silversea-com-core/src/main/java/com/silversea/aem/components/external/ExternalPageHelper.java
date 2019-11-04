package com.silversea.aem.components.external;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.config.ConfigurationManager;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.models.AppSettingsModel;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Arrays;

public class ExternalPageHelper extends WCMUsePojo {

    private AppSettingsModel appSettings;

    public Boolean isExternalPage() {
        String currentPagePath = this.getCurrentPage().getPath();
        return Arrays.stream(ExternalPageDefs.All)
                .filter(x -> x.isEnabled(this.appSettings))
                .anyMatch(x -> x.isPathMatch(currentPagePath));
    }

    public String getTemplatePath() {
        String currentPagePath = this.getCurrentPage().getPath();
        return Arrays.stream(ExternalPageDefs.All)
                .filter(x ->  x.isEnabled(this.appSettings))
                .filter(x -> x.isPathMatch(currentPagePath))
                .map(x -> x.getTemplatePath())
                .findFirst()
                .orElse(null);
    }

    @Override
    public void activate() throws Exception {
        ResourceResolver resourceResolver = super.getResourceResolver();
        CrxContentLoader contentLoader = new CrxContentLoader(resourceResolver);
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);
        this.appSettings = configurationManager.getAppSettings();
    }

}

