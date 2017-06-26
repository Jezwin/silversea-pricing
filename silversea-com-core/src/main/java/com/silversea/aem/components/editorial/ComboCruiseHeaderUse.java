package com.silversea.aem.components.editorial;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.models.ComboCruiseModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.models.SegmentModel;
import com.silversea.aem.services.GeolocationService;

public class ComboCruiseHeaderUse  extends WCMUsePojo{

    private PriceData lowestPrice;
    private String port;
    private ComboCruiseModel comboCruiseModel;
    private PageManager pageManager;
    private GeolocationService geolocationService;

    @Override
    public void activate() throws Exception {
        geolocationService= getSlingScriptHelper().getService(GeolocationService.class);
        String pageReference = getProperties().get("pageReference",String.class);
        if(!StringUtils.isEmpty(pageReference)){
            pageManager = getResourceResolver().adaptTo(PageManager.class);
            comboCruiseModel = initComboCruise(pageReference);
        }
        else{
            comboCruiseModel =  getCurrentPage().adaptTo(ComboCruiseModel.class);
        }
        
        if(comboCruiseModel != null){
            lowestPrice = comboCruiseModel.getLowestPrice();
            port = initPort(comboCruiseModel);
        } 
    }

    private ComboCruiseModel initComboCruise(String pageReference){
        Page page = pageManager.getPage(pageReference);
        ComboCruiseModel comboCruiseModel= null;
        if(page != null){
            GeoLocation geoLocation = geolocationService.initGeolocation(getRequest());
            comboCruiseModel = page.adaptTo(ComboCruiseModel.class);
            comboCruiseModel.initByGeoLocation(geoLocation);
        }
        return comboCruiseModel;
    }
    
    private String initPort(ComboCruiseModel comboCruiseModel){
        String port = null;
        List<SegmentModel> segments = comboCruiseModel.getSegments();
        if(segments != null && !segments.isEmpty()){
            SegmentModel segment = segments.get(0);
            if(segment !=null &&  segment.getCruise() != null){
                List<ItineraryModel> itineraries = segment.getCruise().getItineraries();
                if(itineraries!=null && !itineraries.isEmpty()){
                    port = itineraries.get(0).getTitle();
                }
            }
        }
        return port;
    }

    public PriceData getLowestPrice() {
        return lowestPrice;
    }

    public String getPort() {
        return port;
    }
}
