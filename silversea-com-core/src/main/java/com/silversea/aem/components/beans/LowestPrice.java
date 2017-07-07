package com.silversea.aem.components.beans;

import java.util.HashMap;
import java.util.Map;

public class LowestPrice {

    private Map<String, PriceData> globalPrices;
    private Map<String, PriceVariation> variationPrices;

    public LowestPrice(){

    }

    public void initGlobalPrices(){
        this.globalPrices = new HashMap<String, PriceData>();
    }

    public Map<String, PriceData> getVariation(String key){     
        Map<String, PriceData> variations = null;
        if(key!=null && variationPrices.get(key)!=null){
            variations = variationPrices.get(key).getVariationPrices();
        }
        return variations;
    }
    
    public void addVariation(String key){
        if(variationPrices != null && !variationPrices.containsKey(key)){
            PriceVariation priceVariation = new PriceVariation();
            priceVariation.init();
            variationPrices.put(key, priceVariation);
        }
    }
    public void initVariationPrices(){
        this.variationPrices = new HashMap<String, PriceVariation>();
    }

    public Map<String, PriceData> getGlobalPrices() {
        return globalPrices;
    }

    public void setGlobalPrices(Map<String, PriceData> globalPrices) {
        this.globalPrices = globalPrices;
    }

    public Map<String, PriceVariation> getVariationPrices() {
        return variationPrices;
    }

    public void setVariationPrices(Map<String, PriceVariation> variationPrices) {
        this.variationPrices = variationPrices;
    }
}
