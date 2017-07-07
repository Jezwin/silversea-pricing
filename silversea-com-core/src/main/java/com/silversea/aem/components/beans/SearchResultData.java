package com.silversea.aem.components.beans;

import java.util.List;

public class SearchResultData {
    
    private List<Cruise> cruises;
    private List<SearchFilter> destinations;
    private List<SearchFilter> cities;
    private List<SearchFilter> ships;
    private List<SearchFilter> types;
    private List<Feature> features;
    private List<String> dates;
    private List<String> durations;
  
    public List<Cruise> getCruises() {
        return cruises;
    }

    public void setCruises(List<Cruise> cruises) {
        this.cruises = cruises;
    }

    public List<SearchFilter> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<SearchFilter> destinations) {
        this.destinations = destinations;
    }

    public List<SearchFilter> getCities() {
        return cities;
    }

    public void setCities(List<SearchFilter> cities) {
        this.cities = cities;
    }

    public List<SearchFilter> getShips() {
        return ships;
    }

    public void setShips(List<SearchFilter> ships) {
        this.ships = ships;
    }

    public List<String> getDurations() {
        return durations;
    }

    public void setDurations(List<String> durations) {
        this.durations = durations;
    }

    public List<SearchFilter> getTypes() {
        return types;
    }

    public void setTypes(List<SearchFilter> types) {
        this.types = types;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    } 
    
}
