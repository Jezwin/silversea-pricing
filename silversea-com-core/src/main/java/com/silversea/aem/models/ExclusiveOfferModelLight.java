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
    private String path;
    private Map<String, String> prePriceCache;

    public ExclusiveOfferModelLight(ExclusiveOfferModel exclusiveOfferModel) {

        title = exclusiveOfferModel.getTitle();
        path = exclusiveOfferModel.getPath();
        geomarkets = exclusiveOfferModel.getGeomarkets();
        priorityWeight = exclusiveOfferModel.getDefaultPriorityWeight();
        prePriceCache = retrievePrePriceCache(exclusiveOfferModel);
    }

    public List<String> getGeomarkets() {
        return geomarkets;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    private Map<String, String> retrievePrePriceCache(ExclusiveOfferModel exclusiveOfferModel) {
        Gson gson = new GsonBuilder().create();
        Map<String, String> prePriceOfferCache = new HashMap<>();
        String[] customVoyageSettings = exclusiveOfferModel.getCustomVoyageSettings();
        for (String setting : customVoyageSettings) {
            CustomVoyageSettingsModel customSettings = gson.fromJson(setting, CustomVoyageSettingsModel.class);
            boolean isPrePrice = customSettings.getType().equalsIgnoreCase("prePrice"),
                    isPrePricePresent = StringUtils.isNotEmpty(customSettings.getValue()),
                    isPrePriceActive = Boolean.valueOf(customSettings.getActive());
            if (isPrePrice && isPrePricePresent && isPrePriceActive) {
                String prePrice = customSettings.getValue();
                customSettings.getTags().ifPresent(tags -> {
                    for (String tag : tags) {
                        prePriceOfferCache.put(tag, prePrice);
                    }
                });
            }
        }
        return prePriceOfferCache;
    }

    public Map<String, String> getPrePriceCache() {
        return prePriceCache;
    }

    public Integer getPriorityWeight() {
        return priorityWeight;
    }
}

