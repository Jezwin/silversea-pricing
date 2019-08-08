package com.silversea.aem.models;

import com.silversea.aem.constants.WcmConstants;

public class PriceModelLight {

    private String availability;

    private String[] tagIds;

    private String geomarket;

    private String currency;

    private Long price;

    private Long earlyBookingBonus;

    private String suiteCategory;

    public PriceModelLight(PriceModel priceModel){
    	availability = priceModel.getAvailability();
    	tagIds = priceModel.getTagIds();
    	geomarket = priceModel.getGeomarket();
    	currency = priceModel.getCurrency();
    	price = priceModel.getPrice();
    	earlyBookingBonus = priceModel.getEarlyBookingBonus();
    	suiteCategory = priceModel.getSuiteCategory();
    	priceModel = null;
    }

    public String getAvailability() {
        return availability;
    }

    public String[] getTagIds() {
        return tagIds;
    }

    public String getGeomarket() {
        return geomarket;
    }

    public String getCurrency() {
        return currency;
    }

    public Long getPrice() {
        return price;
    }

    public Long getEarlyBookingBonus() {
        return earlyBookingBonus;
    }

    public Long getComputedPrice() {
        return earlyBookingBonus != null ? earlyBookingBonus : price;
    }

    public String getSuiteCategory() {
        return suiteCategory;
    }

    public boolean isWaitList() {
        return WcmConstants.PV_AVAILABILITY_WAITLIST.equals(availability);
    }
}
