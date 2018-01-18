package com.silversea.aem.models;

public class DestinationItem {

    private String name;

    private String title;

    public DestinationItem(String name, String title) {
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