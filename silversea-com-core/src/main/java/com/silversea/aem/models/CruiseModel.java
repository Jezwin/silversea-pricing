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
public class CruiseModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruiseModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/apititle")
    @Optional
    private String apititle;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/importeddescription")
    @Optional
    private String importeddescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/startdate")
    @Optional
    private String startdate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/enddate")
    @Optional
    private String enddate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/duration")
    @Optional
    private String duration;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipReference")
    @Optional
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
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/keypeople")
    @Optional
    private String[] keypeople;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/exclusiveoffers")
    @Optional
    private String[] exclusiveoffers;

    @PostConstruct
    private void init() {
    }

    public String getTitle() {
        return title;
    }

    public String getApititle() {
        return apititle;
    }

    public String getImporteddescription() {
        return importeddescription;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getEnddate() { return enddate; }

    public String getDuration() { return duration; }

    public String getShipReference() { return shipReference; }

    public String getCruiseCode() { return cruiseCode; }

    public String getItinerary() { return itinerary; }

    public String getAssetSelectionReference() { return assetSelectionReference; }

    public String[] getKeypeople() { return keypeople; }

    public String[] getExclusiveoffers() { return exclusiveoffers; }


}
