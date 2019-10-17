package com.silversea.aem.newcruisedisclaimer;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class NewCruiseDisclaimerModel {

    @Inject
    private String cruiseCodes;

    public NewCruiseDisclaimerModel() {}

    public NewCruiseDisclaimerModel(String cruiseCodes) {
        this.cruiseCodes = cruiseCodes;
    }

    public String getCruiseCodes() {
        return this.cruiseCodes;
    }
}
