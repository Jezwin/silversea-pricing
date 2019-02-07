package com.silversea.aem.components.beans;

import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.ExclusiveOfferModelLight;
import com.silversea.aem.models.PriceModelLight;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represent a cruise item used to display cruise informations (especially geolocated) in find your cruise
 */
public class CruiseItem {

    private final String postPrice;
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

        this.postPrice = retrieveExclusiveOfferPostPrice(exclusiveOffers, market);

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

    public String getPostPrice() {
        return postPrice;
    }

    private static String retrieveExclusiveOfferPostPrice(List<ExclusiveOfferModelLight> exclusiveOffers, String market) {
        Integer priorityWeight = Integer.MIN_VALUE;
        String postPrice = null;
        for (ExclusiveOfferModelLight exclusiveOffer : exclusiveOffers) {
            boolean isDefault = exclusiveOffer.getPostPriceCache().containsKey("default"),
                    isTheRightPostPrice = exclusiveOffer.getPriorityWeight() > priorityWeight;
            if (isTheRightPostPrice && isDefault) {
                priorityWeight = exclusiveOffer.getPriorityWeight();
                postPrice = exclusiveOffer.getPostPriceCache().get("default");
            }

            boolean isMarketPresent = exclusiveOffer.getPostPriceCache().containsKey(market);
            if (isMarketPresent) {
                String geo = exclusiveOffer.getPostPriceCache().get(market);
                postPrice = StringUtils.isNotEmpty(geo) ? geo : postPrice;
            }
        }
        return postPrice;
    }

    public List<ExclusiveOfferModelLight> getExclusiveOffers() {
        return exclusiveOffers;
    }
}