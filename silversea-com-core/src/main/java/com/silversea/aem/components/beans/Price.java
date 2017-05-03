package com.silversea.aem.components.beans;

public class Price {
    
    private String value;
    private boolean isWaitList;
    private String currency;
    
    public Price() {
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
}