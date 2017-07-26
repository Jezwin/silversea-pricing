package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.services.GeolocationService;

public class CountryHelper extends WCMUsePojo {

    private String country;
    private String hrefLangValid;
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
            // Remove any non-digit
            clickableLocalizedPhone = localizedPhone.replaceAll("\\D", "");
        }

        String pagePath = get("path", String.class);
        Resource res = getResourceResolver().getResource(pagePath);
        if (StringUtils.isNotBlank(pagePath)) {
            if (res != null) {
                InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(res);
                Page page = res.adaptTo(Page.class);
                hrefLangValid = properties.getInherited(JcrConstants.JCR_LANGUAGE, page.getProperties().get(JcrConstants.JCR_LANGUAGE, String.class)).replaceAll("_", "-");
            }
        }
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
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