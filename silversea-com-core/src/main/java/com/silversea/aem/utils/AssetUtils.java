package com.silversea.aem.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.resource.collection.ResourceCollection;

import com.day.cq.dam.api.Asset;


/**
 * Utils class for Assets.
 */
public class AssetUtils {

    /**
     * Constructor.
     */
    private AssetUtils() {

    }

    /**
     * Return the Asset list of a media set
     */
    public static List<Asset> buildAssetList(String setPath, ResourceResolver resourceResolver) {
        // Dynamic Media Image Set
        Resource members = resourceResolver.getResource(setPath + "/jcr:content/related/s7Set");
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
}
