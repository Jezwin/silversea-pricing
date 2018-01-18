package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Page.class)
public class TravelAgencyModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(TravelAgencyModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/agencyId")
    private String agencyId;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/address")
    @Optional
    private String address;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/city")
    @Optional
    private String city;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/zip")
    @Optional
    private String zip;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/zip4")
    @Optional
    private String zip4;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/countryIso3")
    @Optional
    private String country;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/stateCode")
    @Optional
    private String stateCode;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/county")
    @Optional
    private String county;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/phone")
    @Optional
    private String phone;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/latitude")
    @Optional
    private Double latitude;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longitude")
    @Optional
    private Double longitude;

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

    public String getPath() {
        return page.getPath();
    }

    public String getAgencyId() {
        return agencyId;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getZip4() {
        return zip4;
    }

    public String getCountry() {
        return country;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getCounty() {
        return county;
    }

    public String getPhone() {
        return phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}