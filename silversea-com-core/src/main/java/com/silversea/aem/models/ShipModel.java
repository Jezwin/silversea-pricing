package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
 * Created by mbennabi on 17/02/2017.
 */
@Model(adaptables = Page.class)
public class ShipModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipModel.class);

    @Inject
    @Self
    private Page page;
    private ResourceResolver resourceResolver;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipCode")
    @Optional
    private String shipCode;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipId")
    @Optional
    private String shipId;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/heroBanner")
    @Optional
    private String heroBanner;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetGallerySelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/deckPlan")
    @Optional
    private String deckPlan;

    private List<DiningModel> dinings;

    private List<PublicAreaModel> publicAreas;

    private List<SuiteModel> suites;

    private PageManager pageManager; 

    @PostConstruct
    private void init() {
        try{
            resourceResolver = page.getContentResource().getResourceResolver();
            pageManager = resourceResolver.adaptTo(PageManager.class);
            dinings = initModels(DiningModel.class,"dining");
            publicAreas = initModels(PublicAreaModel.class,"public-areas");
            suites = initModels(SuiteModel.class,"suites");
        }catch(RuntimeException e){
            LOGGER.error("Error while initializing model {}",e);
        }
    }

    public String getTitle() {
        return title;
    }
    private <T> List<T> initModels(Class<T> modelClass, String root){
        List<T> list  = new ArrayList<T>();
        Iterator<Page> pages = getPages(page.getPath() + "/" + root);
        if(pages != null && pages.hasNext()){
            pages.forEachRemaining(item -> {
                list.add(item.adaptTo(modelClass));
            });  
        }

        return list;
    }
    private Iterator<Page> getPages(String root) {
        Iterator<Page> pages = null;
        Page page = pageManager.getPage(root);
        if(page != null){
            pages = page.listChildren();
        }
        else{
            LOGGER.warn("Page reference {} not found",root);
        }
        return pages;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getShipCode() {
        return shipCode;
    }

    public String getShipId() {
        return shipId;
    }

    public String getHeroBanner() {
        return heroBanner;
    }

    public String getAssetGallerySelectionReference() {
        return assetGallerySelectionReference;
    }

    public String getDeckPlan() {
        return deckPlan;
    }

    public List<DiningModel> getDinings() {
        return dinings;
    }

    public List<PublicAreaModel> getPublicAreas() {
        return publicAreas;
    }

    public List<SuiteModel> getSuites() {
        return suites;
    }

    public Page getPage() {
        return page;
    }
}
