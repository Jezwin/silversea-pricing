package com.silversea.aem.components.beans;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class PriceVariation {
    
    private String name;
    private Map<String, PriceData> variationPrices;
    
    public PriceVariation() {
    }
    
    public void init(){
        this.variationPrices = new HashMap<String, PriceData>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, PriceData> getVariationPrices() {
        return variationPrices;
    }

    public void setVariationPrices(Map<String, PriceData> variationPrices) {
        this.variationPrices = variationPrices;
    }
}
