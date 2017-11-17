package com.silversea.aem.models;

public class ShipItem {

    private String name;

    private String title;

    public ShipItem(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}