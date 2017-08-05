package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.silversea.aem.utils.AssetUtils;
import org.apache.sling.commons.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GalleryUse extends WCMUsePojo {

    private Map<String, List<Asset>> categoryMap;

    @Override
    public void activate() throws Exception {
        // Create map<String title, String path> from multifields properties
        final String[] category = getProperties().get("category", String[].class);

        if (category != null) {
            categoryMap = new LinkedHashMap<>();

            for (String gallery : category) {
                JSONObject jsonObject = new JSONObject(gallery);

                String categoryTitle = jsonObject.getString("title");
                List<Asset> categoryAssets = AssetUtils.buildAssetList(jsonObject.getString("assetReference"), getResourceResolver());
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