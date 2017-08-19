package com.silversea.aem.components.beans;

@Deprecated
public class PriceData {

    private String value;

    private String currency;

    private String marketCode;

    private boolean isWaitList;

    public PriceData() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isWaitList() {
        return isWaitList;
    }

    public void setWaitList(boolean isWaitList) {
        this.isWaitList = isWaitList;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }
}
