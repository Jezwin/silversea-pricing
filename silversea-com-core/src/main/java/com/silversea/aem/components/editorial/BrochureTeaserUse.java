package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;

public class BrochureTeaserUse extends WCMUsePojo {

    private String brochurePath;

    private String assetDescription;

    private String assetTitle;

    private List assetThumbnailsList;

    private Asset asset;
    
    private String assetImage;

    @Override
    public void activate() throws Exception {
        
        brochurePath = getProperties().get("brochure", String.class);
        if (brochurePath != null) {
            Resource resourceDam = getResourceResolver().getResource(getProperties().get("brochure", ""));
            asset = resourceDam.adaptTo(Asset.class);
            
            assetThumbnailsList = getThumbnailsPathList(getRequest());
            
            assetTitle = asset.getMetadataValue(DamConstants.DC_TITLE);
            assetDescription = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
        } else {
            assetTitle = "";
            assetDescription = "";
        }

    }

    public List getThumbnailsPathList(SlingHttpServletRequest request) {
        
        List list = new ArrayList();
        for (int i = 0 ; i<asset.getRenditions().size() ; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(request.getRequestURL().toString().split("/content/")[0]);
            sb.append(asset.getRenditions().get(i).getPath());
            list.add(sb.toString());
        }
        return list;
    }
    
    public String getAssetImage() {
        return assetThumbnailsList.get(1).toString(); //"http://localhost:4502" + asset.getRenditions().get(0).getPath();
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
}
