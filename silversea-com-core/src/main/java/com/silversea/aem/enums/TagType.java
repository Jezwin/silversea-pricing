package com.silversea.aem.enums;

public enum TagType {

    CRUISES("cruise-type"),
    DUARATIONS("cruises-durations"),
    FEATURES("features");
    
    private String value;
  
    TagType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}