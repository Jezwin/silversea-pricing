package com.silversea.aem.components.external;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.config.ConfigurationManager;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.models.AppSettingsModel;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Arrays;

// "/content/silversea-com/[^\\/]+/terms-and-conditions"
// "/apps/silversea/silversea-com/components/external/termsandconditions/termsandconditions.html"

public class ExternalPageHelper extends WCMUsePojo {
    final ExternalPageDef[] externalPageDefs = {
            new ExternalPageDef("/terms-and-conditions",
                    "/termsandconditions/termsandconditions.html",
                    x -> x.isTermsAndConditionsExternalUiEnabled())
    };

    static final String EXTERNAL_TEMPLATE_PATH = "/apps/silversea/silversea-com/components/external";
    static final String TERMS_AND_CONDITIONS_PATH_REGEX = "/content/silversea-com/[^\\/]+/terms-and-conditions";
    static final String FIND_YOUR_CRUISE_RESULTS_PATH_REGEX = "/content/silversea-com/[^\\/]+/cruise/cruise-results";

    private AppSettingsModel appSettings;

    public Boolean isExternalPage() {
        return Arrays.stream(externalPageDefs)
                .filter(x -> x.featureToggle.isEnabled(appSettings))
                .anyMatch(x -> matchesCurrentPage(x));
    }

    public String getTemplatePath() {
        return Arrays.stream(externalPageDefs)
                .filter(x -> x.featureToggle.isEnabled(appSettings))
                .filter(x -> matchesCurrentPage(x))
                .map(x -> EXTERNAL_TEMPLATE_PATH + x.templatePath)
                .findFirst()
                .orElse(null);
    }

    private boolean matchesCurrentPage(ExternalPageDef x) {
        return this.getCurrentPage().getPath().matches("/content/silversea-com/[^\\/]+" + x.crxPathRegex);
    }

    public Boolean isTermsAndConditionsPage() {
        return appSettings.isTermsAndConditionsExternalUiEnabled()
                && this.getCurrentPage().getPath().matches(TERMS_AND_CONDITIONS_PATH_REGEX);
    }

    public Boolean isFindYourCruiseResultsPage() {
        return appSettings.isFindYourCruiseExternalUiEnabled()
                && this.getCurrentPage().getPath().matches(FIND_YOUR_CRUISE_RESULTS_PATH_REGEX);
    }

    @Override
    public void activate() throws Exception {
        ResourceResolver resourceResolver = super.getResourceResolver();
        CrxContentLoader contentLoader = new CrxContentLoader(resourceResolver);
        ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);
        this.appSettings = configurationManager.getAppSettings();
    }

    public interface FeatureToggle {
        public Boolean isEnabled(AppSettingsModel appSettings);
    }

    private class ExternalPageDef {
        private final FeatureToggle featureToggle;
        private String crxPathRegex;
        private String templatePath;

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