package com.silversea.aem.components.beans;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.silversea.aem.components.beans.CruisePrePost.PREPOSTMID;
import com.silversea.aem.components.page.Cruise2018Use;
import com.silversea.aem.models.*;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.*;
import java.util.stream.Stream;

import static com.silversea.aem.utils.AssetUtils.buildAssetList;
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

    private final String excursionsLabel;
    private final List<CruisePrePost> prePosts;
    private final List<LandProgramModel> mid;
    private final List<LandProgramModel> landPrograms;
    private final int shorexSize;

    private final List<HotelModel> hotels;
    private final boolean hasExcursions;
    private final Calendar date;
    private final String arriveTime;
    private final String departTime;
    private final String excursionDescription;
    private final boolean overnight;
    private final String cruiseType;

    public CruiseItinerary(int day, boolean isEmbark, boolean isDebark, String thumbnail, boolean overnight,
                           ItineraryModel itinerary, String cruiseType, String cruisePath, ResourceResolver resolver) {
        this.day = day;
        this.thumbnail = thumbnail;
        this.name = itinerary.getPort().getTitle();
        this.landPrograms = itinerary.getLandPrograms().stream().map(ItineraryLandProgramModel::getLandProgram)
                .collect(toList());
        this.hotels = itinerary.getHotels().stream().map(ItineraryHotelModel::getHotel).collect(toList());
        this.countryIso3 = itinerary.getPort().getCountryIso3();
        this.excursions = retrieveExcursions(isEmbark, isDebark, itinerary);
        this.hasExcursions =
                excursions != null && !excursions.isEmpty() && !cruiseType.equalsIgnoreCase("silversea-expedition");
        if (hasExcursions) {
            excursions.sort(Comparator.comparing(ex -> ex.getTitle().trim()));
        }
        this.mid = landPrograms.stream()
                .filter(landProgram -> PREPOSTMID.MID.equals(PREPOSTMID.retrieve(landProgram.getLandCode())))
                .collect(toList());
        excursionsLabel = retrieveMap(this.mid, this.excursions).toString();
        this.date = itinerary.getDate();
        this.arriveTime = itinerary.getArriveTime();
        this.departTime = itinerary.getDepartTime();
        this.overnight = overnight;
        this.excursionDescription = itinerary.getPort().getDescription();
        this.itineraryId = itinerary.getItineraryId();
        this.prePosts = concat(
                hotels.stream().map(hotel -> new CruisePrePost(itinerary.getItineraryId(),
                        ofNullable(buildAssetList(CruiseUtils.firstNonNull(hotel.getAssetSelectionReference(),
                                hotel.getAssetSelectionReferenceApi()), resolver))
                                .map(list -> list.isEmpty() ? "" : list.get(0).getPath()).orElse(""),
                        itinerary.getPort().getThumbnail(), hotel, cruisePath)),
                landPrograms.stream().map(land -> new CruisePrePost(itinerary.getItineraryId(),
                        ofNullable(buildAssetList(CruiseUtils.firstNonNull(land.getAssetSelectionReference(),
                                land.getAssetSelectionReferenceApi()), resolver))
                                .map(list -> list.isEmpty() ? "" : list.get(0).getPath()).orElse(""),
                        itinerary.getPort().getThumbnail(), land, cruisePath)))
                .filter(prepost -> !"MID".equals(prepost.getPrePost()))
                .collect(toList());
        this.shorexSize = mid.size() + (excursions != null ? excursions.size() : 0);
        this.cruiseType = cruiseType;
    }

    private JsonObject retrieveMap(List<LandProgramModel> mid, List<ExcursionModel> excursions) {
        JsonArray array = new JsonArray();
        for (LandProgramModel landProgram : mid) {
            JsonObject element = new JsonObject();
            element.addProperty("title", landProgram.getTitle());
            element.addProperty("id", landProgram.getLandId());
            element.addProperty("kind", Cruise2018Use.Lightbox.MIDLAND.getSelector());
            array.add(element);
        }
        if (excursions != null) {
            for (ExcursionModel excursion : excursions) {
                JsonObject element = new JsonObject();
                element.addProperty("title", excursion.getTitle());
                element.addProperty("id", excursion.getShorexId());
                element.addProperty("kind", Cruise2018Use.Lightbox.SHOREX_EXCURSION.getSelector());
                array.add(element);
            }
        }
        JsonObject map = new JsonObject();
        int size = array.size();
        for (int i = 0; i < array.size(); i++) {
            JsonObject prev = array.get(Math.floorMod(i - 1, size)).getAsJsonObject();
            JsonObject next = array.get((i + 1) % size).getAsJsonObject();
            JsonObject labels = new JsonObject();
            labels.addProperty("prevLabel", prev.get("title").getAsString());
            labels.addProperty("nextLabel", next.get("title").getAsString());
            labels.addProperty("prevId", prev.get("id").getAsString());
            labels.addProperty("nextId", next.get("id").getAsString());
            labels.addProperty("prevKind", prev.get("kind").getAsString());
            labels.addProperty("nextKind", next.get("kind").getAsString());
            map.add(array.get(i).getAsJsonObject().get("id").getAsString() + "", labels);
        }
        return map;
    }

    public static List<ExcursionModel> retrieveExcursions(ItineraryModel itinerary) {
        return retrieveExcursions(false, false, itinerary);
    }


    public static List<ExcursionModel> retrieveExcursions(boolean isEmbark, boolean isDebark,
                                                          ItineraryModel itinerary) {
        if (itinerary.getHasDedicatedShorex()) {
            List<ExcursionModel> dedicated = ofNullable(itinerary.getExcursions())
                    .map(Collection::stream).orElseGet(Stream::empty)
                    .map(ItineraryExcursionModel::getExcursion)
                    .collect(toList());
            if (!dedicated.isEmpty()) {
                return dedicated;
            }
        }
        return ofNullable(itinerary.getPort().getExcursions())
                .map(Collection::stream).orElseGet(Stream::empty)
                .filter(ex -> !isEmbark || ex.isOkForEmbark())
                .filter(ex -> !isDebark || ex.isOkForDebarks())
                .filter(excursion -> !isSpecial(excursion))
                .collect(toList());
    }

    private static boolean isSpecial(ExcursionModel excursion) {
        return "Special Excursion".equals(excursion.getShorexCategory());
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

    public List<ExcursionModel> getExcursions() {
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

    public String getExcursionsLabel() {
        return excursionsLabel;
    }

    public List<LandProgramModel> getMid() {
        return mid;
    }

    public List<CruisePrePost> getPrePosts() {
        return prePosts;
    }

    public int getShorexSize() {
        return shorexSize;
    }

    public String getCruiseType() {
        return cruiseType;
    }
}
