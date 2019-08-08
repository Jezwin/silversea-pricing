package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.BrochureModel;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrochureTeaserUse extends WCMUsePojo {

    static final private Logger LOGGER = LoggerFactory.getLogger(BrochureTeaserUse.class);

    private BrochureModel brochure;

    private boolean showOnlineRequestForm;
    private boolean showPrintedRequestForm;
    private String onlineRequestFormURL;
    private String printedRequestForm;

    @Override
    public void activate() throws Exception {
        // TODO set the currentstyle param.
        showOnlineRequestForm = false;
        showPrintedRequestForm = false;
        onlineRequestFormURL = "";
        printedRequestForm = "";

        // Initialize the brochure model
        final String brochurePath = getProperties().get(WcmConstants.PN_FILE_REFERENCE, String.class);

        if (brochurePath != null) {
            final Resource assetResource = getResourceResolver().getResource(brochurePath);

            if (assetResource != null) {
                LOGGER.debug("Found asset {}", assetResource.getPath());

                Asset asset = assetResource.adaptTo(Asset.class);

                if (asset != null) {
                    LOGGER.debug("Adapting asset {} to BrochureModel", asset.getPath());

                    brochure = asset.adaptTo(BrochureModel.class);
                }
            }
        }
    }

    public BrochureModel getBrochure() {
        return brochure;
    }

    public boolean isShowOnlineRequestForm() {
        return showOnlineRequestForm;
    }

    public boolean isShowPrintedRequestForm() {
        return showPrintedRequestForm;
    }

    public String getOnlineRequestFormURL() {
        return onlineRequestFormURL;
    }

    public String getPrintedRequestForm() {
        return printedRequestForm;
    }
}