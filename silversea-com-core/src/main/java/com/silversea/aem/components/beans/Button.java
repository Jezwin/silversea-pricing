package com.silversea.aem.components.beans;

/**
 * Button Object
 * TODO move to beans package
 */
public class Button {
    private String title;
    private String titleTablet;
    private String reference;
    private String color;
    private String analyticType;
    private String size;

    /**
     * Constructor Button
     * @param title
     * @param title tablet
     * @param reference
     * @param color
     */
    public Button(String title , String titleTablet, String reference, String color, String analyticType, String size) {
        this.title = title;
        this.titleTablet = titleTablet;
        this.reference = reference;
        this.color = color;
        this.analyticType = analyticType;
        this.size = size;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the titleTablet
     */
    public String getTitleTablet() {
        return titleTablet;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the analyticType
     */
    public String getAnalyticType() {
        return analyticType;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }
}