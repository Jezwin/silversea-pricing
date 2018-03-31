package com.silversea.aem.components.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.models.PriceModelLight;

/**
     * Represent a cruise item used to display cruise informations (especially geolocated) in find your cruise
     */
    public class CruiseItem {

        private CruiseModelLight cruiseModel;

        private PriceModelLight lowestPrice;

        private boolean isWaitList = true;
        
        private Locale locale;

        private List<ExclusiveOfferModel> exclusiveOffers = new ArrayList<>();

        public CruiseItem(final CruiseModelLight cruiseModelLight, final String market, final String currency, final Locale locale) {
            // init lowest price and waitlist based on geolocation
            this.cruiseModel = cruiseModelLight;
            this.lowestPrice = cruiseModelLight.getLowestPrices().get(market + currency);
            this.isWaitList = this.lowestPrice == null;
            
            // init exclusive offers based on geolocation
            for (ExclusiveOfferModel exclusiveOffer : cruiseModelLight.getExclusiveOffers()) {
                if (exclusiveOffer.getGeomarkets() != null
                        && exclusiveOffer.getGeomarkets().contains(market.toLowerCase())) {
                    exclusiveOffers.add(exclusiveOffer);
                }
            }

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
            for (ExclusiveOfferModel exclusiveOffer : exclusiveOffers) {
                if (exclusiveOffer.getPricePrefix() != null) {
                    return exclusiveOffer.getPricePrefix();
                }
            }

            return null;
        }

        public List<ExclusiveOfferModel> getExclusiveOffers() {
            return exclusiveOffers;
        }
    }