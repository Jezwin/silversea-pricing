package com.silversea.aem.components.external;

import com.silversea.aem.models.AppSettingsModel;
import org.apache.sling.api.SlingHttpServletRequest;

public class ReactExternalPageDef extends ExternalPageDef {

    private ExternalPageAemContentOption aemContentOption;
    private String templatePath;

    public ReactExternalPageDef(String crxPathRegex, String templatePath) {

        this(crxPathRegex, templatePath, null, null, ExternalPageAemContentOption.RemoveAemContent);
    }

    public ReactExternalPageDef(String crxRelativePathRegex, String templateRelativePath, FeatureToggle featureToggle, RequestChecker requestChecker, ExternalPageAemContentOption aemContentOption) {
        super(crxRelativePathRegex, templateRelativePath, featureToggle, requestChecker);

        this.aemContentOption = aemContentOption;
    }

    @Override
    public ExternalPageAemContentOption getAemContentOption() {
        return this.aemContentOption;
    }

    @Override
    public String getHeadMarkup(AppSettingsModel appSettings) {

        String cssLink = String.format(
                "<link rel=\"stylesheet\" href=\"%s\" />\n",
                appSettings.getExternalUiCssUrl()
        );

        String js = String.format(
                "<script src=\"%s\"></script>\n",
                appSettings.getExternalUiJsUrl()
        );

        return new StringBuilder()
                .append(cssLink)
                .append("<script src=\"https://unpkg.com/react@16/umd/react.production.min.js\"></script>\n")
                .append("<script src=\"https://unpkg.com/react-dom@16/umd/react-dom.production.min.js\"></script>\n")
                .append(js)
                .toString();
    }

}
