package com.silversea.aem.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * TODO remove unused/local variables
 */
@Model(adaptables = Page.class)
public class DiningModel extends AbstractModel{

    static final private Logger LOGGER = LoggerFactory.getLogger(DiningModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    @Optional
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;
    private String thumbnail;
    
    private ResourceResolver resourceResolver;
    private PageManager pageManager;

    @PostConstruct
    private void init() {
        try{
            resourceResolver = page.getContentResource().getResourceResolver();
            pageManager = resourceResolver.adaptTo(PageManager.class);
            title = initPropertyWithFallBack(page,"diningReference", title, "title",pageManager);
            longDescription = initPropertyWithFallBack(page,"diningReference", longDescription, "longDescription",pageManager);
            assetSelectionReference = initPropertyWithFallBack(page,"diningReference", assetSelectionReference, "assetSelectionReference",pageManager);
            thumbnail = page.getProperties().get("image/fileReference", String.class);
        }catch(RuntimeException e){
            LOGGER.error("Error while initializing model {}",e);
        }
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getTitle() {
        return title;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    public Page getPage() {
        return page;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
