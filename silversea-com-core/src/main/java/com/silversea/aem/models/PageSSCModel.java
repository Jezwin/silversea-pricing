package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class PageSSCModel {
    public Page page;
    public String navigationTitle;

    public PageSSCModel (Page page){
        this.page = page;
        navigationTitle = page.getNavigationTitle();
        if (page.getProperties().get("breadcrumbTitle") != null){
            navigationTitle = page.getProperties().get("breadcrumbTitle", String.class);
        }
    }

    public String getNavigationTitle(){
        return navigationTitle;
    }

    public Page getPage(){
        return page;
    }



}
