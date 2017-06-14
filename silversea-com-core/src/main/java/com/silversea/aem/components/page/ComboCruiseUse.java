package com.silversea.aem.components.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.models.ComboCruiseModel;
import com.silversea.aem.services.GeolocationService;

public class ComboCruiseUse extends WCMUsePojo {

    static final private Logger LOGGER = LoggerFactory.getLogger(ComboCruiseUse.class);

    private GeolocationService geolocationService;
    private ComboCruiseModel comboCruiseModel;
    
    @Override
    public void activate() throws Exception {
        
        geolocationService = getSlingScriptHelper().getService(GeolocationService.class);
        GeoLocation geoLocation = geolocationService.initGeolocation(getRequest());
        comboCruiseModel = getCurrentPage().adaptTo(ComboCruiseModel.class);
        comboCruiseModel.initByGeoLocation(geoLocation);
        LOGGER.debug("");
    }

    public ComboCruiseModel getComboCruiseModel() {
        return comboCruiseModel;
    }
    
}