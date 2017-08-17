package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.services.GeolocationService;

public class SignUpUse extends WCMUsePojo {

    private String pageReference;
    private GeoLocation geoLocation;
    private String siteCountry;
    private String siteLanguage;
    private String siteCurrency;

    @Override
    public void activate() throws Exception {
        final InheritanceValueMap allInheritedProperties = new HierarchyNodeInheritanceValueMap(getResource());
        pageReference = allInheritedProperties.getInherited("pageReference", String.class);

        //get site country and currency
        GeolocationService geolocationService = getSlingScriptHelper().getService(GeolocationService.class);
        geoLocation = geolocationService.initGeolocation(getRequest());
        if (geoLocation != null) {
            siteCountry = geoLocation.getCountry();
        }
        siteCurrency = geolocationService.getLocalizedCurrency(getRequest());

        //get site language
        siteLanguage = LanguageHelper.getLanguage(getRequest());
        if (siteLanguage == null) {
            siteLanguage = LanguageHelper.getLanguage(getCurrentPage());
        }
    }

    public String getPageReference() {
        return pageReference;
    }

    public String getSiteCountry() {
        return siteCountry;
    }

    public void setSiteCountry(String siteCountry) {
        this.siteCountry = siteCountry;
    }

    public String getSiteLanguage() {
        return siteLanguage;
    }

    public void setSiteLanguage(String siteLanguage) {
        this.siteLanguage = siteLanguage;
    }

    public String getSiteCurrency() {
        return siteCurrency;
    }

    public void setSiteCurrency(String siteCurrency) {
        this.siteCurrency = siteCurrency;
    }

}