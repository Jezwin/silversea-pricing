package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class GeolocationTagModel {

    @Inject
    private String market;

    @Inject
    private String currency;

    public String getMarket() {
        return market;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return market;
    }
}
