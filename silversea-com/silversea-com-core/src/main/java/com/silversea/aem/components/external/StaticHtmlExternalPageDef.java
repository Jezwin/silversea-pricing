package com.silversea.aem.components.external;

import com.silversea.aem.models.AppSettingsModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;

public class StaticHtmlExternalPageDef extends ExternalPageDef {
    private PathRetriever pathRetriever;

    public StaticHtmlExternalPageDef(String crxRelativePathRegex, PathRetriever pathRetriever, FeatureToggle featureToggle, RequestChecker requestChecker) {
        super(crxRelativePathRegex, "/static.html", featureToggle, requestChecker);
        this.pathRetriever = pathRetriever;
    }

    @Override
    public ExternalPageAemContentOption getAemContentOption() {
        return ExternalPageAemContentOption.RemoveAemContent;
    }

    private String getUrl(AppSettingsModel appSettings) throws MalformedURLException {
        return new URL(new URL(appSettings.getStaticExternalUiRootUrl()), this.pathRetriever.get(appSettings)).toString();
    }

    private String prependRootUrl(String staticExternalUiRootUrl, String relativeUrl) {
        try {
            URL rootUrl = new URL(staticExternalUiRootUrl);
            URL newUrl = new URL(rootUrl, relativeUrl);
            return newUrl.toString();
        } catch (MalformedURLException e) {
            return relativeUrl;
        }
    }

    private void makeUrlsAbsolute(String elementName, String attributeName, Element parentElement, String rootUrl) {
        Elements linkElements = parentElement.select(String.format("%s[%s^=/]", elementName, attributeName));
        linkElements.forEach(x -> {
            String newHref = prependRootUrl(rootUrl, x.attr(attributeName));
            x.attr(attributeName, newHref);
        });
    }

    @Override
    public String getHeadMarkup(AppSettingsModel appSettings) throws Exception {
        Document doc = Jsoup.connect(getUrl(appSettings)).get();
        Element head = doc.head();
        //String rootUrl = appSettings.getStaticExternalUiRootUrl();
        //makeUrlsAbsolute("link", "href", head, rootUrl);
        return head.html();
    }

    public String getBodyMarkup(AppSettingsModel appSettings) throws Exception {
        Document doc = Jsoup.connect(getUrl(appSettings)).get();
        Element body = doc.body();
        //String rootUrl = appSettings.getStaticExternalUiRootUrl();
        //makeUrlsAbsolute("script", "src", body, rootUrl);
        return body.html();
    }

    public interface PathRetriever {
        String get(AppSettingsModel appSettings);
    }

}
