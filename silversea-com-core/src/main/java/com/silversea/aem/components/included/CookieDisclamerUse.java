package com.silversea.aem.components.included;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.services.GeolocationTagCacheService;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;

/**
 * Created by asiba on 14/03/2017.
 */
public class CookieDisclamerUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieDisclamerUse.class);

    private GeolocationTagCacheService geolocService;

    private String currentCountrySelector;

    private Boolean showCookieMsg;

    private String description;
    
    @Override
    public void activate() throws Exception {
        InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());
        description = properties.getInherited("jcr:description", String.class);

        showCookieMsg = false;

        geolocService = getSlingScriptHelper().getService(GeolocationTagCacheService.class);

        String[] tagIds = getProperties().get("cq:tags", String[].class);

        currentCountrySelector = GeolocationHelper.getCoutryCodeFromSelector(getRequest().getRequestPathInfo().getSelectors());

        /*
        * TODO : AUO Should i put this code in GeolocationTagCacheSevice method ?
        * */
        ResourceResolver resourceResolver = getResourceResolver();
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        for (String tagId : tagIds) {
            Tag tmp = tagManager.resolve(tagId);
            if (tmp.getTitle().toLowerCase().equals(currentCountrySelector))
                showCookieMsg = true;
        }
    }

    /**
     * @return the showCookieMsg
     */
    public Boolean getShowCookieMsg() {
        return showCookieMsg;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}