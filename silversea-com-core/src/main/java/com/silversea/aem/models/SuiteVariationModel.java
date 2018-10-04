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

@Model(adaptables = Page.class)
public class SuiteVariationModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(SuiteVariationModel.class);

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

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteFeature") @Optional
    private String[] features;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteReference") @Optional
    private String suiteReference;
    
    @Inject @Named(JcrConstants.JCR_CONTENT + "/shipId") @Optional
    private String shipId;

	private SuiteModel suite;

    @PostConstruct
    private void init() {

    }

    public String getTitle() {
        return title;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    public String getBedroomsInformation() {
        return bedroomsInformation;
    }

    public String getPlan() {
        return plan;
    }

    public String getLocationImage() {
        return locationImage;
    }

    public String getVirtualTour() {
        return virtualTour;
    }

    public String[] getFeatures() {
        return features;
    }

	public String getSuiteReference() {
		return suiteReference;
	}
	
   public String getShipId() {
		return shipId;
   }
}
