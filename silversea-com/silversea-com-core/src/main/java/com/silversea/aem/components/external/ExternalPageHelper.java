package com.silversea.aem.components.external;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.config.ConfigurationManager;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.models.AppSettingsModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Arrays;

public class ExternalPageHelper extends WCMUsePojo {

    private ExternalPageDef externalPageDef;

    public Boolean isExternalPage() {
        return this.externalPageDef != null;
    }

    public String getTemplatePath() {
        return this.externalPageDef.getTemplatePath();
    }

    public String getAemContentMode() {
        if (this.externalPageDef == null) return "Show";

        ExternalPageAemContentOption aemContentOption =
                this.externalPageDef.getAemContentOption();

        return aemContentOption == ExternalPageAemContentOption.RenderAsFallback
                ? "Fallback"
                : "Remove";
    }

    private ExternalPageDef getExternalPageDef(AppSettingsModel appSettings, String path, SlingHttpServletRequest request) {
        return Arrays.stream(ExternalPageDefs.All)
                .filter(x -> x.isEnabled(appSettings))
                .filter(x -> x.isPathMatch(path))
                .filter(x -> x.isRequestMatch(request))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void activate() throws Exception {
        ResourceResolver resourceResolver = super.getResourceResolver();
        CrxContentLoader contentLoader = new CrxContentLoader(resourceResolver);
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);

        AppSettingsModel appSettings = configurationManager.getAppSettings();
        String pagePath = this.getCurrentPage().getPath();
        SlingHttpServletRequest request = this.getRequest();

        this.externalPageDef = getExternalPageDef(appSettings, pagePath, request);
    }

}

