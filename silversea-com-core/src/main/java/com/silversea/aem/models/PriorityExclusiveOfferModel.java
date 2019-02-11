package com.silversea.aem.models;

public class PriorityExclusiveOfferModel {

    private String market;
    private String postPrice;

    public PriorityExclusiveOfferModel(String market, String postPrice) {
        this.market = market;
        this.postPrice = postPrice;
    }

    public String getPostPrice() {
        return postPrice;
    }

    public String getMarket() {
        return market;
    }
}
