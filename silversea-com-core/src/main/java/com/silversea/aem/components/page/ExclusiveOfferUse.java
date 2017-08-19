package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.models.GeolocationTagModel;
import com.silversea.aem.services.GeolocationService;
import com.silversea.aem.services.GeolocationTagService;

/**
 * Created by asiba on 21/06/2017.
 */
public class ExclusiveOfferUse extends WCMUsePojo {

    private ExclusiveOfferModel exclusiveOfferModel;

    private boolean available;

    @Override
    public void activate() throws Exception {
        exclusiveOfferModel = getCurrentPage().adaptTo(ExclusiveOfferModel.class);

        // init geolocations informations
        String geomarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;

        final GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);
        if (geolocationTagService != null) {
            final GeolocationTagModel geolocationTagModel = geolocationTagService.getGeolocationTagModelFromRequest(
                    getRequest());

            if (geolocationTagModel != null) {
                geomarket = geolocationTagModel.getMarket();
            }
        }

        if (exclusiveOfferModel.getGeomarkets().contains(geomarket)) {
            available = true;
        }
    }

    public ExclusiveOfferModel getExclusiveOfferModel() {
        return exclusiveOfferModel;
    }

    public boolean isAvailable() {
        return available;
    }
}
