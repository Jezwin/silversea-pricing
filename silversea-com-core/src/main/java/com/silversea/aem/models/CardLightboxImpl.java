package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class CardLightboxImpl implements CardLightbox {

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
    private Boolean noModal;


    @Inject
    @Optional
    private String assetSelection;

    public CardLightboxImpl() {
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getBriefDescription() {
        return briefDescription;
    }

    @Override
    public String getAssetSelection() {
        return assetSelection;
    }

    @Override
    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public String getBigPicture() {
        return bigPicture;
    }


    @Override
    public boolean getNoModal() {
        return noModal != null && noModal;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setBigPicture(String bigPicture) {
        this.bigPicture = bigPicture;
    }

    public void setNoModal(Boolean noModal) {
        this.noModal = noModal;
    }

    public void setAssetSelection(String assetSelection) {
        this.assetSelection = assetSelection;
    }
}
