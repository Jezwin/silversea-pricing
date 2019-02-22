package com.silversea.aem.components.beans;


public class CruiseItemFYC {

    private CruiseItem cruiseItem;
    private String postPrice;
    private boolean lastMinuteSavings;

    public CruiseItemFYC(CruiseItem cruiseItem, String postPrice, boolean lastMinuteSavings) {
        this.cruiseItem = cruiseItem;
        this.postPrice = postPrice;
        this.lastMinuteSavings = lastMinuteSavings;
    }

    public String getPostPrice() {
        return postPrice;
    }

    public CruiseItem getCruiseItem() {
        return cruiseItem;
    }

    public boolean isLastMinuteSavings() {
        return lastMinuteSavings;
    }
}

