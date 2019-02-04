package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class CustomCss {

    @Inject
    @Optional
    private String css;

    public String getCss() {
        return css;
    }
}
