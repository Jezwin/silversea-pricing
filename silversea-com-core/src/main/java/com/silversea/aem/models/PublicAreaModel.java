package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Page.class)
public class PublicAreaModel implements ShipAreaModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(PublicAreaModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE) @Optional
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String assetSelectionReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/image/fileReference") @Optional
    private String thumbnail;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/publicAreaReference") @Optional
    private String publicAreaReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/virtualTour") @Optional
    private String virtualTour;

    private SuiteModel genericSuite;

    @PostConstruct
    private void init() {
        final PageManager pageManager = page.getPageManager();

        // init reference
        if (publicAreaReference != null) {
            final Page genericSuitePage = pageManager.getPage(publicAreaReference);

            if (genericSuitePage != null) {
                genericSuite = genericSuitePage.adaptTo(SuiteModel.class);
            }
        }
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getTitle() {
        return title != null ? title :
                (genericSuite != null ? genericSuite.getTitle() : null);
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference != null ? assetSelectionReference :
                (genericSuite != null ? genericSuite.getAssetSelectionReference() : null);
    }

    public String getThumbnail() {
        return thumbnail != null ? thumbnail :
                (genericSuite != null ? genericSuite.getThumbnail() : null);
    }

    public String getVirtualTour() {
        return virtualTour;
    }

    public Page getPage() {
        return page;
    }
}
