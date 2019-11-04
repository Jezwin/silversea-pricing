package com.silversea.aem.components.external;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.config.ConfigurationManager;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.models.AppSettingsModel;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Arrays;

public class ExternalPageHelper extends WCMUsePojo {
    final ExternalPageDef[] externalPageDefs = {
            new ExternalPageDef("/terms-and-conditions",
                    "/termsandconditions/termsandconditions.html",
                    x -> x.isTermsAndConditionsExternalUiEnabled()),
            new ExternalPageDef("/cruise/cruise-results",
                    "/findyourcruise/findyourcruise.html",
                    x -> x.isFindYourCruiseExternalUiEnabled())
    };

    static final String EXTERNAL_TEMPLATE_PATH = "/apps/silversea/silversea-com/components/external";

    private AppSettingsModel appSettings;

    public Boolean isExternalPage() {
        String currentPagePath = this.getCurrentPage().getPath();
        return Arrays.stream(externalPageDefs)
                .filter(x -> x.featureToggle.isEnabled(appSettings))
                .anyMatch(x -> pathMatches(x, currentPagePath));
    }

    public String getTemplatePath() {
        String currentPagePath = this.getCurrentPage().getPath();
        return Arrays.stream(externalPageDefs)
                .filter(x -> x.featureToggle == null || x.featureToggle.isEnabled(appSettings))
                .filter(x -> pathMatches(x, currentPagePath))
                .map(x -> EXTERNAL_TEMPLATE_PATH + x.templatePath)
                .findFirst()
                .orElse(null);
    }

    private boolean pathMatches(ExternalPageDef x, String path) {
        return path.matches("/content/silversea-com/[^\\/]+" + x.crxPathRegex);
    }

    @Override
    public void activate() throws Exception {
        ResourceResolver resourceResolver = super.getResourceResolver();
        CrxContentLoader contentLoader = new CrxContentLoader(resourceResolver);
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);
        this.appSettings = configurationManager.getAppSettings();
    }

    public interface FeatureToggle {
        Boolean isEnabled(AppSettingsModel appSettings);
    }

    private class ExternalPageDef {
        private final FeatureToggle featureToggle;
        private String crxPathRegex;
        private String templatePath;

        public ExternalPageDef(String crxPathRegex, String templatePath) {
            this(crxPathRegex, templatePath, null);
        }

        public ExternalPageDef(String crxPathRegex, String templatePath, FeatureToggle featureToggle) {

            this.crxPathRegex = crxPathRegex;
            this.templatePath = templatePath;
            this.featureToggle = featureToggle;
        }

        public String getCrxPathRegex() {
            return crxPathRegex;
        }

        public String getTemplatePath() {
            return templatePath;
        }
    }
}