package com.silversea.aem.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExclusiveOfferModelLight {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOfferModelLight.class);
    private final Integer priorityWeight;
    private String title;

    private List<String> geomarkets = new ArrayList<>();
    private String pricePrefix;
    private String path;
    private Map<String, String> postPriceCache;

    public ExclusiveOfferModelLight(ExclusiveOfferModel exclusiveOfferModel) {

        title = exclusiveOfferModel.getTitle();
        pricePrefix = exclusiveOfferModel.getPricePrefix();
        path = exclusiveOfferModel.getPath();
        geomarkets = exclusiveOfferModel.getGeomarkets();
        priorityWeight = exclusiveOfferModel.getDefaultPriorityWeight();
        postPriceCache = retrievePostPriceCache(exclusiveOfferModel);
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

    private Map<String, String> retrievePostPriceCache(ExclusiveOfferModel exclusiveOfferModel) {
        Gson gson = new GsonBuilder().create();
        Map<String, String> postPriceOfferCache = new HashMap<>();
        String[] customVoyageSettings = exclusiveOfferModel.getCustomVoyageSettings();
        for (String setting : customVoyageSettings) {
            CustomVoyageSettingsModel customSettings = gson.fromJson(setting, CustomVoyageSettingsModel.class);
            boolean isPostPrice = customSettings.getType().equalsIgnoreCase("postPrice"),
                    isPostPricePresent = StringUtils.isNotEmpty(customSettings.getValue()),
                    isPostPriceActive = Boolean.valueOf(customSettings.getActive());
            if (isPostPrice && isPostPricePresent && isPostPriceActive) {
                String postPrice = customSettings.getValue();
                customSettings.getTags().ifPresent(tags -> {
                    for (String tag : tags) {
                        postPriceOfferCache.put(tag, postPrice);
                    }
                });
            }
        }
        return postPriceOfferCache;
    }

    public Map<String, String> getPostPriceCache() {
        return postPriceCache;
    }

    public Integer getPriorityWeight() {
        return priorityWeight;
    }
}

