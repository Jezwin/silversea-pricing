package com.silversea.aem.components.beans;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.silversea.aem.technical.json.ArrayDeserializer;
import com.silversea.aem.technical.json.LongDeserializer;

public class SearchParameter {
    
    private String destinationId;
    private String departureDate;
    private String duration;
    private String shipId;
    private String cruiseType;
    private String cityId;
    @JsonDeserialize(using=LongDeserializer.class)
    private Long limit;
    @JsonDeserialize(using=LongDeserializer.class)
    private Long offset;
    @JsonDeserialize(using=ArrayDeserializer.class)
    private String [] features;
    private GeoLocation geolocation;
    private String language;

    public SearchParameter(){
        
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getShipId() {
        return shipId;
    }

    public void setShipId(String shipId) {
        this.shipId = shipId;
    }

    public String getCruiseType() {
        return cruiseType;
    }

    public void setCruiseType(String cruiseType) {
        this.cruiseType = cruiseType;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(String[] features) {
        this.features = features;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public GeoLocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeoLocation geolocation) {
        this.geolocation = geolocation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
