package com.silversea.aem.components.beans;

import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.ExclusiveOfferModelLight;
import com.silversea.aem.models.PriceModelLight;
import com.silversea.aem.models.PriorityExclusiveOfferModel;
import org.apache.commons.collections.map.HashedMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represent a cruise item used to display cruise informations (especially geolocated) in find your cruise
 */
public class CruiseItem {

    private final Map<String, List<PriorityExclusiveOfferModel>> postPriceMap;
    private CruiseModelLight cruiseModel;

    private PriceModelLight lowestPrice;

    private boolean isWaitList = true;

    private Locale locale;

    private List<ExclusiveOfferModelLight> exclusiveOffers = new ArrayList<>();

    public CruiseItem(final CruiseModelLight cruiseModelLight, final String market, final String currency, final Locale locale) {
        // init lowest price and waitlist based on geolocation
        this.cruiseModel = cruiseModelLight;
        this.lowestPrice = cruiseModelLight.getLowestPrices().get(market + currency);
        this.isWaitList = this.lowestPrice == null;

        // init exclusive offers based on geolocation
        for (ExclusiveOfferModelLight exclusiveOffer : cruiseModelLight.getExclusiveOffers()) {
            if (exclusiveOffer.getGeomarkets() != null
                    && exclusiveOffer.getGeomarkets().contains(market.toLowerCase())) {
                exclusiveOffers.add(exclusiveOffer);
            }
        }

        this.postPriceMap = retrieveExclusiveOfferPostPrice(exclusiveOffers, market);

        this.locale = locale;
    }

    public CruiseModelLight getCruiseModel() {
        return cruiseModel;
    }

    public PriceModelLight getLowestPrice() {
        return lowestPrice;
    }

    public String getLowestPriceFormated() {
        return PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
    }

    public boolean isWaitList() {
        return isWaitList;
    }

    public String getPricePrefix() {
        for (ExclusiveOfferModelLight exclusiveOffer : exclusiveOffers) {
            if (exclusiveOffer.getPricePrefix() != null) {
                return exclusiveOffer.getPricePrefix();
            }
        }

        return null;
    }

    public Map<String, List<PriorityExclusiveOfferModel>> getPostPriceMap() {
        return postPriceMap;
    }

    private static Map<String, List<PriorityExclusiveOfferModel>> retrieveExclusiveOfferPostPrice(List<ExclusiveOfferModelLight> exclusiveOffers, String market) {
        Map<String, List<PriorityExclusiveOfferModel>> postPrice = new HashedMap();
        for (ExclusiveOfferModelLight exclusiveOffer : exclusiveOffers) {
            if (!exclusiveOffer.getPostPriceCache().isEmpty()) {
                Map<String, String> postPriceCache = exclusiveOffer.getPostPriceCache();
                List<PriorityExclusiveOfferModel> priorityExclusiveOfferModelList = new ArrayList<>();
                for (Map.Entry<String, String> entry : postPriceCache.entrySet()) {
                    PriorityExclusiveOfferModel priorityExclusiveOfferModel = new PriorityExclusiveOfferModel(entry.getKey(), entry.getValue());
                    priorityExclusiveOfferModelList.add(priorityExclusiveOfferModel);
                }
                postPrice.put(exclusiveOffer.getPath(), priorityExclusiveOfferModelList);
            }
        }
        return postPrice;
    }

    public List<ExclusiveOfferModelLight> getExclusiveOffers() {
        return exclusiveOffers;
    }
}