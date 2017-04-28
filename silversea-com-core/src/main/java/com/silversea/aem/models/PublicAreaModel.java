package com.silversea.aem.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;

@Model(adaptables = Page.class)
public class PublicAreaModel {
    
    @Inject
    @Self
    private Page page;
    
    private String description;
    
    @PostConstruct
    private void init() {
    }

    public Page getPage() {
        return page;
    }
}
