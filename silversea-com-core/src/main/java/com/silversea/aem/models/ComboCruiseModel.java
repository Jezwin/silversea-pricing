package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
import com.silversea.aem.components.beans.ItinerariesData;
import com.silversea.aem.components.beans.PriceData;


@Model(adaptables = { Page.class })
public class ComboCruiseModel extends AbstractModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ComboCruiseModel.class);

    @Inject
    @Self
    private Page page;

    private ShipModel ship;
    private List<SegmentModel> segments;
    private ItinerariesData itinerariesData;
    private PriceData lowestPrice;
    private List<SuiteModel> suites;
    private int nbTab;
    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    
    @PostConstruct
    private void init() {
        try {
            resourceResolver = page.getContentResource().getResourceResolver();
            pageManager = resourceResolver.adaptTo(PageManager.class);
            String shipReference = page.getProperties().get("shipReference",String.class);
            ship = initShip(shipReference, pageManager);
        } catch (RuntimeException e) {
            LOGGER.error("Error while initializing model {}", e);
        }
    }

    public void initByGeoLocation(GeoLocation geoLocation) {
        lowestPrice = initLowestPrice(geoLocation.getGeoMarketCode(),page);
        initSegments(geoLocation);
        itinerariesData = initItinerariesData();
        suites = initSuites(page,geoLocation.getGeoMarketCode(),pageManager);
    }
 
    private void initSegments(GeoLocation geoLocation){
        Iterator<Page> children = page.listChildren();
        if(children != null){
            segments = new ArrayList<SegmentModel>();
            children.forEachRemaining(segment ->{
                if(segment != null){
                    SegmentModel segmentModel = segment.adaptTo(SegmentModel.class);
                    segmentModel.initByGeoLocation(geoLocation);
                    segments.add(segmentModel);
                }
            });
        }
    }
    
    public ItinerariesData initItinerariesData() {
        int nbHotels = 0;
        int nbExcursions = 0;
        int nbLandPrograms = 0;
        if(segments != null && !segments.isEmpty()){
            List<ItinerariesData> list = segments.stream()
                                                 .map(SegmentModel::getCruise)
                                                 .map(CruiseModel::getItinerariesData)
                                                 .collect(Collectors.toList());
            if(list != null && !list.isEmpty()){
                for(ItinerariesData element : list){
                    nbHotels += element.getNbHotels();
                    nbExcursions += element.getNbExcursions();
                    nbLandPrograms += element.getNbLandPrograms();
                }
            }
        }
        
       initTabs(nbHotels, nbExcursions, nbLandPrograms);
       return new ItinerariesData(nbHotels, nbExcursions, nbLandPrograms);
    }
    
    public void initTabs(int nbHotels, int nbExcursions, int nbLandPrograms){
        if(nbExcursions > 0 && (nbLandPrograms > 0 || nbHotels > 0)){
            nbTab = 7;  
        }
        else if (nbExcursions < 0 && (nbLandPrograms > 0 || nbHotels > 0)){
            nbTab = 6; 
        }
        else if (nbExcursions > 0 && nbLandPrograms < 0 && nbHotels < 0){
            nbTab = 6; 
        }
        else{
            nbTab = 5;
        }
    }
    
    public PriceData getLowestPrice() {
        return lowestPrice;
    }

    public Page getPage() {
        return page;
    }

    public List<SegmentModel> getSegments() {
        return segments;
    }

    public List<SuiteModel> getSuites() {
        return suites;
    }

    public ShipModel getShip() {
        return ship;
    }

    public ItinerariesData getItinerariesData() {
        return itinerariesData;
    }

    public int getNbTab() {
        return nbTab;
    } 
}