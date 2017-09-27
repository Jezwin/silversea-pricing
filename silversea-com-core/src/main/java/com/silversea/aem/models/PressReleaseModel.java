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
public class PressReleaseModel {
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
    @Named(JcrConstants.JCR_CONTENT + "/publicationDate")
    @Optional
    private Calendar publicationDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

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
     * @return the publicationDate
     */
    public Calendar getPublicationDate() {
        return publicationDate;
    }

    /**
     * @return the longDescription
     */
    public String getLongDescription() {
        return longDescription;
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