package com.silversea.aem.components.external;

import com.silversea.aem.models.AppSettingsModel;
import org.apache.sling.api.SlingHttpServletRequest;

public class ExternalPageDef {

    static final String CRX_BASE_PATH_REGEX = "/content/silversea-com/[^\\/]+";
    static final String HTML_TEMPLATE_BASE_PATH = "/apps/silversea/silversea-com/components/external";

    private final FeatureToggle featureToggle;
    private ExternalPageAemContentOption aemContentOption;
    private String crxPathRegex;
    private String templatePath;
    private RequestChecker requestChecker;

    public ExternalPageDef(String crxPathRegex, String templatePath) {

        this(crxPathRegex, templatePath, null, null, ExternalPageAemContentOption.RemoveAemContent);
    }

    public ExternalPageDef(String crxRelativePathRegex, String templateRelativePath, FeatureToggle featureToggle, RequestChecker requestChecker, ExternalPageAemContentOption aemContentOption) {

        this.crxPathRegex = CRX_BASE_PATH_REGEX + crxRelativePathRegex;
        this.templatePath = HTML_TEMPLATE_BASE_PATH + templateRelativePath;
        this.featureToggle = featureToggle;
        this.aemContentOption = aemContentOption;
        this.requestChecker = requestChecker;
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

    public ExternalPageAemContentOption getAemContentOption() {
        return this.aemContentOption;
    }

    public boolean isRequestMatch(SlingHttpServletRequest queryString) {
        return this.requestChecker == null
                || this.requestChecker.isMatch(queryString);
    }

    public interface FeatureToggle {
        Boolean isEnabled(AppSettingsModel appSettings);
    }

    public interface RequestChecker {
        Boolean isMatch(SlingHttpServletRequest request);
    }
}
