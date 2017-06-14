package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;

public class CountryHelper extends WCMUsePojo {

    private String country;

    @Override
    public void activate() throws Exception {
        country = GeolocationHelper.getCountryCode(getRequest());
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

}