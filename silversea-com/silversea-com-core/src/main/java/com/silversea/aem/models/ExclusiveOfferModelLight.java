package com.silversea.aem.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.ArrayUtils;
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
    private String id;
    private List<String> geomarkets = new ArrayList<>();
    private String pricePrefix;
    private String path;
    private Map<String, String> postPriceCache;

    public ExclusiveOfferModelLight(ExclusiveOfferModel exclusiveOfferModel) {
        id = exclusiveOfferModel.getId();
        title = exclusiveOfferModel.getTitle();
        pricePrefix = exclusiveOfferModel.getPricePrefix();
        path = exclusiveOfferModel.getPath();
        geomarkets = exclusiveOfferModel.getGeomarkets();
        priorityWeight = exclusiveOfferModel.getDefaultPriorityWeight();
        postPriceCache = new HashMap<>();
        try {
            if (exclusiveOfferModel.getActiveSystem()) {
                postPriceCache = retrievePostPriceCache(exclusiveOfferModel);
            }
        } catch (Exception e) {
            LOGGER.error("Issue in FYC CAche when try to create postPriceCache", e);
        }
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
        if (StringUtils.isNotEmpty(exclusiveOfferModel.getDefaultPostPrice())) {
            postPriceOfferCache.put("default", exclusiveOfferModel.getDefaultPostPrice());
        }
        String[] customVoyageSettings = exclusiveOfferModel.getCustomVoyageSettings();
        if (ArrayUtils.isNotEmpty(customVoyageSettings)) {
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
        }
        return postPriceOfferCache;
    }

    public Map<String, String> getPostPriceCache() {
        return postPriceCache;
    }

    public Integer getPriorityWeight() {
        return priorityWeight;
    }

    public String getId() {
        return id;
    }
}

