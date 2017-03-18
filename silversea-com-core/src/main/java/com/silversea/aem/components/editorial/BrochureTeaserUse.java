package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.RenditionPicker;
import com.day.cq.dam.commons.util.PrefixRenditionPicker;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.List;

public class BrochureTeaserUse extends WCMUsePojo {

    private String brochurePath;
    private Asset asset;

    private List<String> assetThumbnailsList;

    private boolean showOnlineRequestForm;
    private boolean showPrintedRequestForm;
    private String onlineRequestFormURL;
    private String printedRequestForm;

    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();

        brochurePath = properties.get(WcmConstants.PN_FILE_REFERENCE, String.class);

        // TODO set the currentstyle param.
        showOnlineRequestForm = false;
        showPrintedRequestForm = false;
        onlineRequestFormURL = "";
        printedRequestForm = "";

        if (brochurePath != null) {
            Resource assetResource = getResourceResolver().getResource(brochurePath);
            asset = assetResource.adaptTo(Asset.class);
        }
    }

    public String getCover() {
        if (asset != null) {
            RenditionPicker renditionPicker = new PrefixRenditionPicker("cover");
            Rendition rendition = asset.getRendition(renditionPicker);

            return rendition.getPath();
        }

        return null;
    }

    public String getBrochurePath() {
        return brochurePath;
    }

    public String getAssetTitle() {
        return asset != null ? asset.getMetadataValue(DamConstants.DC_TITLE) : null;
    }

    public String getAssetDescription() {
        return asset != null ? asset.getMetadataValue(DamConstants.DC_DESCRIPTION) : null;
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