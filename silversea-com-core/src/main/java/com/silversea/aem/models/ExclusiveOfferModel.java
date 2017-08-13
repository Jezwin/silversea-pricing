package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.silversea.aem.components.beans.GeoLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.Destination;
import com.silversea.aem.technical.json.JsonMapper;

@Model(adaptables = Page.class)
public class ExclusiveOfferModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOfferModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cq:tags") @Optional
    private String[] tagIds;

    private List<String> geomarkets = new ArrayList<>();

    @Inject @Named(JcrConstants.JCR_CONTENT + "/jcr:title")
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/jcr:description") @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cruiseFareAdditions") @Optional
    private String[] cruiseFareAdditionsJson;

    private List<String> cruiseFareAdditions = new ArrayList<>();

    @Inject @Named(JcrConstants.JCR_CONTENT + "/mapOverhead") @Optional
    private String mapOverHead;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/lightboxReference") @Optional
    private String lightboxReference;

    @PostConstruct
    private void init() {
        // init geotagging
        if (tagIds != null) {
            for (String tagId : tagIds) {
                if (tagId.startsWith("geotagging:")) {
                    geomarkets.add(tagId.replace("geotagging:", ""));
                }
            }
        }

        // init cruise fare additions
        for (final String cruiseFareAdditionJson : cruiseFareAdditionsJson) {
            try {
                final JSONObject jsonObject = new JSONObject(cruiseFareAdditionJson);

                final String addition = jsonObject.optString("addition");
                if (StringUtils.isNotEmpty(addition)) {
                    cruiseFareAdditions.add(addition);
                }
            } catch (JSONException ignored) {}
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public List<String> getGeomarkets() {
        return geomarkets;
    }

    public List<String> getCruiseFareAdditions() {
        return cruiseFareAdditions;
    }

    public String getMapOverHead() {
        return mapOverHead;
    }

    public String getLightboxReference() {
        return lightboxReference;
    }





    // ---------------- TODO -------------- //

    public void initByGeoLocation(GeoLocation geolocation) {
    }

    public boolean isValid(String geoMarketCode) {
        return isTagExists(page, geoMarketCode);
    }

    public void initDescription(String country, String destination) {
        if (StringUtils.isEmpty(getDestinationText(destination))) {
            Page variation = getVariationByCountry(page, country);
            if (variation != null) {
                InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(variation.getContentResource());
                title = properties.getInherited(JcrConstants.JCR_TITLE, String.class);
                mapOverHead = properties.getInherited("mapOverhead", String.class);
                description = properties.getInherited("longDescription", String.class);
                String[] fareAdditons = properties.getInherited("cruiseFareAdditions", String[].class);
                //cruiseFareAdditions = initFareAdditions(fareAdditons);
                lightboxReference = properties.getInherited("lightboxReference", String.class);
            } else {
                description = page.getProperties().get("longDescription", String.class);
            }
        } else {
            description = getDestinationText(destination);
        }
    }

    private String getDestinationText(String destinationReference) {
        String description = null;
        String[] destinations = page.getProperties().get("destinations", String[].class);
        if (destinations != null) {
            for (String item : destinations) {
                Destination destination = JsonMapper.getDomainObject(item, Destination.class);
                if (destination != null && StringUtils.equals(destination.getReference(), destinationReference)) {
                    description = destination.getText();
                }
            }
        }
        return description;
    }

    public Page getVariationByCountry(Page page, String country) {
        Iterator<Page> children = page.listChildren();
        if (children != null && children.hasNext()) {
            while (children.hasNext()) {
                Page current = children.next();
                if (isTagExists(current, country)) {
                    return current;
                }
            }
        }

        return null;
    }

    public boolean isTagExists(Page page, String value) {
        boolean exist = false;
        Tag[] tags = page.getTags();
        if (tags != null) {
            for (Tag tag : tags) {
                if (StringUtils.equals(value, tag.getName().toUpperCase())) {
                    exist = true;
                }
            }
        }
        return exist;
    }
}
