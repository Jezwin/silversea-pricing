package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.silversea.aem.components.beans.PriceData;


@Model(adaptables = { Page.class })
public class ComboCruiseModel extends AbstractModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ComboCruiseModel.class);

    @Inject
    @Self
    private Page page;

    private PriceData lowestPrice;
    private List<CruiseModel> cruises;
    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    
    @PostConstruct
    private void init() {
        try {
            resourceResolver = page.getContentResource().getResourceResolver();
            pageManager = resourceResolver.adaptTo(PageManager.class);
            
        } catch (RuntimeException e) {
            LOGGER.error("Error while initializing model {}", e);
        }
    }

    public void initByGeoLocation(GeoLocation geoLocation) {
        lowestPrice = initLowestPrice(geoLocation.getGeoMarketCode(),page);
        initCruises(geoLocation);
        LOGGER.debug("");
    }

    private void initCruises(GeoLocation geoLocation){
        Iterator<Page> children = page.listChildren();
        if(children != null){
            cruises = new ArrayList<CruiseModel>();
            children.forEachRemaining(segment ->{
                String cruiseReference = segment.getProperties().get("cruiseReference",String.class);
                Page cruisePage = getPage(cruiseReference,pageManager);
                if(cruisePage!=null){
                    CruiseModel cruiseModel = cruisePage.adaptTo(CruiseModel.class);
                    cruiseModel.initByGeoLocation(geoLocation);
                    cruises.add(cruiseModel);
                }
            });
        }
    }
    public PriceData getLowestPrice() {
        return lowestPrice;
    }

    public Page getPage() {
        return page;
    }

    public List<CruiseModel> getCruises() {
        return cruises;
    }
    
}