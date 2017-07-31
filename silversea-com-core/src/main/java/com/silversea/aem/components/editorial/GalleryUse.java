package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.commons.json.JSONObject;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.silversea.aem.utils.AssetUtils;

public class GalleryUse extends WCMUsePojo {

    private Map<String, List<Asset>> categoryMap;

    @Override
    public void activate() throws Exception {
        // Create map<String title, String path> from multifields properties
        final String[] category = getProperties().get("category", String[].class);
        if (category != null) {
            JSONObject jsonObject;
            categoryMap = new LinkedHashMap<String, List<Asset>>();
            String categoryTitle;
            List<Asset> categoryAssets = new ArrayList<Asset>();

            for (String gallery : category) {
                jsonObject = new JSONObject(gallery);

                categoryTitle = jsonObject.getString("title");
                categoryAssets = AssetUtils.buildAssetList(jsonObject.getString("assetReference"), getResourceResolver());
                categoryMap.put(categoryTitle, categoryAssets);
            }

        }
    }

    /**
     * @return the categoryMap
     */
    public Map<String, List<Asset>> getCategoryMap() {
        return categoryMap;
    }
}