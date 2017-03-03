/**
 * 
 */
package com.silversea.aem.components.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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

    private static final String ETC_TAGS_GEOLOCATION_PATH = "/etc/tags/geolocation/";

    private static final String CONTENT_DAM_SIVERSEA_COM = "/content/dam/siversea-com";

    static final private Logger LOGGER = LoggerFactory.getLogger(GeolocationTagCacheServiceImpl.class);

    public Map<String, String> mapTags;

    private boolean isInitService = false;

    private List<String> langList;

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
                    if (countryCodeTag.getTitle() != null && !"".equals(countryCodeTag.getTitle())) {
                        mapTags.put(countryCodeTag.getName().toString().toLowerCase(), countryCodeTag.getTagID());
                    } else {
                        LOGGER.debug("Name not found for {} tag, set tag title and reload : Silversea.com - Geolocation Tag Cache Service.",
                                countryCodeTag.getPath());
                    }
                }
            }
        }

        Resource resourceNode = resourceResolver.getResource(CONTENT_DAM_SIVERSEA_COM);
        Node silverseaDamNode = resourceNode.adaptTo(Node.class);
        NodeIterator langNodeIterator = silverseaDamNode.getNodes();

        langList = new ArrayList<String>();

        while (langNodeIterator.hasNext()) {
            Node currentLangNode = (Node) langNodeIterator.next();
            if (currentLangNode.getName() != null && !"jcr:content".equals(currentLangNode.getName()))
                langList.add(currentLangNode.getName());
        }

        isInitService = false;
    }

    @Override
    public List<String> getLangList(ResourceResolver resourceResolver) throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return langList;
    }

    @Override
    public Map<String, String> getTags(ResourceResolver resourceResolver) throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return mapTags;
    }

    @Override
    public String getTagIdFromCountryId(ResourceResolver resourceResolver, String countryId) throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return mapTags.get(countryId);
    }

    @Override
    public String getTagIdFromCurrentRequest(ResourceResolver resourceResolver, SlingHttpServletRequest request) throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return mapTags.get(GeolocationHelper.getCoutryCodeFromSelector(request.getRequestPathInfo().getSelectors()));
    }

    @Override
    public String getLanguageCodeCurrentRequest(ResourceResolver resourceResolver, SlingHttpServletRequest request) throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return GeolocationHelper.getLanguageCodeFromSelector(request.getRequestPathInfo().getSelectors());
    }

}
