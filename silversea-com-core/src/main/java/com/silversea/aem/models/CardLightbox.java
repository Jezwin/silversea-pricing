package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class CardLightbox {

    @Inject
    private String title;

    @Inject
    @Optional
    private String briefDescription;

    @Inject
    @Optional
    private String longDescription;

    @Inject
    @Optional
    private String thumbnail;

    @Inject
    @Optional
    private String bigPicture;

    @Inject
    @Optional
    private String assetSelection;

    public String getTitle() {
        return title;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public String getAssetSelection() {
        return assetSelection;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getBigPicture() {
        return bigPicture;
    }

}
