package com.silversea.aem.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.tagging.Tag;

@Model(adaptables = Tag.class)
public class TagModel {

    @Inject @Self
    private Tag tag;

    private Resource resource;

    @PostConstruct
    private void init() {
        // Need resource for extra properties (icon, description, ...)
        resource = tag.adaptTo(Resource.class);
    }

    /**
     * @return the tag
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * @return the resource
     */
    public Resource getResource() {
        return resource;
    }
}