package com.silversea.aem.components.beans;

public class ItinerariesData {

    private int nbHotels = 0;
    private int nbExcursions = 0;
    private int nbLandPrograms = 0;

    public ItinerariesData() {

    }

    public ItinerariesData(int nbHotels, int nbExcursions, int nbLandPrograms) {
        super();
        this.nbHotels = nbHotels;
        this.nbExcursions = nbExcursions;
        this.nbLandPrograms = nbLandPrograms;
    }

    public int getNbHotels() {
        return nbHotels;
    }

    public void setNbHotels(int nbHotels) {
        this.nbHotels = nbHotels;
    }

    public int getNbExcursions() {
        return nbExcursions;
    }

    public void setNbExcursions(int nbExcursions) {
        this.nbExcursions = nbExcursions;
    }

    public int getNbLandPrograms() {
        return nbLandPrograms;
    }

    public void setNbLandPrograms(int nbLandPrograms) {
        this.nbLandPrograms = nbLandPrograms;
    }
}
