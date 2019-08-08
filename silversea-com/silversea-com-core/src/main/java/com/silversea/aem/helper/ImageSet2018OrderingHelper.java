package com.silversea.aem.helper;

import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.silversea.aem.utils.AssetUtils;

public class ImageSet2018OrderingHelper extends WCMUsePojo {
    private List<Asset> renditionList;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);

        if (path != null) {
            renditionList = AssetUtils.buildAssetList2018Order(path, getResourceResolver());
        }
    }

    /**
     * @return the renditionList
     */
    public List<Asset> getRenditionList() {
        return renditionList;
    }
    
    public String getRenditionListString() {
    	StringBuilder listAsset = new StringBuilder();
    	if (renditionList != null) {
    		for(int i=0; i < renditionList.size(); i++) {
    			listAsset.append(renditionList.get(i).getPath());
    			if (i < renditionList.size() -1) {
    				listAsset.append("#next#");
    			}
    		}
    	}
        return listAsset.toString();
    }
    
    public String getMetadataResourceListString() {
    	String metadata = get("metadata", String.class);
    	StringBuilder listAsset = new StringBuilder();
    	if (renditionList != null) {
    		for(int i=0; i < renditionList.size(); i++) {
    			String assetPath = renditionList.get(i).getPath();
    			Resource res = getResourceResolver().getResource(assetPath + "/" + JcrConstants.JCR_CONTENT + "/metadata");
    			if (res != null && res.getValueMap() != null) {
    				String properyValue =  res.getValueMap().get(metadata, String.class);
    				if(properyValue != null) {
    					listAsset.append(properyValue);
    					if (i < renditionList.size() -1) {
    						listAsset.append("#next#");
    					}
    				}	
    			}
    		}
    	}
        return listAsset.toString();
    }
}

