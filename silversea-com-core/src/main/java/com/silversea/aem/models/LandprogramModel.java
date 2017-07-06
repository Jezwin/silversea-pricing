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
public class LandprogramModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(LandprogramModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE) @Optional
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION) @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/landId") @Optional
    private String landId;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/landCode") @Optional
    private String landCode;

    private String shortDescription;
    
    @PostConstruct
    private void init() {
    	shortDescription = description != null ? description.substring(0, 200) : null;
        LOGGER.debug("{}", page.getPath());
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    
    public String getShortDescription() {
        return shortDescription;
    }

    public String getLandId() {
        return landId;
    }

    public String getLandCode() {
        return landCode;
    }
}
