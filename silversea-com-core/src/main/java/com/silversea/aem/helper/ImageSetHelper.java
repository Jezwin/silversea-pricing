package com.silversea.aem.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.resource.collection.ResourceCollection;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.google.common.collect.Lists;

public class ImageSetHelper extends WCMUsePojo {
    private List<Asset> renditionList;
    private List<List<Asset>> renditionListByGroup;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);
        Integer size = get("size", Integer.class);

        if (path != null) {
            renditionList = buildRenditionPathList(path);
        }

        if (renditionList != null && size != null) {
            renditionListByGroup = Lists.partition(renditionList, size);
        }
    }

    private List<Asset> buildRenditionPathList(String setPath) {
        // Dynamic Media Image Set
        Resource members = getResourceResolver().getResource(setPath + "/jcr:content/related/s7Set");
        if (members != null) {
            ResourceCollection membersCollection = members.adaptTo(ResourceCollection.class);

            Iterator<Resource> it = membersCollection.getResources();

            List<Asset> renditionList = new ArrayList<Asset>();
            while (it.hasNext()) {
                Resource res = it.next();
                renditionList.add(res.adaptTo(Asset.class));
            }
            return renditionList;
        }

        return null;
    }

    /**
     * @return the renditionList
     */
    public List<Asset> getRenditionList() {
        return renditionList;
    }

    /**
     * @return the renditionListByGroup
     */
    public List<List<Asset>> getRenditionListByGroup() {
        return renditionListByGroup;
    }
}