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
    private Boolean termsAndConditionsExternalUiEnabled;
    @Inject @Optional
    private Boolean showAllCruisesOnAuthorEnabled;
    @Inject @Optional
    private Boolean antarcticaExperimentExternalUiEnabled;


    public AppSettingsModel() {}

    public Boolean isExclusiveOffersExternalBffEnabled() {
        return defaultIfNull(this.exclusiveOffersExternalBffEnabled, false);
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


    public Boolean getShowAllCruisesOnAuthorEnabled() {
        return defaultIfNull(this.showAllCruisesOnAuthorEnabled,false);
    }
    public Boolean isAntarcticaExperimentExternalUiEnabled() {
        return defaultIfNull(this.antarcticaExperimentExternalUiEnabled, false);
    }
}
