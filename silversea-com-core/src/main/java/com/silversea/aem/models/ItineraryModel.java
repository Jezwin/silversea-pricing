package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Model(adaptables = Resource.class)
public class ItineraryModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ItineraryModel.class);

    @Self
    private Resource resource;

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private Integer itineraryId;

    @Inject @Optional
    private Calendar date;

    @Inject @Optional
    private String arriveTime;

    @Inject @Optional
    private String arriveAmPm;

    @Inject @Optional
    private String departTime;

    @Inject @Optional
    private String departAmPm;

    @Inject @Optional
    private boolean overnight;

    @Inject @Optional
    private String portReference;

    @Inject @Optional
    private List<ItineraryExcursionModel> excursions = new ArrayList<>();

    @Inject @Optional
    private List<ItineraryHotelModel> hotels = new ArrayList<>();

    @Inject @Optional
    private List<ItineraryLandProgramModel> landPrograms = new ArrayList<>();

    private Integer cruiseId;

    private PortModel port;

    @PostConstruct
    private void init() {
        final Resource itinerariesContentResource = resource.getParent();

        if (itinerariesContentResource != null) {
            final Resource cruiseContentResource = itinerariesContentResource.getParent();

            if (cruiseContentResource != null) {
                cruiseId = cruiseContentResource.getValueMap().get("cruiseId", Integer.class);
            }
        }

        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null && StringUtils.isNotEmpty(portReference)) {
            final Page portPage = pageManager.getPage(portReference);

            if (portPage != null) {
                port = portPage.adaptTo(PortModel.class);
            }
        }
    }

    public Resource getResource() {
        return resource;
    }

    public Integer getItineraryId() {
        return itineraryId;
    }

    public Calendar getDate() {
        return date;
    }

    public String getArriveTime() {
        if (arriveTime.length() == 4) {
            return arriveTime.substring(0, 2) + ":" + arriveTime.substring(2, arriveTime.length());
        }

        return arriveTime;
    }

    public String getDepartTime() {
        if (departTime.length() == 4) {
            return departTime.substring(0, 2) + ":" + departTime.substring(2, departTime.length());
        }

        return departTime;
    }

    public Integer getCruiseId() {
        return cruiseId;
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

    public List<ItineraryExcursionModel> getExcursions() {
        return excursions;
    }

    public List<ItineraryHotelModel> getHotels() {
        return hotels;
    }

    public List<ItineraryLandProgramModel> getLandPrograms() {
        return landPrograms;
    }

    /**
     * Check if the itinerary correspond to an itinerary with <code>cruiseId</code>,
     * <code>cityId</code>, <code>date</code>
     *
     * @param cruiseId id of voyage
     * @param date date
     * @return true if all arguments match with the current itinerary, false else
     */
    public boolean isItinerary(final Integer cruiseId, final Calendar date) {
        if (this.cruiseId == null || this.port == null || this.date == null) {
            return false;
        }

        return this.cruiseId.equals(cruiseId) && date.getTime().equals(this.date);
    }

    // -------------- TODO review after this line ----------------- //
    @Deprecated
    public String getTitle() {
        return null;
    }

    @Deprecated
    public void init(Node node) {
    }

    @Deprecated
    public Page getPage() {
        return null;
    }
}
