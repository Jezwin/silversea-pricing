package com.silversea.aem.enums;

public enum FacetKey {
    
    DESTINATION("4_property"),
    CITIES("7_property"),
    SHIP("5_property"),
    DURATION("6_property"),
    DATES("8_property"),
    TAGS("tags");
    
    private String value;
  
    FacetKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
