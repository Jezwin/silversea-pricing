package com.silversea.aem.helper;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class NewCruiseDisclaimerNode {

    @Inject
    private String cruiseCodes;

    public NewCruiseDisclaimerNode() {}

    public NewCruiseDisclaimerNode(String cruiseCodes) {
        this.cruiseCodes = cruiseCodes;
    }

    public String getCruiseCodes() {
        return this.cruiseCodes;
    }
}
