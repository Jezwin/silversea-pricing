package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;

public class ExternalizerHelper extends WCMUsePojo {

    private Boolean absolute;

    private String externalizedUrl;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);
        absolute = get("absolute", Boolean.class);

        externalizedUrl = (path != null) ? externalizePath(path, absolute) : "";
    }

    /**
     * Externalizing the URL
     * 
     * @param path Path to externalize
     * @return Externalized URL
     */
    private String externalizePath(String path, Boolean abs) {
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

    /**
     * @return the externalizedUrl
     */
    public String getExternalizedUrl() {
        return externalizedUrl;
    }
}