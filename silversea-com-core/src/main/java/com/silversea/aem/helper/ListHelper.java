package com.silversea.aem.helper;

import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.google.common.collect.Lists;

public class ListHelper extends WCMUsePojo {

    private List<List<Asset>> assetListGroup;

    @Override
    public void activate() throws Exception {
        Integer size = get("size", Integer.class);
        List<Asset> assetList = get("list", List.class);

        if(size != null && assetList != null) {
            assetListGroup = Lists.partition(assetList, 5);
        }

    }

    /**
     * @return List of sub list
     */
    public List<List<Asset>> getAssetListGroup() {
        return assetListGroup;
    }
}