package com.silversea.aem.components.beans;

public class GeoLocation {
    
    String country;
    String geoMarketCode;
    
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
}