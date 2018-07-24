package com.silversea.aem.models;

import java.util.List;

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

@Model(adaptables = Page.class)
public class ShipModel {

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
    @Named(JcrConstants.JCR_CONTENT + "/excerpt")
    @Optional
    private String excerpt;

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
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetGallerySelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/photoVideoSuiteSelectionReference")
    @Optional
    private String photoVideoSuiteSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/deckPlan")
    @Optional
    private String deckPlan;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/guestsCapacity")
    @Optional
    private String guestsCapacity;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/crewCapacity")
    @Optional
    private String crewCapacity;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/builtDate")
    @Optional
    private String builtDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/refurbDate")
    @Optional
    private String refurbDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/tonnage")
    @Optional
    private String tonnage;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/lengthFt")
    @Optional
    private String lengthFt;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/lengthM")
    @Optional
    private String lengthM;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/width")
    @Optional
    private String width;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/speed")
    @Optional
    private String speed;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/registry")
    @Optional
    private String registry;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/image/fileReference")
    @Optional
    private String thumbnail;

    @Inject
    @Named("dinings")
    private List<DiningModel> dinings;

    @Inject
    @Named("public-areas")
    private List<PublicAreaModel> publicAreas;

    @Inject
    @Named("suites")
    private List<SuiteModel> suites;

    private String path;

    private String name;

    @PostConstruct
    private void init() {
        path = page.getPath();
        name = page.getName();
    }

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

    public String getPhotoVideoSuiteSelectionReference() {
        return photoVideoSuiteSelectionReference;
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

    public String getGuestsCapacity() {
        return guestsCapacity;
    }

    public String getCrewCapacity() {
        return crewCapacity;
    }

    public String getBuiltDate() {
        return builtDate;
    }

    public String getRefurbDate() {
        return refurbDate;
    }

    public String getTonnage() {
        return tonnage;
    }

    public String getLengthFt() {
        return lengthFt;
    }

    public String getLengthM() {
        return lengthM;
    }

    public String getWidth() {
        return width;
    }

    public String getSpeed() {
        return speed;
    }

    public String getRegistry() {
        return registry;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPath() {
        return path;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ShipModel)) {
            return false;
        }

        final ShipModel objShipModel = (ShipModel) obj;

        return objShipModel.getPath().equals(getPath());
    }
}
