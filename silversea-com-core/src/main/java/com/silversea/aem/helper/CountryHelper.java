package com.silversea.aem.helper;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.services.GeolocationTagService;

public class CountryHelper extends WCMUsePojo {

    private String country;
    private String localizedPhone;

    @Override
    public void activate() throws Exception {
        country = GeolocationHelper.getCountryCode(getRequest());

        // get geolocalized phone number
        GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);
        final String geolocationTagId = geolocationTagService.getTagFromRequest(getRequest());
        TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
        if (geolocationTagId != null) {
            Tag geolocationTag = tagManager.resolve(geolocationTagId);
            if (geolocationTag != null) {
                Resource node = geolocationTag.adaptTo(Resource.class);
                localizedPhone = node.getValueMap().get("phone", String.class);
            }
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

}