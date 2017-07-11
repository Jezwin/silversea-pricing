package com.silversea.aem.components.beans;

import java.util.List;

public class Cruise {
    
    private String thumbnail;
    private String title;
    private String type;    
    private String ship;
    private String startDate;
    private String destination;
    private String duration;
    private String cruiseCode;
    private List<Feature> features;
    private PriceData lowestPrice;
    private List<String> ititneraries;
    
    public Cruise(){
        
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShip() {
        return ship;
    }

    public void setShip(String ship) {
        this.ship = ship;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public PriceData getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(PriceData lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public List<String> getItitneraries() {
        return ititneraries;
    }

    public void setItitneraries(List<String> ititneraries) {
        this.ititneraries = ititneraries;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCruiseCode() {
        return cruiseCode;
    }

    public void setCruiseCode(String cruiseCode) {
        this.cruiseCode = cruiseCode;
    } 
}
