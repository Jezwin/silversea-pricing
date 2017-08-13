package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class)
public class GeolocationTagModel {

    @Inject
    private String market;

    @Inject @Named("Currency")
    private String currency;

    public String getMarket() {
        return market;
    }

    public String getCurrency() {
        // TODO issues with imported data
        if (currency.equals("Euro")) {
            return "EUR";
        }

        return currency;
    }

    @Override
    public String toString() {
        return market;
    }
}
