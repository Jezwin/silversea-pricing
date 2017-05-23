package com.silversea.aem.components.beans;

public class SuiteVariation {

    private String name;
    private PriceData price;


    public SuiteVariation() {
    }
    
    
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(PriceData price) {
        this.price = price;
    }
    public String getName() {
        return name;
    }
    public PriceData getPrice() {
        return price;
    }
}
