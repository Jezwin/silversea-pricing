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
public class ExcursionModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExcursionModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE) @Optional
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION) @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/codeExcursion")
    private String codeExcursion;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/apiLongDescription")
    private String apiLongDescription;

//    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription")
//    private String longDescription;
//
//    @Inject @Named(JcrConstants.JCR_CONTENT + "/pois")
//    private String pois;

    @PostConstruct
    private void init() {
        LOGGER.debug("{}", page.getPath());
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCodeExcursion() {
        return codeExcursion;
    }

    public String getApiLongDescription() {
        return apiLongDescription;
    }
//
//    public String getLongDescription() {
//        return longDescription;
//    }
//
//    public String getPois() {
//        return pois;
//    }
}
