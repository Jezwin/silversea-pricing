package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Page.class)
public class SuiteModel implements ShipAreaModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(SuiteModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE) @Optional
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String assetSelectionReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/bedroomsInformation") @Optional
    private String bedroomsInformation;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/plan") @Optional
    private String plan;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/locationImage") @Optional
    private String locationImage;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/virtualTour") @Optional
    private String virtualTour;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/image/fileReference") @Optional
    private String thumbnail;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteFeature") @Optional
    private String[] features;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteReference") @Optional
    private String suiteReference;

    // TODO replace by injector
    private SuiteModel genericSuite;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteSubTitle") @Optional
    private String suiteSubTitle;

    private String name;

    @PostConstruct
    private void init() {
        final PageManager pageManager = page.getPageManager();

        // init reference
        if (suiteReference != null) {
            final Page genericSuitePage = pageManager.getPage(suiteReference);

            if (genericSuitePage != null) {
                genericSuite = genericSuitePage.adaptTo(SuiteModel.class);
            }
        }

        name = page.getName();
    }

    public String getTitle() {
        return title;
    }

    public String getLongDescription() {
        return longDescription != null ? longDescription :
                (genericSuite != null ? genericSuite.getLongDescription() : null);
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference != null ? assetSelectionReference :
                (genericSuite != null ? genericSuite.getAssetSelectionReference() : null);
    }

    public String getBedroomsInformation() {
        return bedroomsInformation;
    }

    public String getPlan() {
        return plan;
    }

    public String[] getFeatures() {
        return features;
    }

    public String getLocationImage() {
        return locationImage;
    }

    public String getVirtualTour() {
        return virtualTour;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Page getPage() {
        return page;
    }

    public String getName() {
        return name;
    }
}
