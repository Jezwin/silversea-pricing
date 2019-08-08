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

/**
 * Created by aurelienolivier on 12/02/2017.
 */
@Model(adaptables = Page.class)
public class HotelModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(HotelModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE) @Optional
    private String name;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION) @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/code") @Optional
    private String code;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/hotelId") @Optional
    private Long hotelId;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String  assetSelectionReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference_api") @Optional
    private String  assetSelectionReferenceApi;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/category") @Optional
    private String category;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/customTitle") @Optional
    private String customTitle;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/nights") @Optional
    private Integer nights;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/note")
    @Optional
    private String note;

	private String shortDescription;

    @PostConstruct
    private void init() {
        shortDescription = (description != null && description.length() > 200) ? description.substring(0, 200) : description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getCode() {
        return code;
    }

    public Page getPage() {
        return page;
    }

    public String getCategory() {
        return category;
    }

    public Integer getNights() {
        return nights;
    }
    public Long getHotelId() {
        return hotelId;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public String getAssetSelectionReferenceApi() {
        return assetSelectionReferenceApi;
    }

    public String getNote() {
        return note;
    }
}
