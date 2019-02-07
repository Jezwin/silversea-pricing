package com.silversea.aem.components.beans;


import com.silversea.aem.models.CruiseModelLight;

import java.util.Locale;

public class CruiseItemFYC extends CruiseItem {

    private String postPrice;

    public CruiseItemFYC(CruiseModelLight cruiseModelLight, String market, String currency, Locale locale) {
        super(cruiseModelLight, market, currency, locale);
    }


    public String getPostPrice() {
        return postPrice;
    }
}

