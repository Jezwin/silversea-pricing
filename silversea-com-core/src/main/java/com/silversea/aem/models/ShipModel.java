package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

/**
 * Created by mbennabi on 17/02/2017.
 */
@Model(adaptables = Page.class)
public class ShipModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipModel.class);

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
    @Named(JcrConstants.JCR_CONTENT + "/shipCode")
    @Optional
    private String shipCode;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipId")
    @Optional
    private String shipId;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/heroBanner")
    @Optional
    private String heroBanner;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetGallerySelectionReference")
    @Optional
    private String assetGallerySelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/deckPlan")
    @Optional
    private String deckPlan;

    @PostConstruct
    private void init() {
    }

    public String getTitle() {
        return title;
    }

    public String getLongDescription () {
        return longDescription;
    }

    public String getShipCode() {
        return shipCode;
    }

    public String getShipId() {
        return shipId;
    }

    public String getHeroBanner() { return heroBanner; }

    public String getAssetGallerySelectionReference() { return assetGallerySelectionReference; }

    public String getDeckPlan() { return deckPlan; }


}
