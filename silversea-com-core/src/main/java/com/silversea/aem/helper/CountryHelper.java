package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.services.GeolocationService;

public class CountryHelper extends WCMUsePojo {

    private String country;
    private String localizedPhone;

    private GeolocationService geolocationService;

    @Override
    public void activate() throws Exception {
        country = GeolocationHelper.getCountryCode(getRequest());

        // get geolocalized phone number
        geolocationService = getSlingScriptHelper().getService(GeolocationService.class);
        localizedPhone = geolocationService.getLocalizedPhone(getRequest());
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    public String getLocalizedPhone() {
        return localizedPhone;
    }

}