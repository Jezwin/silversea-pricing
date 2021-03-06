package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;

public class ExternalizerHelper extends WCMUsePojo {

    private Boolean absolute;

    private String externalizedUrl;

    private String externalizedTranslatedUrl;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);
        absolute = get("absolute", Boolean.class);

        externalizedUrl = path != null ? externalizePath(path, absolute) : "";
        externalizedTranslatedUrl = path != null ? externalizeAndTranslatePath(path, absolute) : "";
    }

    /**
     * Externalizing the URL
     *
     * @param path Path to externalize
     * @return Externalized URL
     */
    private String externalizePath(final String path, final Boolean abs) {
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        String externalizedUrl = "";

        absolute = (abs != null) ? absolute : false;

        if (path != null && externalizer != null) {
            if (absolute) {
                externalizedUrl = externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, path);
            } else {
                externalizedUrl = externalizer.relativeLink(getRequest(), path);
            }
        }

        return externalizedUrl;
    }

    private String externalizeAndTranslatePath(final String path, final Boolean absolute) {
        final String language = LanguageHelper.getLanguage(getCurrentPage());
        final String translatedPath = path.replace("/en/", "/" + language + "/");

        return externalizePath(translatedPath, absolute);
    }

    /**
     * @return the externalized URL
     */
    public String getExternalizedUrl() {
        return externalizedUrl;
    }

    /**
     * @return the translated externalized URL
     */
    public String getExternalizedTranslatedUrl() {
        return externalizedTranslatedUrl;
    }
}