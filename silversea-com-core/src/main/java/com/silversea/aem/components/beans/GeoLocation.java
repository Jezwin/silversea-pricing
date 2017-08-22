package com.silversea.aem.components.beans;

@Deprecated
public class GeoLocation {

    private String country;

    private String countryCode;

    private String geoMarketCode;

    public GeoLocation() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGeoMarketCode() {
        return geoMarketCode;
    }

    public void setGeoMarketCode(String geoMarketCode) {
        this.geoMarketCode = geoMarketCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
