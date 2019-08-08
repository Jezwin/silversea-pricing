package com.silversea.aem.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.GeolocationTagModel;
import com.silversea.aem.services.GeolocationTagService;

/**
 * @author aurelienolivier
 */
public class AbstractGeolocationAwareUse extends AbstractSilverUse {

    protected GeolocationTagModel geolocation;

    protected String geomarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;

    protected String countryCode = WcmConstants.DEFAULT_GEOLOCATION_COUNTRY;

    protected String countryCodeIso3 = WcmConstants.DEFAULT_GEOLOCATION_COUNTRY_ISO3;

    protected String currency = WcmConstants.DEFAULT_CURRENCY;

    @Override
    public void activate() throws Exception {
        GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);

        if (geolocationTagService != null) {
            geolocation = geolocationTagService.getGeolocationTagModelFromRequest(getRequest());

            if (geolocation != null) {
                geomarket = geolocation.getMarket();
                countryCode = geolocation.getCountryCode();
                countryCodeIso3 = geolocation.getCountryCodeIso3();
                currency = geolocation.getCurrency();
            } else {
                geolocation = geolocationTagService.getGeolocationTagModelFromCountryCode(getRequest().getResourceResolver(), countryCode);

                if (geolocation != null) {
                    geomarket = geolocation.getMarket();
                    countryCode = geolocation.getCountryCode();
                    countryCodeIso3 = geolocation.getCountryCodeIso3();
                    currency = geolocation.getCurrency();
                }
            }
        }
    }
    
	/**
	 * Check is the tag is a best match. The function compare market, geoArea
	 * and country withgeolocation obj.
	 * 
	 * @param tag
	 *            Tag to extract market, geoArea and country
	 * @return true/false if is best match
	 */
	protected boolean isBestMatch(String tag) {
		boolean retVal = false;
		// split tag by market/geoArea/country as/far-east/CK
		String[] splitTag = tag.split("/");
		String market = (splitTag.length > 0) ? splitTag[0] : null;
		String geoArea = (splitTag.length > 1) ? splitTag[1] : null;
		String country = (splitTag.length > 2) ? splitTag[2] : null;

		if ((market != null) && (this.geomarket != null)) {
			retVal = market.equalsIgnoreCase(this.geomarket);

			if (retVal && (geoArea != null) && (this.geolocation.getRegion() != null)) {
				retVal = geoArea.equalsIgnoreCase(this.geolocation.getRegion()) || geoArea.equalsIgnoreCase(this.geolocation.getRegionFromPath());

				if (retVal && (country != null) && (this.countryCode != null || this.countryCodeIso3 != null)) {
					retVal = (country.equalsIgnoreCase(this.countryCode) || country
							.equalsIgnoreCase(this.countryCodeIso3));
				}
			}
		}

		return retVal;
	}

    public String getCountryCodeIso3() {
        return countryCodeIso3;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
}
