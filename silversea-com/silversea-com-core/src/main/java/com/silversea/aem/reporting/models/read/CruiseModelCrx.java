package com.silversea.aem.reporting.models.read;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.*;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Model(adaptables = Page.class)
public class CruiseModelCrx {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruiseModelCrx.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION)
    @Optional
    private String description;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/apiTitle")
    @Optional
    private String apiTitle;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/importedDescription")
    @Optional
    private String importedDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/startDate")
    @Optional
    private Calendar startDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/endDate")
    @Optional
    private Calendar endDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/duration")
    @Optional
    private String duration;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customDestination")
    @Optional
    private String customDestination;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipReference")
    private String shipReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/cruiseCode")
    @Optional
    private String cruiseCode;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/itinerary")
    @Optional
    private String itinerary;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/bigItineraryMap")
    @Optional
    private String bigItineraryMap;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/bigThumbnailItineraryMap")
    @Optional
    private String bigThumbnailItineraryMap;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/smallItineraryMap")
    @Optional
    private String smallItineraryMap;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/itineraries")
    @Optional
    private List<ItineraryModel> itineraries;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/bigThumbnail")
    @Optional
    private String bigThumbnail;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/offer")
    @Optional
    private String[] exclusiveOffersReferences;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/isVisible")
    @Default(booleanValues = true)
    private boolean isVisible;

    @PostConstruct
    private void init() {
        if (itineraries == null) {
            itineraries = new ArrayList<>();
        }

        if (exclusiveOffersReferences != null) {
            exclusiveOffersReferences = new String[0];
        }
    }


    /**
     * @return cruise api title
     */
    public String getApiTitle() {
        return apiTitle;
    }

    /**
     * @return cruise description
     */
    public String getDescription() {
        return description != null ? description : importedDescription;
    }

    /**
     * @return cruise imported description
     */
    public String getImportedDescription() {
        return importedDescription;
    }

    /**
     * @return start date of the cruise
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * @return end date of  the cruise
     */
    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * @return duration of the cruise (in days)
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @return the cruise code
     */
    public String getCruiseCode() {
        return cruiseCode;
    }

    /**
     * @return DAM path of the cruise itinerary image
     */
    public String getItinerary() {
        return itinerary;
    }

    /**
     * @return list of itinerary items
     */
    public List<ItineraryModel> getItineraries() {
        return itineraries;
    }

    /**
     * @return cruise page
     */
    public Page getPage() {
        return page;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public String getBigThumbnail() {
        return bigThumbnail;
    }

    public String getBigItineraryMap() {
        return bigItineraryMap;
    }

    public String getBigThumbnailItineraryMap() {
        return bigThumbnailItineraryMap;
    }

    public String getSmallItineraryMap() {
        return smallItineraryMap;
    }
}