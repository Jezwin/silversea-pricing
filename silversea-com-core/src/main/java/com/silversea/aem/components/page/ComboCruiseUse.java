package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.models.ComboCruiseModel;
import com.silversea.aem.services.GeolocationService;
import com.silversea.aem.utils.PathUtils;

public class ComboCruiseUse extends WCMUsePojo {

    private GeolocationService geolocationService;
    private ComboCruiseModel comboCruiseModel;

    @Override
    public void activate() throws Exception {

        geolocationService = getSlingScriptHelper().getService(GeolocationService.class);
        GeoLocation geoLocation = geolocationService.initGeolocation(getRequest());
        comboCruiseModel = getCurrentPage().adaptTo(ComboCruiseModel.class);
        comboCruiseModel.initByGeoLocation(geoLocation);
    }

    public ComboCruiseModel getComboCruiseModel() {
        return comboCruiseModel;
    }

    /**
     * Return path for request quote page
     */
    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage().getLanguage(false));
    }

}