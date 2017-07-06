package com.silversea.aem.enums;

public enum Currency {
    EU("EUR", "€"),
    UK("GBP", "£"),
    AS("AUD", "$"),
    FT("USD", "$");

    private String value;
    private String label;

    Currency(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

}
