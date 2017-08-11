package com.silversea.aem.models;

import com.day.cq.tagging.Tag;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Tag.class)
public class FeatureModel {

    @Inject @Self
    private Tag tag;

    private String featureId;

    private String icon;

    private String featureCode;

    private String description;

    @PostConstruct
    private void init() {
        // Need resource for extra properties (icon, description, ...)
        final Resource resource = tag.adaptTo(Resource.class);

        if (resource != null) {
            final ValueMap properties = resource.getValueMap();

            featureId = properties.get("featureId", String.class);
            icon = properties.get("icon", String.class);
            featureCode = properties.get("featureCode", String.class);
            description = properties.get("jcr:description", String.class);
        }
    }

    public String getTitle() {
        return tag.getTitle();
    }

    public String getFeatureId() {
        return featureId;
    }

    public String getIcon() {
        return icon;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public String getDescription() {
        return description;
    }
}
