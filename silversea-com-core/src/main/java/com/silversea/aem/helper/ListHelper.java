package com.silversea.aem.helper;

import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.google.common.collect.Lists;

public class ListHelper extends WCMUsePojo {

    private List<List<Object>> objectListGroup;

    @Override
    public void activate() throws Exception {
        Integer size = get("size", Integer.class);
        List<Object> assetList = get("list", List.class);

        if (size != null && assetList != null) {
            objectListGroup = Lists.partition(assetList, size);
        }

    }

    /**
     * @return List of sub list
     */
    public List<List<Object>> getObjectListGroup() {
        return objectListGroup;
    }
}