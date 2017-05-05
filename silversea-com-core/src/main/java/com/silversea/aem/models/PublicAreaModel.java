package com.silversea.aem.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

@Model(adaptables = Page.class)
public class PublicAreaModel {

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    @Optional
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    private ResourceResolver resourceResolver;

    private String thumbnail;

    @PostConstruct
    private void init() {
        resourceResolver = page.getContentResource().getResourceResolver();
        title = initProperty("publicAreaReference", title, "title");
        longDescription = initProperty("publicAreaReference", longDescription, "longDescription");
        assetSelectionReference = initProperty("publicAreaReference", assetSelectionReference,
                "assetSelectionReference");
        thumbnail = page.getProperties().get("image/fileReference", String.class);
    }

    private String initProperty(String reference, String property, String referenceProperty) {
        String value = property;
        if (StringUtils.isEmpty(property)) {
            Page page = getPageReference(reference);
            if (page != null) {
                value = page.getProperties().get(referenceProperty, String.class);
            }

        }
        return value;
    }

    private Page getPageReference(String reference) {
        Page pageReference = null;
        String path = page.getProperties().get(reference, String.class);
        Resource resource = resourceResolver.resolve(path);
        if (resource != null) {
            pageReference = resource.adaptTo(Page.class);
        }

        return pageReference;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getTitle() {
        return title;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    public Page getPage() {
        return page;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
