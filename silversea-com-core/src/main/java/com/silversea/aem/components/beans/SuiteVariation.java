package com.silversea.aem.components.beans;

public class SuiteVariation {

    private String name;
    private Price price;


    public SuiteVariation() {
    }
    
    
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(Price price) {
        this.price = price;
    }
    public String getName() {
        return name;
    }
    public Price getPrice() {
        return price;
    }
}
