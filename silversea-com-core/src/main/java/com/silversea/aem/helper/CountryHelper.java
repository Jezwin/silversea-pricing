package com.silversea.aem.helper;

import org.apache.cxf.common.util.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.services.GeolocationService;

public class CountryHelper extends WCMUsePojo {

    private String country;
    private String localizedPhone;
    private String clickableLocalizedPhone;

    private GeolocationService geolocationService;

    @Override
    public void activate() throws Exception {
        country = GeolocationHelper.getCountryCode(getRequest());

        // get geolocalized phone number
        geolocationService = getSlingScriptHelper().getService(GeolocationService.class);
        localizedPhone = geolocationService.getLocalizedPhone(getRequest());
        clickableLocalizedPhone = localizedPhone;
        if (!StringUtils.isEmpty(localizedPhone)) {
            clickableLocalizedPhone = localizedPhone.replaceAll("\\s", "").replaceAll("-", "");
        }
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

    public String getClickableLocalizedPhone() {
        return clickableLocalizedPhone;
    }

}