package com.silversea.aem.models;

import java.util.Optional;

public class CustomVoyageSettingsModel {
    private String active;
    private String type;
    private String shortDescription;
    private String value;
    private String valueIcon;
    private String tags;

    private CustomVoyageSettingsModel() {
    }

    public String getActive() {
        return active;
    }

    public String getType() {
        return type;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getValue() {
        return value;
    }

    public String getValueIcon() {
        return valueIcon;
    }

    public Optional<String[]> getTags() {
        return Optional.ofNullable(tags).map(tag -> tags.replace("geotagging:", "").split(","));
    }
}