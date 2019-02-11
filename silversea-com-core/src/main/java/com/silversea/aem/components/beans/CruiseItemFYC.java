package com.silversea.aem.components.beans;


import com.silversea.aem.models.CruiseModelLight;

import java.util.Locale;

public class CruiseItemFYC {

    private CruiseItem cruiseItem;
    private String postPrice;

    public CruiseItemFYC(CruiseItem cruiseItem, String postPrice) {
        this.cruiseItem = cruiseItem;
        this.postPrice = postPrice;
    }

    public String getPostPrice() {
        return postPrice;
    }

    public CruiseItem getCruiseItem() {
        return cruiseItem;
    }
}

