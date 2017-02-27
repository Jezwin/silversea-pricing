/**
 * 
 */
package com.silversea.aem.components.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.silversea.aem.components.services.GeolocationTagCacheService;
import com.silversea.aem.helper.GeolocationHelper;

/**
 * @author mjedli
 *
 */
@Service
@Component(immediate = true, label = "Silversea.com - Geolocation Tag Cache Service")
public class GeolocationTagCacheServiceImpl implements GeolocationTagCacheService {

    private static final String REQUEST_COUNTRY_ID_PARAM = "countryId";

    private static final String ETC_TAGS_GEOLOCATION_PATH = "/etc/tags/geolocation/";

    static final private Logger LOGGER = LoggerFactory.getLogger(GeolocationTagCacheServiceImpl.class);
    
    public Map<String, String> mapTags;

    private boolean isInitService = false;

    private void initService(ResourceResolver resourceResolver) throws RepositoryException {

        mapTags = new HashMap<String, String>();

        Resource resourceTag = resourceResolver.getResource(ETC_TAGS_GEOLOCATION_PATH);

        Tag geolocTag = resourceTag.adaptTo(Tag.class);
        Iterator<Tag> iteratorGeolocNode = geolocTag.listChildren();

        while (iteratorGeolocNode.hasNext()) {

            Tag areaTag = iteratorGeolocNode.next();
            Iterator<Tag> iteratorAreaNode = areaTag.listChildren();

            while (iteratorAreaNode.hasNext()) {
                Tag marketTag = iteratorAreaNode.next();
                Iterator<Tag> iteratorMarketNode = marketTag.listChildren();

                while (iteratorMarketNode.hasNext()) {
                    Tag countryCodeTag = iteratorMarketNode.next();
                    if  (countryCodeTag.getTitle() != null && !"".equals(countryCodeTag.getTitle())) {
                        mapTags.put(countryCodeTag.getTitle(), countryCodeTag.getTagID());
                    } else {
                        LOGGER.debug("Title not found for {} tag, set tag title and reload aem instance.", countryCodeTag.getPath());
                    }
                }
            }
        }

        isInitService = true;
    }

    @Override
    public Map<String, String> getTags(ResourceResolver resourceResolver) throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return mapTags;
    }

    @Override
    public String getTagIdFromCountryId(ResourceResolver resourceResolver, String countryId)
            throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return mapTags.get(countryId);
    }

    @Override
    public String getTagIdFromCurrentRequest(ResourceResolver resourceResolver, SlingHttpServletRequest request)
            throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return mapTags.get(GeolocationHelper.getCoutryCodeFromSelector(request.getRequestPathInfo().getSelectorString()));
    }

}
