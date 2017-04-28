package com.silversea.aem.components.page;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.services.GeolocationTagService;

public class CruiseUse extends WCMUsePojo{

    //TODO : to change US AND FT by default
    private static final String DEFAULT_GEOLOCATION_COUTRY = "FR";
    private static final String DEFAULT_GEOLOCATION_GEO_MARKET_CODE = "EU";
    
    
    private GeolocationTagService geolocationTagService;
    
    private CruiseModel cruiseModel;
    private String previous;
    private String next;
    
    private TagManager tagManager;
 
    
    @Override
    public void activate() throws Exception {
        tagManager = getResourceResolver().adaptTo(TagManager.class);
        geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);
        GeoLocation geoLocation = initGeolocation(geolocationTagService);
        cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
        cruiseModel.initByGeoLocation(geoLocation);
        initPagination();
    }
    
    private GeoLocation initGeolocation(GeolocationTagService geolocationTagService){
        
        String tagId = geolocationTagService.getTagFromRequest(getRequest());
        String country = GeolocationHelper.getCountryCode(getRequest());
        String geoMarketCode = getGeoMarketCode(tagId);
        GeoLocation geoLocation = new GeoLocation();
        if(!StringUtils.isEmpty(country) && !StringUtils.isEmpty(geoMarketCode)){
            geoLocation.setCountry(country);
            geoLocation.setGeoMarketCode(geoMarketCode.toUpperCase());
        }
        else{
           geoLocation.setCountry(DEFAULT_GEOLOCATION_COUTRY);
           geoLocation.setGeoMarketCode(DEFAULT_GEOLOCATION_GEO_MARKET_CODE);
        }
        return geoLocation;
    }

    private void initPagination(){
        Page currentPage = getCurrentPage();
        Iterator<Page> children = currentPage.getParent().listChildren();
        if(children != null && children.hasNext()){
            while(children.hasNext()){
                Page current = children.next();
                if(StringUtils.equals(current.getPath(), currentPage.getPath())){
                    if(children.hasNext()){
                        next = children.next().getPath();
                    }
                    break;
                }
                previous = current.getPath();
            }
        }    
    }
    
    private String getGeoMarketCode(String geolocationTag) {
        String geoMarketCode = null;

        Tag tag = tagManager.resolve(geolocationTag);
        if (tag != null) {
            geoMarketCode = tag.getParent().getParent().getName();
        }
        return geoMarketCode;
    }
    
    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }

    /**
     * @return cruise's model
     */
    public CruiseModel getCruiseModel() {
        return cruiseModel;
    }

}
