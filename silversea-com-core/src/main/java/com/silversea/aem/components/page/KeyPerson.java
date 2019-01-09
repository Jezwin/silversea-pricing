package com.silversea.aem.components.page;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.CardLightbox;
import org.apache.sling.api.resource.ResourceResolver;

import static java.util.Optional.ofNullable;

public class KeyPerson implements CardLightbox {

    private final String name;
    private final String description;
    private final String profession;
    private final Asset image;

    public KeyPerson(Page page, ResourceResolver resourceResolver) {
        this.name = page.getTitle();
        this.description = page.getDescription();
        this.profession = page.getProperties().get("profession", String.class);
        this.image = ofNullable(page.getContentResource())
                .map(res -> res.getChild("image"))
                .map(res -> res.getChild("fileReference"))
                .map(res -> res.adaptTo(String.class))
                .map(resourceResolver::getResource)
                .map(res -> res.adaptTo(Asset.class)).orElse(null);
    }

    public String getProfession() {
        return profession;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Asset getImage() {
        return image;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getBriefDescription() {
        return profession;
    }

    @Override
    public String getAssetSelection() {
        return image.getPath();
    }

    @Override
    public String getLongDescription() {
        return description;
    }

    @Override
    public String getThumbnail() {
        return image.getPath();
    }

    @Override
    public String getBigPicture() {
        return image.getPath();
    }
}
