package com.silversea.aem.components.external;

import com.silversea.aem.models.AppSettingsModel;
import org.apache.sling.api.SlingHttpServletRequest;

public abstract class ExternalPageDef {
    public static final String HTML_TEMPLATE_BASE_PATH = "/apps/silversea/silversea-com/components/external";
    static final String CRX_BASE_PATH_REGEX = "/content/silversea-com/[^\\/]+";
    protected final FeatureToggle featureToggle;
    protected String crxPathRegex;
    protected RequestChecker requestChecker;
    protected String templatePath;

    public ExternalPageDef(String crxRelativePathRegex, String templateRelativePath, FeatureToggle featureToggle, RequestChecker requestChecker) {
        this.crxPathRegex = CRX_BASE_PATH_REGEX + crxRelativePathRegex;
        this.templatePath = HTML_TEMPLATE_BASE_PATH + templateRelativePath;

        this.featureToggle = featureToggle;
        this.requestChecker = requestChecker;
    }

    public Boolean isPathMatch(String path) {
        return path.matches(this.crxPathRegex);
    }

    public boolean isEnabled(AppSettingsModel appSettings) {
        return this.featureToggle == null
                || this.featureToggle.isEnabled(appSettings);
    }

    public boolean isRequestMatch(SlingHttpServletRequest queryString) {
        return this.requestChecker == null
                || this.requestChecker.isMatch(queryString);
    }

    public String getTemplatePath() {
        return this.templatePath;
    }
    public abstract ExternalPageAemContentOption getAemContentOption();

    public interface FeatureToggle {
        Boolean isEnabled(AppSettingsModel appSettings);
    }

    public interface RequestChecker {
        Boolean isMatch(SlingHttpServletRequest request);
    }

    public abstract String getHeadMarkup(AppSettingsModel appSettings, String language) throws Exception;
}
