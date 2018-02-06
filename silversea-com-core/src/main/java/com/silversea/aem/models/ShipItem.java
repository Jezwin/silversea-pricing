package com.silversea.aem.models;

public class ShipItem {

    private String name;

    private String title;
    
    private String id;

    public ShipItem(String name, String title, String id) {
        this.name = name;
        this.title = title;
        this.id= id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
    
    public String getId() {
    	return id;
    }
}