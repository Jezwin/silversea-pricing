package com.silversea.aem.models;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.RenditionPicker;
import com.day.cq.dam.commons.util.PrefixRenditionPicker;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * Created by aurelienolivier on 19/03/2017.
 */
@Model(adaptables = Asset.class)
public class BrochureModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(BrochureModel.class);

    @Self
    private Asset asset;

    private Resource assetResource;

    private TagManager tagManager;

    @PostConstruct
    private void init() {
        try {
            assetResource = asset.adaptTo(Resource.class);
            tagManager = assetResource.getResourceResolver().adaptTo(TagManager.class);
        } catch (RuntimeException e) {
            LOGGER.error("Error while initializing model {}", e);
        }
    }

    public String getCover() {
        if (asset != null) {
            Rendition renditionPng = asset.getRendition("cover.png");
            if (renditionPng != null) {
                return renditionPng.getPath();
            }
            RenditionPicker renditionPicker = new PrefixRenditionPicker("cover");
            Rendition rendition = asset.getRendition(renditionPicker);
            if (rendition != null) {
                return rendition.getPath();
            }
        }

        return null;
    }

    public String getBrochurePath() {
        return asset.getPath();
    }

    public String getBrochureCode() {
        return getCustomMetadata(WcmConstants.PN_BROCHURE_CODE, String.class);
    }

    public String getAssetTitle() {
        return asset.getMetadataValue(DamConstants.DC_TITLE);
    }

    public String getAssetDescription() {
        return asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
    }

    public String getOnlineBrochureUrl() {
        return getCustomMetadata(WcmConstants.PN_BROCHURE_ONLINE_URL, String.class);
    }

    public Boolean isBrochureDigitalOnly() {
        return getCustomMetadata(WcmConstants.PN_BROCHURE_IS_DIGITAL_ONLY, Boolean.class);
    }

    public Boolean isPreOrder() {
        return getCustomMetadata(WcmConstants.PN_BROCHURE_IS_PREORDER, Boolean.class);
    }

    public Tag getLanguage() {
        Resource metadataResource = assetResource.getChild("jcr:content/metadata");

        if (metadataResource != null) {
            Tag[] tags = tagManager.getTags(metadataResource);

            for (Tag tag : tags) {
                if (tag.getTagID().startsWith(WcmConstants.TAG_NAMESPACE_LANGUAGES)) {
                    return tag;
                }
            }
        }

        return null;
    }

    public ArrayList<Tag> getGroups() {
        return getBrochureTags(WcmConstants.TAG_NAMESPACE_BROCHURE_GROUPS);
    }

    public ArrayList<String> getGroupNames() {
        return getBrochureTagValues(WcmConstants.TAG_NAMESPACE_BROCHURE_GROUPS);
    }

    public ArrayList<Tag> getLocalizations() {
        return getBrochureTags(WcmConstants.GEOLOCATION_TAGS_PREFIX);
    }

    public ArrayList<String> getGetLocalizationNames() {
        return getBrochureTagValues(WcmConstants.GEOLOCATION_TAGS_PREFIX);
    }

    public ArrayList<Tag> getBrochureTags(String tagName) {
        Resource metadataResource = assetResource.getChild("jcr:content/metadata");
        ArrayList<Tag> brochureTags = new ArrayList<Tag>();
        if (metadataResource != null) {
            Tag[] tags = tagManager.getTags(metadataResource);
            for (Tag tag : tags) {
                if (tag.getTagID().startsWith(tagName)) {
                    brochureTags.add(tag);
                }
            }
        }
        return brochureTags;
    }

    public ArrayList<String> getBrochureTagValues(String tagName) {
        Resource metadataResource = assetResource.getChild("jcr:content/metadata");
        ArrayList<String> tagValues = new ArrayList<String>();
        if (metadataResource != null) {
            Tag[] tags = tagManager.getTags(metadataResource);
            for (Tag tag : tags) {
                if (tag.getTagID().startsWith(tagName)) {
                    tagValues.add(tag.getName());
                }
            }
        }
        return tagValues;
    }

    /**
     * Helper: retrieve custom asset's meta data
     *
     * @param propertyName property name
     * @param type         type
     */
    private <T> T getCustomMetadata(String propertyName, Class<T> type) {
        Resource metadataResource = assetResource.getChild("jcr:content/metadata");
        T value = null;
        if (metadataResource != null) {
            ValueMap properties = ResourceUtil.getValueMap(metadataResource);
            value = properties.get(propertyName, type);
        }

        return value;
    }
}