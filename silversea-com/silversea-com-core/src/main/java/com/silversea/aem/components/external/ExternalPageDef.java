package com.silversea.aem.components.external;

import com.silversea.aem.models.AppSettingsModel;

public class ExternalPageDef {

    static final String CRX_BASE_PATH_REGEX = "/content/silversea-com/[^\\/]+";
    static final String HTML_TEMPLATE_BASE_PATH = "/apps/silversea/silversea-com/components/external";

    private final FeatureToggle featureToggle;
    private String crxPathRegex;
    private String templatePath;

    public ExternalPageDef(String crxPathRegex, String templatePath) {

        this(crxPathRegex, templatePath, null);
    }

    public ExternalPageDef(String crxRelativePathRegex, String templateRelativePath, FeatureToggle featureToggle) {

        this.crxPathRegex = CRX_BASE_PATH_REGEX + crxRelativePathRegex;
        this.templatePath = HTML_TEMPLATE_BASE_PATH + templateRelativePath;
        this.featureToggle = featureToggle;
    }

    public String getTemplatePath() {
        return this.templatePath;
    }

    public Boolean isPathMatch(String path) {
        return path.matches(this.crxPathRegex);
    }

    public boolean isEnabled(AppSettingsModel appSettings) {
        return this.featureToggle == null
                || this.featureToggle.isEnabled(appSettings);
    }

    public interface FeatureToggle {
        Boolean isEnabled(AppSettingsModel appSettings);
    }
}
