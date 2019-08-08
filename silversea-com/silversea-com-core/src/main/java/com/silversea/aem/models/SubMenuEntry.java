package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import java.util.Collections;
import java.util.List;

@Model(adaptables = Resource.class)
public class SubMenuEntry {

    private final String title;
    private final SilverseaAsset picture;
    private final List<ExternalLink> entries;

    public SubMenuEntry(ExternalLink link) {
        this.title = link.getLabel();
        this.picture = null;
        this.entries = Collections.singletonList(link);
    }

    public SubMenuEntry(String title, SilverseaAsset picture, List<ExternalLink> entries) {
        this.title = title;
        this.picture = picture;
        this.entries = entries;
    }

    public String getTitle() {
        return title;
    }

    public SilverseaAsset getPicture() {
        return picture;
    }

    public List<ExternalLink> getEntries() {
        return entries;
    }
}

