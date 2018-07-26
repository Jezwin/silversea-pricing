package com.silversea.aem.components.beans;

import com.silversea.aem.models.ExclusiveOfferFareModel;

public class EoBean {

    private String title;
    private String shortTitle;
    private String description;
    private String shortDescription;
    private String footnote;
    private String mapOverhead;


    private Integer priorityWeight;
    private ExclusiveOfferFareModel[] cruiseFares;
    private String image;
    private boolean greyBoxesSystem;
    private boolean isAvailable;
    private String eoFootnotes;

    public EoBean() {
    }

    public EoBean(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFootnote() {
        return footnote;
    }

    public String getEoFootnotes() {
        return eoFootnotes;
    }

    public void setEoFootnotes(String eoFootnotes) {
        this.eoFootnotes = eoFootnotes;
    }

    public void setFootnote(String footnote) {
        this.footnote = footnote;
    }

    public String getMapOverhead() {
        return mapOverhead;
    }

    public void setMapOverhead(String mapOverhead) {
        this.mapOverhead = mapOverhead;
    }

    public ExclusiveOfferFareModel[] getCruiseFares() {
        return cruiseFares;
    }

    public void setCruiseFares(ExclusiveOfferFareModel[] cruiseFares) {
        this.cruiseFares = cruiseFares;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isGreyBoxesSystem() {
        return greyBoxesSystem;
    }

    public void setGreyBoxesSystem(boolean greyBoxesSystem) {
        this.greyBoxesSystem = greyBoxesSystem;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public Integer getPriorityWeight() {
        return priorityWeight;
    }

    public void setPriorityWeight(Integer priorityWeight) {
        this.priorityWeight = priorityWeight;
    }
}