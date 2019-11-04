package com.silversea.aem.components;

public class GeolocationHelper extends AbstractGeolocationAwareUse {
    public String getGeolocation() {
        return super.geolocation.getPath().replaceAll("^/etc/tags/geotagging/", "");
    }

    public String getLanguage() {
        return super.getCurrentPage().getPath().replaceAll("^/content/silversea-com/([^/\\.]+).*", "$1");
    }
}
