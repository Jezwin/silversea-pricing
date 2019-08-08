package com.silversea.aem.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.models.GeolocationTagModel;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
@Component(immediate = true)
public class GeolocationTagServiceImpl implements GeolocationTagService {

    static final private Logger LOGGER = LoggerFactory.getLogger(GeolocationTagServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private Map<String, String> tagIdMapping;

    private Map<String, String> iso3codeTagIdMapping;

    @Activate
    public void activate(final ComponentContext context) {
        tagIdMapping = new HashMap<>();
        iso3codeTagIdMapping = new HashMap<>();

        final Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "geotagging-cache");

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params)) {
            final Resource geotaggingNamespace = resourceResolver.getResource(WcmConstants.PATH_TAGS_GEOLOCATION);

            if (geotaggingNamespace != null) {
                Tag geotaggingTag = geotaggingNamespace.adaptTo(Tag.class);

                addTagsToCache(geotaggingTag);
            } else {
                LOGGER.debug("{} not found, do not build tag list", WcmConstants.PATH_TAGS_GEOLOCATION);
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot get a repository session, do not build tag list", e);
        }
    }

    @Override
    public Map<String, String> getTagIdsMapping() {
        return tagIdMapping;
    }

    @Override
    public String getTagIdFromCountryId(final String countryId) {
        LOGGER.trace("Searching for {} in tags mapping, found {}", countryId, tagIdMapping.get(countryId));

        return tagIdMapping.get(countryId);
    }

    @Override
    public String getTagIdFromCountryCodeIso3(final String countryCodeIso3) {
        LOGGER.trace("Searching for {} in tags mapping, found {}", countryCodeIso3, iso3codeTagIdMapping.get(countryCodeIso3));

        return iso3codeTagIdMapping.get(countryCodeIso3);
    }

    @Override
    public List<String> getTagIdsFromCountryCodeIso3(final List<String> countryCodesIso3) {
        List<String> tagIds = new ArrayList<>();

        for (String countryCode : countryCodesIso3) {
            tagIds.add(getTagIdFromCountryCodeIso3(countryCode));
        }

        return tagIds;
    }

    @Override
    public String getTagIdFromRequest(final SlingHttpServletRequest request) {
        final String countryCode = GeolocationHelper.getCountryCode(request);

        if (countryCode != null) {
            return tagIdMapping.get(countryCode);
        }

        return null;
    }

    @Override
    public GeolocationTagModel getGeolocationTagModelFromRequest(final SlingHttpServletRequest request) {
        final String geolocationTagId = getTagIdFromRequest(request);

        return getGeolocationTagModel(request.getResourceResolver(), geolocationTagId);
    }

    @Override
    public GeolocationTagModel getGeolocationTagModelFromCountryCode(final ResourceResolver resourceResolver, final String countryCode) {
        final String geolocationTagId = tagIdMapping.get(countryCode);

        return getGeolocationTagModel(resourceResolver, geolocationTagId);
    }

    /**
     * Adding all leaf subtags (representing countries in the geotagging tags tree)
     * @param tag parent tag
     */
    private void addTagsToCache(final Tag tag) {
        Iterator<Tag> children = tag.listChildren();

        if (!children.hasNext()) {
            LOGGER.trace("Tag {} is a leaf, adding it the map", tag.getTagID());

            // Mapping tag name (iso2) with tag ID
            tagIdMapping.put(tag.getName(), tag.getTagID());

            // Mapping iso3 code with tag ID
            final Resource tagResource = tag.adaptTo(Resource.class);
            if (tagResource != null) {
                final ValueMap properties = tagResource.getValueMap();

                if (properties.get("iso3", String.class) != null) {
                    iso3codeTagIdMapping.put(properties.get("iso3", String.class), tag.getTagID());
                }
            }
        } else {
            LOGGER.trace("Tag {} is not a leaf, browsing children", tag.getTagID());

            while (children.hasNext()) {
                Tag child = children.next();

                addTagsToCache(child);
            }
        }
    }

    private GeolocationTagModel getGeolocationTagModel(final ResourceResolver resourceResolver, final String geolocationTagId) {
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);

        if (tagManager != null && geolocationTagId != null) {
            final Tag geolocationTag = tagManager.resolve(geolocationTagId);

            if (geolocationTag != null) {
                final Resource geolocationTagResource = geolocationTag.adaptTo(Resource.class);

                if (geolocationTagResource != null) {
                    return geolocationTagResource.adaptTo(GeolocationTagModel.class);
                }
            }
        }

        return null;
    }
}
