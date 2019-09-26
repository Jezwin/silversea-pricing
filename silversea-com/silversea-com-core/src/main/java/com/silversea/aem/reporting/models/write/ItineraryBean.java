package com.silversea.aem.reporting.models.write;

import com.silversea.aem.models.ItineraryModel;

public class ItineraryBean {

    private final Integer cruiseId;
    private final Integer itineraryId;
    private final Integer numberOfDays;
    private final Boolean hasDedicatedShorex;
    private final int numberOfExcursions;
    private final int numberOfHotels;
    private final int numberOfLandPrograms;
    private final Integer portId;

    public ItineraryBean(ItineraryModel itineraryModel) {
        cruiseId = itineraryModel.getCruiseId();
        itineraryId = itineraryModel.getItineraryId();
        numberOfDays = itineraryModel.getNumberDays();
        hasDedicatedShorex = itineraryModel.getHasDedicatedShorex();
        numberOfExcursions = itineraryModel.getExcursions().size();
        numberOfHotels = itineraryModel.getHotels().size();
        numberOfLandPrograms = itineraryModel.getLandPrograms().size();
        portId = itineraryModel.getPortId();
    }

    public Integer getCruiseId() {
        return cruiseId;
    }

    public Integer getItineraryId() {
        return itineraryId;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public Boolean getHasDedicatedShorex() {
        return hasDedicatedShorex;
    }

    public int getNumberOfExcursions() {
        return numberOfExcursions;
    }

    public int getNumberOfHotels() {
        return numberOfHotels;
    }

    public int getNumberOfLandPrograms() {
        return numberOfLandPrograms;
    }

    public Integer getPortId() {
        return portId;
    }
}
