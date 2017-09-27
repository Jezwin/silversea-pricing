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
public class VoyageJournalModel {
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
    @Named(JcrConstants.JCR_CONTENT + "/date")
    @Optional
    private Calendar date;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/duration")
    @Optional
    private String duration;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipReference")
    @Optional
    private String shipReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/destinationReference")
    @Optional
    private String destinationReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/cruiseReference")
    @Optional
    private String cruiseReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/relatedCruiseReference")
    @Optional
    private String relatedCruiseReferences;

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
     * @return the date
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @return the assetSelectionReference
     */
    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    /**
     * @return the shipReference
     */
    public String getShipReference() {
        return shipReference;
    }

    /**
     * @return the destinationReference
     */
    public String getDestinationReference() {
        return destinationReference;
    }

    /**
     * @return the cruiseReference
     */
    public String getCruiseReference() {
        return cruiseReference;
    }

    /**
     * @return the relatedCruiseReferences
     */
    public String getRelatedCruiseReferences() {
        return relatedCruiseReferences;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return page.getPath();
    }

    /**
     * @return the cruiseModel
     */
    public CruiseModel getCruiseModel() {
        Page cruisePage = page.getPageManager().getPage(cruiseReference);

        if (cruisePage != null) {
            return cruisePage.adaptTo(CruiseModel.class);
        }

        return null;
    }

    /**
     * @return the shipModel
     */
    public ShipModel getShipModel() {
        Page shipPage = page.getPageManager().getPage(shipReference);

        if (shipPage != null) {
            return shipPage.adaptTo(ShipModel.class);
        }

        return null;
    }
}