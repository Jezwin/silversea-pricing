package com.silversea.aem.components.included;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.resource.collection.ResourceCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;

public class VariationAssetDisplayUse extends WCMUsePojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(VariationAssetDisplayUse.class);

    @Override
    public void activate() throws Exception {
    }

    /**
     * @return image path
     */
    private Asset getAsset(String componentPath) {
        // Check if the node is created
        Resource componentRes = getResourceResolver().resolve(getResource().getPath() + componentPath);
        if (componentRes != null) {
            String imagePath = componentRes.getValueMap().get("fileReference", String.class);
            if (StringUtils.isNotEmpty(imagePath)) {
                Resource assetResource = getResourceResolver().getResource(imagePath);
                // Check if the asset exist
                if (assetResource != null) {
                    return assetResource.adaptTo(Asset.class);
                }
            }
        }
        return null;
    }

    private List<String> renditionPathList(String componentPath) {
        List<String> renditionList = new ArrayList<String>();

        // Dynamic Media Image Set
        Resource members = getResourceResolver().resolve(getAsset(componentPath).getPath() + "/jcr:content/related/s7Set");
        ResourceCollection membersCollection = members.adaptTo(ResourceCollection.class);
        Iterator<Resource> it = membersCollection.getResources();

        while (it.hasNext()) {
            renditionList.add(it.next().getPath());
        }

        return renditionList;
    }

    private String renditionPath(String componentPath) {
        // Dynamic Media Image
        if(getAsset(componentPath) != null) {
            return getAsset(componentPath).getPath();
        }

        return null;
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
    public List<String> getLocationImageRenditionPath() {
        return renditionPathList("/locationImages/image");
    }

    /**
     * @return the virtual tour image path
     */
    public String getVirtualtourImageRenditionPath() {
        return renditionPath("/virtualTour/image");
    }
}