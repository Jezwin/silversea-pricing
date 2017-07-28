package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;

public class AssetHelper extends WCMUsePojo {
    private String assetType;

    @Override
    public void activate() throws Exception {
        String assetPath = get("assetPath", String.class);
         if (StringUtils.isNotBlank(assetPath)) {
             Resource res = getResourceResolver().getResource(assetPath + "/" + JcrConstants.JCR_CONTENT);
             if (res != null) {
                 assetType = res.getValueMap().get(S7damConstants.PN_S7_TYPE, String.class);
             }
         }
    }

    /**
     * @return the assetType
     */
    public String getAssetType() {
        return assetType;
    }
}