package com.silversea.aem.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.resource.collection.ResourceCollection;

import com.day.cq.dam.api.Asset;
import com.silversea.aem.models.ShipAreaModel;
import com.silversea.aem.models.SilverseaAsset;

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

    public static Map<String,List<SilverseaAsset>> addAllShipAreaAssets(final ResourceResolver resourceResolver, final List<? extends ShipAreaModel> shipAreas) {
    	Map<String,List<SilverseaAsset>> result = new HashMap<>();
        List<SilverseaAsset> assets = new ArrayList<>();
        List<SilverseaAsset> assetsVirtualTour = new ArrayList<>();

        for (ShipAreaModel shipArea : shipAreas) {
            for (Asset item : shipArea.getAssets()) {
            	SilverseaAsset sscAsset =  new SilverseaAsset();
            	sscAsset.setPath(item.getPath());
            	sscAsset.setName(item.getName());
            	sscAsset.setLabel(shipArea.getTitle());
                assets.add(sscAsset);
            }
            if (StringUtils.isNotBlank(shipArea.getVirtualTour())) {
                Resource virtualTourResource = resourceResolver.getResource(shipArea.getVirtualTour());
                if (virtualTourResource != null) {     
					Asset vAsset = virtualTourResource.adaptTo(Asset.class);
                	SilverseaAsset sscAssetVirtT =  new SilverseaAsset();
                	sscAssetVirtT.setPath(vAsset.getPath());
                    assetsVirtualTour.add(sscAssetVirtT);
                }
            }
        }
        
        result.put("assets", assets);
        result.put("assetsVirtualTour", assetsVirtualTour);

        return result;
    }
}
