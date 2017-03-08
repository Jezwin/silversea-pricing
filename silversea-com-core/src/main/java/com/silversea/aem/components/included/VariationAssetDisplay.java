package com.silversea.aem.components.included;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

public class VariationAssetDisplay extends WCMUsePojo {
    private Boolean hasPlanContent;
    private Boolean hasLocationContent;
    private Boolean hasVirtualTourContent;

    private String planLightboxPath;
    private String[] locationlightboxPaths;
    private String virualtourLightboxPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(VariationAssetDisplay.class);

    @Override
    public void activate() throws Exception {
        hasPlanContent = hasContent(getResource().getPath() + "/plan/image");
        hasLocationContent = hasContent(getResource().getPath() + "/locationImages/image");
        hasVirtualTourContent = hasContent(getResource().getPath() + "/virtualTour/image");
    }

    private Boolean hasContent(String path) {
        Resource res = getResourceResolver().resolve(path);
        if (res != null) {
            return StringUtils.isNotEmpty(res.getValueMap().get("fileReference", String.class));
        }
        return false;
    }

    /**
     * @return the hasPlanContent
     */
    public Boolean hasPlanContent() {
        return hasPlanContent;
    }

    /**
     * @return the hasLocationContent
     */
    public Boolean hasLocationContent() {
        return hasLocationContent;
    }

    /**
     * @return the hasVirtualTourContent
     */
    public Boolean hasVirtualTourContent() {
        return hasVirtualTourContent;
    }

    /**
     * @return the planLightboxPath
     */
    public String getPlanLightboxPath() {
        return planLightboxPath;
    }

    /**
     * @return the locationThumbnailPaths
     */
    public String[] getLocationThumbnailPaths() {
        return locationlightboxPaths;
    }

    /**
     * @return the virualtourLightboxPath
     */
    public String getVirualtourLightboxPath() {
        return virualtourLightboxPath;
    }
}