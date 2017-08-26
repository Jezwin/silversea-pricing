package com.silversea.aem.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.GeolocationTagModel;
import com.silversea.aem.services.GeolocationTagService;

/**
 * @author aurelienolivier
 */
public class AbstractGeolocationAwareUse extends WCMUsePojo {

    protected GeolocationTagModel geolocation;

    protected String geomarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;

    protected String countryCode = WcmConstants.DEFAULT_GEOLOCATION_COUNTRY;

    protected String countryCodeIso3 = WcmConstants.DEFAULT_GEOLOCATION_COUNTRY_ISO3;

    protected String currency = WcmConstants.DEFAULT_CURRENCY;

    @Override
    public void activate() throws Exception {
        final GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);

        if (geolocationTagService != null) {
            geolocation = geolocationTagService.getGeolocationTagModelFromRequest(getRequest());

            if (geolocation != null) {
                geomarket = geolocation.getMarket();
                countryCode = geolocation.getCountryCode();
                countryCodeIso3 = geolocation.getCountryCodeIso3();
                currency = geolocation.getCurrency();
            } else {
                geolocation = geolocationTagService.getGeolocationTagModelCountryCode(getRequest(), countryCode);

                geomarket = geolocation.getMarket();
                countryCode = geolocation.getCountryCode();
                countryCodeIso3 = geolocation.getCountryCodeIso3();
                currency = geolocation.getCurrency();
            }
        }
    }

    public String getCountryCodeIso3() {
        return countryCodeIso3;
    }
}
