package com.silversea.aem.helper;

import java.util.ArrayList;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.google.common.collect.Lists;

public class ListHelper extends WCMUsePojo {

    private List<List<Object>> objectListGroup;
    private String[] objectListLoop;


    @Override
    public void activate() throws Exception {
        Integer size = get("size", Integer.class);
        List<Object> assetList = get("list", List.class);
        
        Integer sizeLoop = get("sizeLoop", Integer.class);


        if (size != null && assetList != null) {
            objectListGroup = Lists.partition(assetList, size);
        }
        
        if(sizeLoop != null) {
        	objectListLoop = new String[sizeLoop];
        }
        

    }

    /**
     * @return List of sub list
     */
    public List<List<Object>> getObjectListGroup() {
        return objectListGroup;
    }
    
    /**
     * @return List of sub list
     */
    public String[] getObjectListLoop() {
        return objectListLoop;
    }
}