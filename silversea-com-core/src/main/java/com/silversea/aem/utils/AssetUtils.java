package com.silversea.aem.utils;

import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.resource.collection.ResourceCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utils class for Assets.
 */
public class AssetUtils {

    /**
     * @param setPath path of the asset which is a Media Set
     * @param resourceResolver the resource resolver
     * @return the Asset list of a media set
     */
    public static List<Asset> buildAssetList(String setPath, ResourceResolver resourceResolver) {
        List<Asset> renditionList = new ArrayList<>();

        // Dynamic Media Image Set
        final Resource members = resourceResolver.getResource(setPath + "/jcr:content/related/s7Set");

        if (members != null) {
            final ResourceCollection membersCollection = members.adaptTo(ResourceCollection.class);

            if (membersCollection != null) {
                final Iterator<Resource> it = membersCollection.getResources();

                while (it.hasNext()) {
                    final Asset asset = it.next().adaptTo(Asset.class);

                    if (asset != null) {
                        renditionList.add(asset);
                    }
                }
            }
        }

        return renditionList;
    }
}
