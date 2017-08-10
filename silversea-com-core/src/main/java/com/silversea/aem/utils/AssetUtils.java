package com.silversea.aem.utils;

import com.day.cq.dam.api.Asset;
import com.silversea.aem.models.ShipAreaModel;
import org.apache.commons.lang3.StringUtils;
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

    public static List<Asset> addAllShipAreaAssets(final ResourceResolver resourceResolver, final List<? extends ShipAreaModel> shipAreas) {
        List<Asset> assets = new ArrayList<>();

        for (ShipAreaModel shipArea : shipAreas) {
            if (StringUtils.isNotBlank(shipArea.getThumbnail())) {
                final Resource thumbnailResource = resourceResolver.getResource(shipArea.getThumbnail());

                if (thumbnailResource != null) {
                    final Asset thumbnailAsset = thumbnailResource.adaptTo(Asset.class);

                    if (thumbnailAsset != null) {
                        assets.add(thumbnailAsset);
                    }
                }
            }

            if (StringUtils.isNotBlank(shipArea.getAssetSelectionReference())) {
                assets.addAll(buildAssetList(shipArea.getAssetSelectionReference(), resourceResolver));
            }

            if (StringUtils.isNotBlank(shipArea.getVirtualTour())) {
                final Resource virtualTourResource = resourceResolver.getResource(shipArea.getVirtualTour());
                if (virtualTourResource != null) {
                    assets.add(virtualTourResource.adaptTo(Asset.class));
                }
            }
        }

        return assets;
    }
}
