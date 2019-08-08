package com.silversea.aem.helper;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import org.apache.commons.lang3.StringUtils;

public class CountryHelper extends AbstractGeolocationAwareUse {

    private String hrefLangValid;

    private String localizedPhone;

    private String clickableLocalizedPhone;

    @Override
    public void activate() throws Exception {
        super.activate();

        // get geolocalized phone number
        localizedPhone = geolocation.getPhone();
        clickableLocalizedPhone = localizedPhone;

        if (StringUtils.isNotEmpty(localizedPhone)) {
            // Remove any non-digit
            clickableLocalizedPhone = localizedPhone.replaceAll("\\s", "").replaceAll("-", "");
        }

        hrefLangValid = getCurrentPage().getLanguage(false).toLanguageTag();
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return countryCode;
    }

    /**
     * @return the hrefLangValid
     */
    public String getHrefLangValid() {
        return hrefLangValid;
    }

    /**
     * @return the localizedPhone
     */
    public String getLocalizedPhone() {
        return localizedPhone;
    }

    /**
     * @return the clickableLocalizedPhone
     */
    public String getClickableLocalizedPhone() {
        return clickableLocalizedPhone;
    }
}