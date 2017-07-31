package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.silversea.aem.utils.AssetUtils;

import java.util.List;

public class ImageSetHelper extends WCMUsePojo {
    private List<Asset> renditionList;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);

        if (path != null) {
            renditionList = AssetUtils.buildAssetList(path, getResourceResolver());
        }
    }

    /**
     * @return the renditionList
     */
    public List<Asset> getRenditionList() {
        return renditionList;
    }
}