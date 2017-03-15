package com.silversea.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.silversea.aem.components.services.GeolocationTagCacheService;
import com.silversea.aem.helper.GeolocationHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.*;

/**
 * Created by asiba on 14/03/2017.
 */
public class CookieDisclamerUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieDisclamerUse.class);

    private GeolocationTagCacheService geolocService;

    private String currentCountrySelector;

    private Boolean showCookieMsg;

    @Override
    public void activate() throws Exception {

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

    public Boolean getShowCookieMsg() {
        return showCookieMsg;
    }

}
