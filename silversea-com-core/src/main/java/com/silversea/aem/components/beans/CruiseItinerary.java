package com.silversea.aem.components.beans;

import com.silversea.aem.models.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class CruiseItinerary {
    private final int day;
    private final int itineraryId;
    private final String thumbnail;
    private final String name;
    private final String countryIso3;

    private final List<ExcursionModel> excursions;
    private final List<CruisePrePost> prePosts;

    private final List<LandProgramModel> landPrograms;
    private final List<HotelModel> hotels;
    private final boolean hasExcursions;
    private final Calendar date;
    private final String arriveTime;
    private final String departTime;
    private final String excursionDescription;
    private final boolean overnight;

    public CruiseItinerary(int day, boolean isEmbark, boolean isDebark, String thumbnail, boolean overnight,
                    ItineraryModel itinerary) {
        this.day = day;
        this.thumbnail = thumbnail;
        this.name = itinerary.getPort().getTitle();
        this.landPrograms = itinerary.getLandPrograms().stream().map(ItineraryLandProgramModel::getLandProgram)
                .collect(toList());
        this.hotels = itinerary.getHotels().stream().map(ItineraryHotelModel::getHotel).collect(toList());
        this.countryIso3 = itinerary.getPort().getCountryIso3();
        this.excursions = retrieveExcursions(isEmbark, isDebark, itinerary);
        this.hasExcursions = excursions != null && !excursions.isEmpty();
        if (hasExcursions) {
            excursions.sort(Comparator.comparing(ex -> ex.getTitle().trim()));
        }
        this.date = itinerary.getDepartDate();
        this.arriveTime = itinerary.getArriveTime();
        this.departTime = itinerary.getDepartTime();
        this.overnight = overnight;
        this.excursionDescription = itinerary.getPort().getDescription();
        this.itineraryId = itinerary.getItineraryId();
        this.prePosts = concat(
                hotels.stream().map(hotel -> new CruisePrePost(itinerary.getItineraryId(), itinerary.getPort().getThumbnail(), hotel)),
                landPrograms.stream().map(land -> new CruisePrePost(itinerary.getItineraryId(), itinerary.getPort().getThumbnail(), land)))
                .collect(toList());
    }

    public List<CruisePrePost> getPrePosts() {
        return prePosts;
    }

    private List<ExcursionModel> retrieveExcursions(boolean isEmbark, boolean isDebark, ItineraryModel itinerary) {
        if (itinerary.getHasDedicatedShorex()) {
            return ofNullable(itinerary.getExcursions())
                    .map(Collection::stream).orElseGet(Stream::empty)
                    .map(ItineraryExcursionModel::getExcursion)
                    .collect(toList());
        } else {
            return ofNullable(itinerary.getPort().getExcursions())
                    .map(Collection::stream).orElseGet(Stream::empty)
                    .filter(ex -> !isEmbark || ex.isOkForEmbark())
                    .filter(ex -> !isDebark || ex.isOkForDebarks())
                    .collect(toList());
        }
    }


    public int getDay() {
        return day;
    }

    public int getItineraryId() {
        return itineraryId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public boolean isHasExcursions() {
        return hasExcursions;
    }

    public String getName() {
        return name;
    }

    public String getCountryIso3() {
        return countryIso3;
    }

    public String getExcursionDescription() {
        return excursionDescription;
    }

    public List<?> getExcursions() {
        return excursions;
    }

    public Calendar getDate() {
        return date;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public boolean isOvernight() {
        return overnight;
    }

    public String getDepartTime() {
        return departTime;
    }

    public List<LandProgramModel> getLandPrograms() {
        return landPrograms;
    }

    public List<HotelModel> getHotels() {
        return hotels;
    }
}
