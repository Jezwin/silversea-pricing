package com.silversea.aem.components.beans;

import com.silversea.aem.models.HotelModel;
import com.silversea.aem.models.LandProgramModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CruisePrePost {
    public enum PREPOSTMID {
        PRE("PRE"), POST("POST"), MID("MID"), NONE("");
        String name;

        PREPOSTMID(String name) {
            this.name = name;
        }

        public static PREPOSTMID retrieve(String code) {
            if (code.matches(".*PO?ST.*")) {
                return POST;
            } else if (code.matches(".*PRE.*")) {
                return PRE;
            } else if (code.matches(".*MID.*")) {
                return MID;
            } else {
                return NONE;
            }
        }

        public String getName() {
            return name;
        }
    }

    private final String thumbnail;
    private final String fallbackThumbnail;
    private final PREPOSTMID prePost;
    private final String category;
    private final String id;
    private final String code;
    private final String title;
    private final String customTitle;
    private final Integer nights;
    private final String type;
    private final Integer itineraryId;
    private final static List<String> coutoureCollectoinCodes =
            Arrays.asList("05PREHKGTW", "05PSTHKGTW", "06PREDRWAO", "06PREKEFFI", "06PSTDRWAO", "06PSTKEFFI",
                    "07PRECPTNR", "07PREZNZRS", "07PSTZNZRS", "08PRETYOGK", "08PSTMCTRP", "08PSTTYOGK", "09PRECAOBR",
                    "09PSTCAOBR"
            );//Mario dixit

    public CruisePrePost(Integer itineraryId, String thumbnail, String fallbackThumbnail, HotelModel hotel) {
        this.thumbnail = thumbnail;
        this.fallbackThumbnail = fallbackThumbnail;
        this.category = hotel.getCategory();
        this.title = hotel.getPage().getTitle();
        this.id = hotel.getHotelId().toString();
        this.code = hotel.getCode();
        this.nights = 0;
        this.customTitle = hotel.getCustomTitle();
        this.prePost = PREPOSTMID.retrieve(hotel.getCode());
        this.type = "hotel";
        this.itineraryId = itineraryId;
    }

    public CruisePrePost(Integer itineraryId, String thumbnail, String fallbackThumbnail, LandProgramModel land) {
        this.thumbnail = thumbnail;
        this.fallbackThumbnail = fallbackThumbnail;
        this.title = land.getTitle();
        this.id = land.getLandId().toString();
        this.code = land.getLandCode();
        this.category = coutoureCollectoinCodes.contains(code) ? "Couture Collection" : "";
        this.prePost = PREPOSTMID.retrieve(land.getLandCode().substring(2, 6));
        this.nights = numberOfNights(code);
        this.customTitle = land.getCustomTitle();
        this.type = "land";
        this.itineraryId = itineraryId;
    }

    private int numberOfNights(String landCode) {
        try {
            return Integer.parseInt((landCode.substring(0, 2).replaceFirst("^0?", "")));
        } catch (Exception e) {
            return 0;
        }

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
        return prePost.getName();
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

    public String getCustomTitle() {
        return customTitle;
    }

    public String getType() {
        return type;
    }

    public Integer getItineraryId() {
        return itineraryId;
    }

    public String getFallbackThumbnail() {
        return fallbackThumbnail;
    }

    public String getCode() {
        return code;
    }
}
