package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Model(adaptables = Resource.class)
public class AppSettingsModel {

    @Inject @Optional
    private boolean exclusiveOffersExternalBffEnabled;
    @Inject @Optional
    private String externalUiJsUrl;
    @Inject @Optional
    private String bffApiBaseUrl;
    @Inject @Optional
    private Boolean findYourCruiseExternalUiEnabled;
    @Inject @Optional
    private String exclusiveOfferApiDomain;
    @Inject @Optional
    private Boolean termsAndConditionsExternalUiEnabled;

    public AppSettingsModel() {}

    public Boolean isExclusiveOffersExternalBffEnabled() {
        return defaultIfNull(this.exclusiveOffersExternalBffEnabled, false);
    }

    public String getExclusiveOfferApiDomain() { return defaultIfNull(exclusiveOfferApiDomain, "127.0.0.1:3000");
    }

    public Boolean isFindYourCruiseExternalUiEnabled() {
        return defaultIfNull(this.findYourCruiseExternalUiEnabled, false);
    }

    public String getExternalUiJsUrl() {
        return externalUiJsUrl;
    }

    public String getBffApiBaseUrl() {
        return bffApiBaseUrl;
    }

    public Boolean isTermsAndConditionsExternalUiEnabled() {
        return defaultIfNull(this.termsAndConditionsExternalUiEnabled, false);
    }
}
