package com.silversea.aem.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.GeoLocation;

@Model(adaptables = Page.class)
public class SegmentModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(SegmentModel.class);

    @Inject
    @Self
    private Page page;
    private String focusedMapReference;
    private String mapReference;
    private String subTitle;
    private CruiseModel cruise;
    private ResourceResolver resourceResolver;
    private PageManager pageManager;

    @PostConstruct
    private void init() {
        resourceResolver = page.getContentResource().getResourceResolver();
        pageManager = resourceResolver.adaptTo(PageManager.class);
        focusedMapReference = page.getProperties().get("focusedMapReference",String.class);
        mapReference = page.getProperties().get("mapReference",String.class);
        subTitle = page.getProperties().get("subTitle",String.class);
    }

    public Page getPage() {
        return page;
    }

    public CruiseModel getCruise() {
        return cruise;
    }

    public String getFocusedMapReference() {
        return focusedMapReference;
    }

    public String getMapReference() {
        return mapReference;
    }

    public String getSubTitle() {
        return subTitle;
    }
    
}
