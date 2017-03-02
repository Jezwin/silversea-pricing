/**
 * 
 */
package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;

/**
 * @author mjedli
 *
 */
public class DiningVariationAssetUse extends WCMUsePojo {

    private static final String LOCATION_IMAGE = "locationImage";

    private static final String VIRTUAL_TOUR = "virtualTour";

    private static final String PLAN = "plan";

    private Asset asset;

    private String assetTitle;

    private List<String> assetThumbnailsList;

    @Override
    public void activate() throws Exception {

        String currentPath = "";

        if ("".equals(currentPath) && getCurrentPage().getProperties().get(PLAN) != null
                && !"".equals(getCurrentPage().getProperties().get(PLAN))) {
            currentPath = getCurrentPage().getProperties().get(PLAN).toString();
        }

        if ("".equals(currentPath) && getCurrentPage().getProperties().get(VIRTUAL_TOUR) != null
                && !"".equals(getCurrentPage().getProperties().get(VIRTUAL_TOUR))) {
            currentPath = getCurrentPage().getProperties().get(VIRTUAL_TOUR).toString();
        }

        if ("".equals(currentPath) && getCurrentPage().getProperties().get(LOCATION_IMAGE) != null
                && !"".equals(getCurrentPage().getProperties().get(VIRTUAL_TOUR))) {
            currentPath = getCurrentPage().getProperties().get(LOCATION_IMAGE).toString();
        }

        if (!"".equals(currentPath)) {
            Resource resourceDam = getResourceResolver().getResource(currentPath);
            asset = resourceDam.adaptTo(Asset.class);
            assetTitle = asset.getMetadataValue(DamConstants.DC_TITLE);
            assetThumbnailsList = getThumbnailsPathList(getRequest());
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

    public String getAssetTitle() {
        return assetTitle;
    }

}
