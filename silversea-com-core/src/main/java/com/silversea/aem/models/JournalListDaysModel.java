package com.silversea.aem.models;

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

/**
 * Created by mbennabi on 17/02/2017.
 */
@Model(adaptables = Page.class)
public class JournalListDaysModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(JournalListDaysModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/dayNumber")
    @Optional
    private String dayNumber;

    private String path;

    @PostConstruct
    private void init() {
        try{
            path = page.getPath();
        }catch(RuntimeException e){
            LOGGER.error("Error while initializing model {}",e);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public String getPath() {
        return (path != null && path.startsWith("/content") && !path.endsWith(".html") ? path + ".html" : path);
    }
}