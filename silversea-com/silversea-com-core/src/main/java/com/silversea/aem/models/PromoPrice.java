package com.silversea.aem.models;

import com.google.gson.annotations.SerializedName;

public class PromoPrice {

    @SerializedName("rt_business_price_promo")
    private int businessClassPromoPrice;

    @SerializedName("rt_economy_price_promo")
    private int economyClassPromoPrice;

    public int getBusinessClassPromoPrice() {
        return businessClassPromoPrice;
    }

    public int getBusinessClassPromoPriceEachWay() {
        return businessClassPromoPrice / 2 ;
    }

    public boolean getHasFreeEconomy() {
        return economyClassPromoPrice == 0;
    }
}
