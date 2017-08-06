package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Page.class)
public class ShipModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/shipCode") @Optional
    private String shipCode;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/shipId") @Optional
    private String shipId;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/heroBanner") @Optional
    private String heroBanner;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String assetGallerySelectionReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/deckPlan") @Optional
    private String deckPlan;

    @Inject @Named("dinings")
    private List<DiningModel> dinings;

    @Inject @Named("public-areas")
    private List<PublicAreaModel> publicAreas;

    @Inject @Named("suites")
    private List<SuiteModel> suites;

    public Page getPage() {
        return page;
    }

    public String getTitle() {
        return title;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getShipCode() {
        return shipCode;
    }

    public String getShipId() {
        return shipId;
    }

    public String getHeroBanner() {
        return heroBanner;
    }

    public String getAssetGallerySelectionReference() {
        return assetGallerySelectionReference;
    }

    public String getDeckPlan() {
        return deckPlan;
    }

    public List<DiningModel> getDinings() {
        return dinings;
    }

    public List<PublicAreaModel> getPublicAreas() {
        return publicAreas;
    }

    public List<SuiteModel> getSuites() {
        return suites;
    }
}
