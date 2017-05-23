package com.silversea.aem.enums;

public enum CruiseType {
    
    SILVERSEA_CTUISE("Silversea Cruise"),
    SILVERSEA_EXPEDITION_CTUISE("Silversea Expedition");

    private String value;
    
    private CruiseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
