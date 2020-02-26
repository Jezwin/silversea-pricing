package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

import java.net.URL;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Model(adaptables = Resource.class)
public class AppSettingsModel {

    @Inject
    @Optional
    private boolean exclusiveOffersExternalBffEnabled;
    @Inject
    @Optional
    private String externalUiJsUrl;
    @Inject
    @Optional
    private String externalUiCssUrl;
    @Inject
    @Optional
    private String algoliaAppId;
    @Inject
    @Optional
    private String algoliaApiKey;
    @Inject
    @Optional
    private String algoliaSearchIndexName;
    @Inject
    @Optional
    private String bffApiBaseUrl;
    @Inject
    @Optional
    private String graphQLEndpointUrl;
    @Inject
    @Optional
    private String sanityDataset;
    @Inject
    @Optional
    private String sanityProjectId;
    @Inject
    @Optional
    private Boolean termsAndConditionsExternalUiEnabled;
    @Inject
    @Optional
    private Boolean showAllCruisesOnAuthorEnabled;
    @Inject
    @Optional
    private Boolean antarcticaExperimentExternalUiEnabled;
    @Inject
    @Optional
    private Boolean antarcticaExperimentStaticExternalUiEnabled;
    @Inject
    @Optional
    private String staticExternalUiRootUrl;
    @Inject
    @Optional
    private String antarcticaExperimentStaticExternalUiPath;

    public AppSettingsModel() {
    }

    public Boolean isExclusiveOffersExternalBffEnabled() {
        return defaultIfNull(this.exclusiveOffersExternalBffEnabled, false);
    }

    public String getExternalUiJsUrl() {
        return externalUiJsUrl;
    }

    public String getExternalUiCssUrl() {
        return externalUiCssUrl;
    }

    public String getAlgoliaAppId() {
        return algoliaAppId;
    }

    public String getAlgoliaApiKey() {
        return algoliaApiKey;
    }

    public String getAlgoliaSearchIndexName() {
        return algoliaSearchIndexName;
    }

    public String getBffApiBaseUrl() {
        return bffApiBaseUrl;
    }

    public String getGraphQLEndpointUrl() {
        return graphQLEndpointUrl;
    }

    public String getSanityDataset() {
        return sanityDataset;
    }

    public String getSanityProjectId() {
        return sanityProjectId;
    }

    public Boolean isTermsAndConditionsExternalUiEnabled() {
        return defaultIfNull(this.termsAndConditionsExternalUiEnabled, false);
    }

    public Boolean getShowAllCruisesOnAuthorEnabled() {
        return defaultIfNull(this.showAllCruisesOnAuthorEnabled, false);
    }

    public Boolean isAntarcticaExperimentExternalUiEnabled() {
        return defaultIfNull(this.antarcticaExperimentExternalUiEnabled, false);
    }

    public Boolean isAntarcticaExperimentStaticExternalUiEnabled() {
        return defaultIfNull(this.antarcticaExperimentStaticExternalUiEnabled, false);
    }

    public String getStaticExternalUiRootUrl() {
        return this.staticExternalUiRootUrl == null
                ? "https://master.d1rtypgd03ay5i.amplifyapp.com/"
                : this.staticExternalUiRootUrl;
    }

    public String getAntarcticaExperimentStaticExternalUiPath() {
        return this.antarcticaExperimentStaticExternalUiPath == null
                ? "/destinations/antarctica/"
                : this.antarcticaExperimentStaticExternalUiPath;
    }
}
