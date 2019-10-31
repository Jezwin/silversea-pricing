package com.silversea.aem.models;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.CruiseItinerary;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Model(adaptables = Resource.class)
public class ItineraryModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ItineraryModel.class);

    private static final String PV_AM = "AM";
    private static final String PV_PM = "PM";
    private static final String DEFAULT_TIME_MORNING = "9:00";
    private static final String DEFAULT_TIME_AFTERNOON = "12:00";

    @Self
    private Resource resource;

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private Integer itineraryId;

    @Inject
    @Optional
    private Date date; // TODO change by arriveDate

    private Calendar departDate; // used only when itineraries are compacted
    private Calendar departDateInit;

    @Inject
    @Optional
    private String arriveTime;

    @Inject
    @Optional
    private String arriveAmPm;

    @Inject
    @Optional
    private String departTime;

    @Inject
    @Optional
    private String departAmPm;

    @Inject
    @Optional
    private boolean overnight;

    @Inject
    @Optional
    private String portReference;

    @Inject
    @Optional
    private List<ItineraryExcursionModel> excursions = new ArrayList<>();

    private List<ItineraryExcursionModel> compactedExcursions = null;

    @Inject
    @Optional
    private List<ItineraryHotelModel> hotels = new ArrayList<>();

    @Inject
    @Named("land-programs")
    @Optional
    private List<ItineraryLandProgramModel> landPrograms = new ArrayList<>();

    private Integer cruiseId;

    private String cruiseType;

    private PortModel port;

    private Boolean hasDedicatedShorex;

    private Integer numberDays = 0;

    @PostConstruct
    private void init() {
        final Resource itinerariesContentResource = resource.getParent();
        hasDedicatedShorex = false;

        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        if (itinerariesContentResource != null) {
            final Resource cruiseContentResource = itinerariesContentResource.getParent();

            if (cruiseContentResource != null) {
                cruiseType = CruiseModel.cruiseType(tagManager, cruiseContentResource);
                cruiseId = cruiseContentResource.getValueMap().get("cruiseId", Integer.class);
                departDateInit = cruiseContentResource.getValueMap().get("startDate", Calendar.class);

            }
        }

        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null && StringUtils.isNotEmpty(portReference)) {
            final Page portPage = pageManager.getPage(portReference);

            if (portPage != null) {
                port = portPage.adaptTo(PortModel.class);
            }
        }
        List<ItineraryExcursionModel> excursionToShow = new ArrayList<>();


        boolean isExpedition = "silversea-expedition".equals(getCruiseType());
        if (!isExpedition) {
            for (ItineraryExcursionModel excursion : this.excursions) {
                ExcursionModel otherExcursion = excursion.getExcursion();
                if (otherExcursion != null && StringUtils.isNotEmpty(otherExcursion.getCodeExcursion())) {
                    if (excursionToShow.stream().noneMatch(
                            dto -> dto.getExcursion().getCodeExcursion().equals(otherExcursion.getCodeExcursion()))) {
                        excursionToShow.add(excursion);
                    }
                }
            }
        }
        hasDedicatedShorex =
                !isExpedition &&  Days.daysBetween(Instant.now(), new DateTime(departDateInit)).getDays() < 180;

        excursionToShow.sort(Comparator.comparing(ItineraryExcursionModel::getTitle));
        this.excursions = excursionToShow;

        List<ItineraryLandProgramModel> landProgramsToShow = new ArrayList<>();
        for (ItineraryLandProgramModel landProgram : this.landPrograms) {
            if (landProgram.getLandProgram() != null &&
                    StringUtils.isNotEmpty(landProgram.getLandProgram().getLandCode())) {
                landProgramsToShow.add(landProgram);
            }
        }
        landProgramsToShow.sort(Comparator.comparing(ItineraryLandProgramModel::getTitle));
        this.landPrograms = landProgramsToShow;

        List<ItineraryHotelModel> hotelsToShow = new ArrayList<>();
        for (ItineraryHotelModel hotel : this.hotels) {
            if (hotel.getHotel() != null && StringUtils.isNotEmpty(hotel.getHotel().getCode())) {
                hotelsToShow.add(hotel);
            }
        }
        this.hotels = hotelsToShow;
    }

    public Resource getResource() {
        return resource;
    }

    public Integer getItineraryId() {
        return itineraryId;
    }

    public Calendar getDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }

    public boolean isOvernight() {
        return overnight;
    }

    public Date getRawDate() {
        return date;
    }

    public Calendar getDepartDate() {
        return departDate;
    }

    public void setDepartDate(final Calendar departDate) {
        this.departDate = departDate;
    }

    public String getArriveTime() {
        if (arriveTime == null && arriveAmPm != null) {
            if (arriveAmPm.equals(PV_AM)) {
                return DEFAULT_TIME_MORNING;
            }

            if (arriveAmPm.equals(PV_PM)) {
                return DEFAULT_TIME_AFTERNOON;
            }
        }

        if (arriveTime.length() == 4) {
            return arriveTime.substring(0, 2) + ":" + arriveTime.substring(2, arriveTime.length());
        }

        return arriveTime;
    }

    public String getDepartTime() {
        if (departTime == null && departAmPm != null) {
            if (departAmPm.equals(PV_AM)) {
                return DEFAULT_TIME_MORNING;
            }

            if (departAmPm.equals(PV_PM)) {
                return "12:00";
            }
        }

        if (departTime.length() == 4) {
            return departTime.substring(0, 2) + ":" + departTime.substring(2, departTime.length());
        }

        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public Integer getCruiseId() {
        return cruiseId;
    }

    public Boolean getHasDedicatedShorex() {
        return hasDedicatedShorex;
    }

    public PortModel getPort() {
        return port;
    }

    public Integer getPortId() {
        if (port != null) {
            return port.getCityId();
        }

        return null;
    }

    /**
     * @return get excursions for this itinerary
     */
    public List<ItineraryExcursionModel> getExcursions() {
        return excursions;
    }

    /**
     * @return list of unique excursion for this itinerary, with general departure date removed
     */
    public List<ItineraryExcursionModel> getCompactedExcursions() {
        if (compactedExcursions != null) {
            return compactedExcursions;
        }

        compactedExcursions = new ArrayList<>();
        for (ItineraryExcursionModel excursion : excursions) {
            if (excursion.getExcursion() != null &&
                    StringUtils.isNotEmpty(excursion.getExcursion().getCodeExcursion())) {
                boolean found = false;

                for (ItineraryExcursionModel excursionForCompactedList : compactedExcursions) {
                    if (excursionForCompactedList.getCodeExcursion() != null
                            && excursion.getCodeExcursion() != null
                            && excursionForCompactedList.getCodeExcursion().equals(excursion.getCodeExcursion())) {
                        found = true;
                    }
                }

                if (!found) {
                    // trick to deep clone the itinerary item
                    // without implementing java clone method
                    final ItineraryExcursionModel excursionCopy =
                            excursion.getResource().adaptTo(ItineraryExcursionModel.class);

                    if (excursionCopy != null) {
                        excursionCopy.setGeneralDepartureTime(null);
                        compactedExcursions.add(excursionCopy);
                    }
                }
            }

        }

        return compactedExcursions;
    }

    public void addExcursions(List<ItineraryExcursionModel> excursions) {
        this.excursions.addAll(excursions);
    }

    public List<ItineraryHotelModel> getHotels() {
        return hotels;
    }

    public void addHotels(List<ItineraryHotelModel> hotels) {
        this.hotels.addAll(hotels);
    }

    public List<ItineraryLandProgramModel> getLandPrograms() {
        return landPrograms;
    }

    public Integer getNumberDays() {
        return numberDays;
    }

    public void setNumberDays(Integer numberDays) {
        this.numberDays = numberDays;
    }

    public String getCruiseType() {
        return cruiseType;
    }

    public void addLandPrograms(List<ItineraryLandProgramModel> landPrograms) {
        this.landPrograms.addAll(landPrograms);
    }

    /**
     * Check if the itinerary correspond to an itinerary with <code>cruiseId</code>,
     * <code>cityId</code>, <code>date</code>
     *
     * @param cruiseId id of voyage
     * @param date     date
     * @return true if all arguments match with the current itinerary, false else
     */
    public boolean isItinerary(final Integer cruiseId, final Calendar date, final Integer cityId) {
        if (this.cruiseId == null || this.port == null || this.date == null) {
            return false;
        }

        return this.cruiseId.equals(cruiseId) && date.getTime().equals(this.date) &&
                this.port.getCityId().equals(cityId);
    }

    /**
     * Fallback for excursions
     *
     * @param cruiseId
     * @param date
     * @return
     */
    public boolean isItineraryBasedOnDayOnly(final Integer cruiseId, final Calendar date) {
        if (this.cruiseId == null || this.port == null || this.date == null) {
            return false;
        }

        return this.cruiseId.equals(cruiseId) && date.getTime().equals(this.date);
    }
}