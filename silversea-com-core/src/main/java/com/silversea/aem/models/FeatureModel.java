package com.silversea.aem.models;

import com.day.cq.tagging.Tag;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Objects;

@Model(adaptables = Tag.class)
public class FeatureModel {

    @Inject
    @Self
    private Tag tag;

    private String title;

    private String featureId;

    private String icon;

    private String featureCode;

    private String description;

    private String name;

    private String path;

    @PostConstruct
    private void init() {
        // Need resource for extra properties (icon, description, ...)
        final Resource resource = tag.adaptTo(Resource.class);

        if (resource != null) {
            final ValueMap properties = resource.getValueMap();

            title = properties.get("jcr:title", String.class);
            featureId = properties.get("featureId", String.class);
            icon = properties.get("icon", String.class);
            featureCode = properties.get("featureCode", String.class);
            description = properties.get("jcr:description", String.class);

            name = tag.getName();
            path = tag.getPath();
        }
    }

    public String getTitle() {
        return title;
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

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof FeatureModel)) {
            return false;
        }

        final FeatureModel objFeatureModel = (FeatureModel) obj;

        return objFeatureModel.getPath().equals(getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, featureId, icon, featureCode, description, name, path);
    }
}
