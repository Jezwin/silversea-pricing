package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.services.GeolocationService;

/**
 * Created by asiba on 21/06/2017.
 */
public class ExclusiveOffersUse extends WCMUsePojo {

    private GeolocationService geolocationService;
    private ExclusiveOfferModel exclusiveOfferModel;

    @Override
    public void activate() throws Exception {

        geolocationService = getSlingScriptHelper().getService(GeolocationService.class);
        GeoLocation geoLocation = geolocationService.initGeolocation(getRequest());
        exclusiveOfferModel = getCurrentPage().adaptTo(ExclusiveOfferModel.class);
        exclusiveOfferModel.initByGeoLocation(geoLocation);
    }

    public ExclusiveOfferModel getExclusiveOfferModel() {
        return exclusiveOfferModel;
    }
}
