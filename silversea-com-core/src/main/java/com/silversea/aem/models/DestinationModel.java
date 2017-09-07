package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Page.class)
public class DestinationModel {

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/mapLabel") @Optional
    private String mapLabel;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/excerpt") @Optional
    private String excerpt;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/destinationId") @Optional
    private String destinationId;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION) @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/footnote") @Optional
    private String footnote;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetselectionreference") @Optional
    private String assetselectionreference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/category") @Optional
    private String category;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/customHtml") @Optional
    private String customHtml;

    private String path;

    private String name;

    @PostConstruct
    private void init() {
        path = page.getPath();
        name = page.getName();
    }

    public String getTitle() {
        return title;
    }

    public String getMapLabel() {
        return mapLabel;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getFootnote() {
        return footnote;
    }

    public String getAssetselectionreference() {
        return assetselectionreference;
    }

    public String getCategory() {
        return category;
    }

    public String getCustomHtml() {
        return customHtml;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DestinationModel)) {
            return false;
        }

        final DestinationModel objDestinationModel = (DestinationModel)obj;

        return objDestinationModel.getPath().equals(getPath());
    }
}
