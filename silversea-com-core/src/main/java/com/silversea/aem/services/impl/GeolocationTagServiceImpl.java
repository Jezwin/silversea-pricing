package com.silversea.aem.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@Component(immediate = true)
public class GeolocationTagServiceImpl implements GeolocationTagService {

    static final private Logger LOGGER = LoggerFactory.getLogger(GeolocationTagServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private Map<String, String> tagIdMapping;

    @Activate
    public void activate(final ComponentContext context) {
        tagIdMapping = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "geotagging-cache");

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);

            Resource geotaggingNamespace = resourceResolver.getResource(PATH_TAGS_GEOLOCATION);

            if (geotaggingNamespace != null) {
                Tag geotaggingTag = geotaggingNamespace.adaptTo(Tag.class);

                addTags(geotaggingTag);
            } else {
                LOGGER.debug("{} not found, do not build tag list", PATH_TAGS_GEOLOCATION);
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot get a repository session, do not build tag list", e);
        }
    }

    /**
     * Adding all leaf subtags (representing countries in the geotagging tags tree)
     * @param tag parent tag
     */
    private void addTags(final Tag tag) {
        Iterator<Tag> children = tag.listChildren();

        if (!children.hasNext()) {
            LOGGER.trace("Tag {} is a leaf, adding it the map", tag.getTagID());

            tagIdMapping.put(tag.getName(), tag.getTagID());
        } else {
            LOGGER.trace("Tag {} is not a leaf, browsing children", tag.getTagID());

            while (children.hasNext()) {
                Tag child = children.next();

                addTags(child);
            }
        }
    }

    @Override
    public String getTagFromCountryId(String countryId) {
        return tagIdMapping.get(countryId);
    }

    @Override
    public String getTagFromRequest(SlingHttpServletRequest request) {
        String countryCode = GeolocationHelper.getCountryCode(request);

        if (countryCode != null) {
            return tagIdMapping.get(countryCode);
        }

        return null;
    }
}
