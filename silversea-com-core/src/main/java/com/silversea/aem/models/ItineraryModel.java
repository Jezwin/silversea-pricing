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
import java.util.Calendar;
import java.util.Date;
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
    private Date date;

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

    public Integer getItineraryId() {
        return itineraryId;
    }

    public Resource getResource() {
        return resource;
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

    /**
     * Check if the itinerary correspond to an itinerary with <code>cruiseId</code>,
     * <code>cityId</code>, <code>date</code>
     *
     * @param cruiseId id of voyage
     * @param cityId if of city
     * @param date date
     * @return true if all arguments match with the current itinerary, false else
     */
    public boolean isItinerary(final Integer cruiseId, final Integer cityId, final Calendar date) {
        if (this.cruiseId == null || this.port == null || this.date == null) {
            return false;
        }

        return this.cruiseId.equals(cruiseId) /*&& this.port.getCityId().equals(cityId)*/ && date.getTime().equals(this.date);
    }

    // -------------- TODO review after this line ----------------- //
    public List<HotelModel> getHotels() {
        return null;
    }

    public List<ExcursionModel> getExcursions() {
        return null;
    }

    public List<LandProgramModel> getLandPrograms() {
        return null;
    }

    public String getTitle() {
        return null;
    }

    public void init(Node node) {

    }

    public Page getPage() {
        return null;
    }
}
