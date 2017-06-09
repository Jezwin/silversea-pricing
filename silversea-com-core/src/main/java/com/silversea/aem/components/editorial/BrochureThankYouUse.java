package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.silversea.aem.models.BrochureModel;

/**
 *
 */
public class BrochureThankYouUse extends WCMUsePojo {

    static final private Logger LOGGER = LoggerFactory.getLogger(BrochureThankYouUse.class);

    private BrochureModel brochure;

    @Override
    public void activate() throws Exception {
    	// Initialize the brochure model
        // The requested brochure will be obtained in the URL suffix
        String brochurePath = getRequest().getRequestPathInfo().getSuffix();
        if (brochurePath != null) {
            Resource assetResource = getResourceResolver().getResource(brochurePath);
            Asset asset = assetResource.adaptTo(Asset.class);

            LOGGER.debug("Found asset {}", asset.getPath());

            if (asset != null) {
                LOGGER.debug("Adapting asset {} to BrochureModel", asset.getPath());

                brochure = asset.adaptTo(BrochureModel.class);
            }
        }
    }

    public BrochureModel getBrochure() {
        return brochure;
    }
}