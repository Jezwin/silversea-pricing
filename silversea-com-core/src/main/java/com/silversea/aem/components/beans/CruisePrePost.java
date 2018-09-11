package com.silversea.aem.components.beans;

import com.silversea.aem.models.HotelModel;
import com.silversea.aem.models.LandProgramModel;

import java.util.Objects;

public class CruisePrePost {
    private final String thumbnail;
    private final String prePost;
    private final String category;
    private final String id;
    private final String title;
    private final Integer nights;
    private final String type;
    private final Integer itineraryId;

    public CruisePrePost(Integer itineraryId, String thumbnail, HotelModel hotel) {
        this.thumbnail = thumbnail;
        this.category = hotel.getCategory();
        this.title = hotel.getPage().getTitle();
        this.nights = hotel.getNights();
        this.prePost = "pre";
        this.id = hotel.getHotelId().toString();
        this.type = "hotel";
        this.itineraryId = itineraryId;
    }

    public CruisePrePost(Integer itineraryId, String thumbnail, LandProgramModel land) {
        this.thumbnail = thumbnail;
        this.prePost = "post";
        this.nights = land.getNights();
        this.title = land.getTitle();
        this.category = land.getCategory();
        this.id = land.getLandId().toString();
        this.type = "land";
        this.itineraryId = itineraryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CruisePrePost that = (CruisePrePost) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPrePost() {
        return prePost;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public Integer getNights() {
        return nights;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Integer getItineraryId() {
        return itineraryId;
    }
}
