package com.silversea.aem.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.resource.collection.ResourceCollection;

import com.adobe.cq.sightly.WCMUsePojo;

public class ImageSetHelper extends WCMUsePojo {
    private List<String> renditionList;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);
        if (path != null) {
            renditionList = renditionPathList(path);
        }

    }

    private List<String> renditionPathList(String setPath) {
        // Dynamic Media Image Set
        Resource members = getResourceResolver().resolve(setPath + "/jcr:content/related/s7Set");
        if (members != null) {
            ResourceCollection membersCollection = members.adaptTo(ResourceCollection.class);

            Iterator<Resource> it = membersCollection.getResources();

            List<String> renditionList = new ArrayList<String>();
            while (it.hasNext()) {
                Resource res = it.next();
                renditionList.add(res.getPath());
            }
            return renditionList;
        }

        return null;
    }

    /**
     * @return the renditionList
     */
    public List<String> getRenditionList() {
        return renditionList;
    }
}