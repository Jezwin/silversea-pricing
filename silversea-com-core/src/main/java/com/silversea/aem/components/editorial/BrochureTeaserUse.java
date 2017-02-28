package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;

public class BrochureTeaserUse extends WCMUsePojo {

    private static final String BROCHURE_PROPERTY = "brochure";

    private String brochurePath;

    private String assetDescription;

    private String assetTitle;

    private List<String> assetThumbnailsList;

    private Asset asset;

    private boolean showOnlineRequestForm;

    private boolean showPrintedRequestForm;

    private String onlineRequestFormURL;

    private String printedRequestForm;

    @Override
    public void activate() throws Exception {

        if (get("brochurePathParam", String.class) != null) {
            getRequest().setAttribute(BROCHURE_PROPERTY, get("brochurePathParam", String.class));
        }
        if (getRequest().getAttribute(BROCHURE_PROPERTY) != null) {
            brochurePath = getRequest().getAttribute(BROCHURE_PROPERTY).toString();

            // TODO set the currentstyle param.
            showOnlineRequestForm = false;
            showPrintedRequestForm = false;
            onlineRequestFormURL = "";
            printedRequestForm = "";
        } else {
            brochurePath = getProperties().get(BROCHURE_PROPERTY, String.class);
        }
        if (brochurePath != null) {
            Resource resourceDam = getResourceResolver().getResource(brochurePath);
            asset = resourceDam.adaptTo(Asset.class);

            /* TODO replace by dynamic media */
            assetThumbnailsList = getThumbnailsPathList(getRequest());

            assetTitle = asset.getMetadataValue(DamConstants.DC_TITLE);
            assetDescription = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
        } else {
            assetTitle = "";
            assetDescription = "";
        }

    }

    /* TODO replace by dynamic media */
    public List<String> getThumbnailsPathList(SlingHttpServletRequest request) {

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < asset.getRenditions().size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(request.getRequestURL().toString().split("/content/")[0]);
            sb.append(asset.getRenditions().get(i).getPath());
            list.add(sb.toString());
        }
        return list;
    }

    public List<String> getAssetThumbnailsList() {
        return assetThumbnailsList;
    }

    public String getAssetImage() {
        // "http://localhost:4502" + asset.getRenditions().get(0).getPath();
        return assetThumbnailsList.get(1).toString();
    }

    public String getBrochurePath() {
        return brochurePath;
    }

    public String getAssetDescription() {
        return assetDescription;
    }

    public String getAssetTitle() {
        return assetTitle;
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