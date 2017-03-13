package com.silversea.aem.components.included;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;

public class VariationAssetDisplay extends WCMUsePojo {

    @Override
    public void activate() throws Exception {
    }

    /**
     * @return image path
     */
    private String renditionLightboxPath(String componentPath) {
        Resource componentRes = getResourceResolver().resolve(getResource().getPath() + componentPath);
        if (componentRes != null) {
            String imagePath = componentRes.getValueMap().get("fileReference", String.class);
            if (StringUtils.isNotEmpty(imagePath)) {
                Asset asset;
                Resource resAsset = getResourceResolver().getResource(imagePath);
                if (resAsset != null) {
                    asset = resAsset.adaptTo(Asset.class);
                    return asset.getImagePreviewRendition().getPath();
                }
            }
        }
        return "";
    }

    /**
     * @return the plan image path
     */
    public String getPlanLightboxPath() {
        return renditionLightboxPath("/plan/image");
    }

    /**
     * @return the location image path
     */
    public String getLocationLightboxPath() {
        return renditionLightboxPath("/locationImages/image");
    }

    /**
     * @return the virtual tour image path
     */
    public String getVirualtourLightboxPath() {
        return renditionLightboxPath("/virtualTour/image");
    }
}