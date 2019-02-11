package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class OfferPriorityModel {

    @Inject
    @Optional
    private String offerPath;

    @Inject
    @Optional
    private String priority;

    public String getOfferPath() {
        return offerPath;
    }

    public String getPriority() {
        return priority;
    }
}
