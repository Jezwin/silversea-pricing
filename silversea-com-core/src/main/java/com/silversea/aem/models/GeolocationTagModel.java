package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class)
public class GeolocationTagModel {

    @Inject @Named("iso2")
    private String countryCode;

    @Inject @Named("iso3")
    private String countryCodeIso3;

    @Inject
    private String market;

    @Inject @Named("Currency") @Optional
    private String currency;

    @Inject @Optional
    private String prefix;

    @Inject @Named("jcr:title") @Optional
    private String title;

    @Inject @Optional
    private String phone;

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryCodeIso3() {
        return countryCodeIso3;
    }

    public String getMarket() {
        return market.toLowerCase();
    }

    public String getCurrency() {
        // TODO issues with imported data
        if (currency.equals("Euro")) {
            return "EUR";
        }

        return currency;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTitle() {
        return title;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return market;
    }
}
