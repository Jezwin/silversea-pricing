package com.silversea.aem.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExclusiveOfferModelLight {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOfferModelLight.class);

    private String title;

    private List<String> geomarkets = new ArrayList<>();
    private String pricePrefix;
    private String path;

    public ExclusiveOfferModelLight(ExclusiveOfferModel exclusiveOfferModel) {

        title = exclusiveOfferModel.getTitle();
        pricePrefix = exclusiveOfferModel.getPricePrefix();
        path = exclusiveOfferModel.getPath();
        geomarkets = exclusiveOfferModel.getGeomarkets();
    }

    public List<String> getGeomarkets() {
        return geomarkets;
    }

    public String getPricePrefix() {
        return pricePrefix;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

}

