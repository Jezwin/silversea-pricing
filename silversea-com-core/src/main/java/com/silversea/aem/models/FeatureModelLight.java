package com.silversea.aem.models;

import java.util.Objects;

public class FeatureModelLight {

    private String title;

    private String featureId;

    private String icon;

    private String featureCode;

    private String description;

    private String name;

    private String path;

    public FeatureModelLight(FeatureModel featureModel){
    	title = featureModel.getTitle();
    	featureId = featureModel.getFeatureId();
    	icon = featureModel.getIcon();
    	featureCode = featureModel.getFeatureCode();
    	description = featureModel.getDescription();
    	name = featureModel.getName();
    	path = featureModel.getPath();
    }
   
    public String getTitle() {
        return title;
    }

    public String getFeatureId() {
        return featureId;
    }

    public String getIcon() {
        return icon;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof FeatureModelLight)) {
            return false;
        }

        final FeatureModelLight objFeatureModel = (FeatureModelLight) obj;

        return objFeatureModel.getPath().equals(getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, featureId, icon, featureCode, description, name, path);
    }
}
