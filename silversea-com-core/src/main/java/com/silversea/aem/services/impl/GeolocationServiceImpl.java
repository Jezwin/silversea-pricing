package com.silversea.aem.services.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.services.GeolocationService;
import com.silversea.aem.services.GeolocationTagService;

/**
 * TODO remove long running sessions https://cqdump.wordpress.com/2015/11/02/aem-anti-pattern-long-running-sessions/
 * TODO add label as service is exposed in OSGI configuration console
 */
@Service
@Component(immediate = true, metatype = true)
public class GeolocationServiceImpl implements GeolocationService {

    static final private Logger LOGGER = LoggerFactory.getLogger(GeolocationServiceImpl.class);

    // TODO : to change US AND FT by default
    private static final String DEFAULT_GEOLOCATION_COUTRY = "FR";
    private static final String DEFAULT_GEOLOCATION_GEO_MARKET_CODE = "EU";

    @Property(value = DEFAULT_GEOLOCATION_COUTRY, label = "Gelocation country")
    private static final String COUNTRY_GELOCATION_KEY = "country";
    private String defaultCountryCode;

    @Property(value = DEFAULT_GEOLOCATION_GEO_MARKET_CODE, label = "Geo market code")
    private static final String GEO_MARKET_CODE_KEY = "geoMarketCode";
    private String defaultGeoMarketCode;

    @Reference
    private GeolocationTagService geolocationTagService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private ResourceResolver resourceResolver;
    private TagManager tagManager;

    @Activate
    @Modified
    public void activate(final ComponentContext context) {
        init();
        Dictionary<?, ?> properties = context.getProperties();
        defaultCountryCode = PropertiesUtil.toString(properties.get(COUNTRY_GELOCATION_KEY), DEFAULT_GEOLOCATION_COUTRY);
        defaultGeoMarketCode = PropertiesUtil.toString(properties.get(GEO_MARKET_CODE_KEY), DEFAULT_GEOLOCATION_GEO_MARKET_CODE);
    }

    private void init() {
        try {
            Map<String, Object> authenticationPrams = new HashMap<String, Object>();
            // TODO to change user
            authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
            tagManager = resourceResolver.adaptTo(TagManager.class);
        } catch (LoginException e) {
            LOGGER.error("Geolocation service -- Login exception ", e);
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }

    public GeoLocation initGeolocation(SlingHttpServletRequest request) {

        String tagId = geolocationTagService.getTagFromRequest(request);
        String countryCode = GeolocationHelper.getCountryCode(request);
        String geoMarketCode = getGeoMarketCode(tagId);
        GeoLocation geoLocation = new GeoLocation();
        if (!StringUtils.isEmpty(countryCode) && !StringUtils.isEmpty(geoMarketCode)) {
            geoLocation.setCountry(countryCode);
            geoLocation.setGeoMarketCode(geoMarketCode.toUpperCase());
        } else {
            geoLocation.setCountry(defaultCountryCode);
            geoLocation.setGeoMarketCode(defaultGeoMarketCode);
        }
        return geoLocation;
    }

    /*
     * @return the geolocalized phone number
     */
    public String getLocalizedPhone(SlingHttpServletRequest request) {
        String localizedPhone = "";
        String geolocationTagId = geolocationTagService.getTagFromRequest(request);
        if (geolocationTagId == null) {
            //use default country
            geolocationTagId = geolocationTagService.getTagFromCountryId(DEFAULT_GEOLOCATION_COUTRY);
        }
        if (geolocationTagId != null) {
            Tag geolocationTag = tagManager.resolve(geolocationTagId);
            if (geolocationTag != null) {
                Resource node = geolocationTag.adaptTo(Resource.class);
                localizedPhone = node.getValueMap().get("phone", String.class);
            }
        }
        return localizedPhone;
    }

    private String getGeoMarketCode(String geolocationTag) {
        String geoMarketCode = null;

        Tag tag = tagManager.resolve(geolocationTag);
        if (tag != null) {
            geoMarketCode = tag.getParent().getParent().getName();
        }
        return geoMarketCode;
    }

}
