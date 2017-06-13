package com.silversea.aem.models;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

/**
 * Created by mbennabi on 17/02/2017.
 */
@Model(adaptables = Page.class)
public class VoyageJournalDayModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(VoyageJournalDayModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

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
    private Date publicationDate;

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

//    private List<JournalListDaysModel> listDays;

    @PostConstruct
    private void init() {
//        listDays = new ArrayList<>();
//        final Iterator<Page> childs = page.getParent().listChildren();
//        while (childs.hasNext()) {
//            listDays.add(childs.next().adaptTo(JournalListDaysModel.class));
//        }
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getTitle() {
        return title;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public String getPublicationDate() {
        return publicationDate.toString();
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public String getWeather() {
        return weather;
    }

    public String getWind() {
        return wind;
    }

    public String getHeroAssetSelectionReference() {
        return heroAssetSelectionReference;
    }

    public String getAuthor() {
        return author;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

//    public List<JournalListDaysModel> getListDays() {
//        return listDays;
//    }

}
