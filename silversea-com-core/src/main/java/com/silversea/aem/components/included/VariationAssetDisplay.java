package com.silversea.aem.components.included;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.resource.collection.ResourceCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;

public class VariationAssetDisplay extends WCMUsePojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(VariationAssetDisplay.class);

    @Override
    public void activate() throws Exception {
    }

    /**
     * @return image path
     */
    private String renditionPath(String componentPath) {
        // Check if the node is created
        Resource componentRes = getResourceResolver().resolve(getResource().getPath() + componentPath);
        if (componentRes != null) {
            String imagePath = componentRes.getValueMap().get("fileReference", String.class);
            if (StringUtils.isNotEmpty(imagePath)) {
                Resource assetResource = getResourceResolver().getResource(imagePath);
                // Check if the asset exist
                if (assetResource != null) {
                    Asset asset = assetResource.adaptTo(Asset.class);
                    Node assetNode = assetResource.adaptTo(Node.class);

                    String assetType = null;
                    String thumbnailPath = "";
                    try {
                        if (assetNode.hasProperty(S7damConstants.S7_ASSET_METADATA_NODE + "/" + S7damConstants.PN_S7_TYPE)) {
                            assetType = assetNode.getProperty(S7damConstants.S7_ASSET_METADATA_NODE + "/" + S7damConstants.PN_S7_TYPE).getString();
                        }
                    } catch (RepositoryException e) {
                        LOGGER.error("Exception, asset node not find", e);
                    }

                    if (assetType.equals(S7damConstants.S7_IMAGE_SET)) {
                        // Dynamic Media Image Set
                        Resource members = getResourceResolver().resolve(asset.getPath() + "/jcr:content/related/s7Set");
                        ResourceCollection membersCollection = members.adaptTo(ResourceCollection.class);
                        // Use the first member for thumbnail
                        thumbnailPath = membersCollection.getResources().next().getPath();
                    } else if (assetType.equals(S7damConstants.S7_IMAGE)) {
                        // Dynamic Media Image
                        thumbnailPath = asset.getImagePreviewRendition().getPath();
                    }
                    return thumbnailPath;
                }
            }
        }
        return "";
    }

    /**
     * @return the plan image path
     */
    public String getPlanImageRenditionPath() {
        return renditionPath("/plan/image");
    }

    /**
     * @return the location image path
     */
    public String getLocationImageRenditionPath() {
        return renditionPath("/locationImages/image");
    }

    /**
     * @return the virtual tour image path
     */
    public String getVirtualtourImageRenditionPath() {
        return renditionPath("/virtualTour/image");
    }
}