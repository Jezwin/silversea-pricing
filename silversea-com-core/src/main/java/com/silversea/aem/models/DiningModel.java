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

/**
 * TODO remove unused/local variables
 * TODO add plan and location image
 */
@Model(adaptables = Page.class)
public class DiningModel  implements ShipAreaModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(DiningModel.class);

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

    @Inject @Named(JcrConstants.JCR_CONTENT + "/virtualTour") @Optional
    private String virtualTour;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/diningReference") @Optional
    private String diningReference;

    // TODO replace by an injector
    private DiningModel genericDining;

    @PostConstruct
    private void init() {
        final PageManager pageManager = page.getPageManager();

        // init reference
        if (diningReference != null) {
            final Page genericDiningPage = pageManager.getPage(diningReference);

            if (genericDiningPage != null) {
                genericDining = genericDiningPage.adaptTo(DiningModel.class);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public String getLongDescription() {
        return longDescription != null ? longDescription :
                (genericDining != null ? genericDining.getLongDescription() : null);
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference != null ? assetSelectionReference :
                (genericDining != null ? genericDining.getAssetSelectionReference() : null);
    }

    public String getThumbnail() {
        return thumbnail != null ? thumbnail :
                (genericDining != null ? genericDining.getThumbnail() : null);
    }

    public String getVirtualTour() {
        return virtualTour;
    }

    public Page getPage() {
        return page;
    }
}
