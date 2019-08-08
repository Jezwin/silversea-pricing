package com.silversea.aem.models;

import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

@Model(adaptables = Page.class)
public class VoyageJournalDayModel {
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
    @Named(JcrConstants.JCR_CONTENT + "/image/fileReference")
    @Optional
    private String thumbnail;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/dayNumber")
    @Optional
    private String dayNumber;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/publicationDate")
    @Optional
    private Calendar publicationDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/coordinates")
    @Optional
    private String coordinates;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/temperature")
    @Optional
    private String temperature;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/pressure")
    @Optional
    private String pressure;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/weather")
    @Optional
    private String weather;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/wind")
    @Optional
    private String wind;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/heroAssetSelectionReference")
    @Optional
    private String heroAssetSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/author")
    @Optional
    private String author;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    @PostConstruct
    private void init() {

    }

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @return the longDescription
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * @return the dayNumber
     */
    public String getDayNumber() {
        return dayNumber;
    }

    /**
     * @return the publicationDate
     */
    public Calendar getPublicationDate() {
        return publicationDate;
    }

    /**
     * @return the coordinates
     */
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * @return the temperature
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     * @return the pressure
     */
    public String getPressure() {
        return pressure;
    }

    /**
     * @return the weather
     */
    public String getWeather() {
        return weather;
    }

    /**
     * @return the wind
     */
    public String getWind() {
        return wind;
    }

    /**
     * @return the heroAssetSelectionReference
     */
    public String getHeroAssetSelectionReference() {
        return heroAssetSelectionReference;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the assetSelectionReference
     */
    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }
    

    /**
     * @return the path
     */
    public String getPath() {
        return page.getPath();
    }
}