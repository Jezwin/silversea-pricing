package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.silversea.aem.components.services.GeolocationTagCacheService;

public class BrochureTeaserListUse extends WCMUsePojo {

    private List<Map> brochureProperties;

    private String debugTest;
    
    private Map testTag;

    private GeolocationTagCacheService geolocService;
    
    @Override
    public void activate() throws Exception {

        geolocService = getSlingScriptHelper().getService(GeolocationTagCacheService.class);
        
        testTag = geolocService.getTags(getResourceResolver());
        
        Resource resourceDam = getResourceResolver().getResource("/content/dam/siversea-com/brochures");
        Node node = resourceDam.adaptTo(Node.class);
        NodeIterator iterator = node.getNodes();   

        brochureProperties = new ArrayList<Map>();

        while (iterator.hasNext()) {
            Node currentNode = (Node) iterator.next();
            if(currentNode.getPath().endsWith(".pdf")) {
                
                Resource assetResource = getResourceResolver().getResource(currentNode.getPath());
                Asset asset = assetResource.adaptTo(Asset.class);
                Map currentMap = new HashMap<>();
                currentMap.put("title", asset.getMetadataValue(DamConstants.DC_TITLE));
                currentMap.put("description", asset.getMetadataValue(DamConstants.DC_DESCRIPTION));
                if (asset.getImagePreviewRendition() != null)
                    currentMap.put("imgpath", asset.getImagePreviewRendition().getPath());
                else 
                    currentMap.put("imgpath", "");
                /*
                Resource metadataResource = assetResource.getChild("jcr:content/metadata");
                ValueMap prop = ResourceUtil.getValueMap(metadataResource);
                Map currentMap = new HashMap<>();
                currentMap.put("title", prop.get(DamConstants.DC_TITLE, String.class));
                currentMap.put("description", prop.get(DamConstants.DC_DESCRIPTION, String.class));
                */
                brochureProperties.add(currentMap);
            }
        }
    }

    public Map getTestTag() {
        return testTag;
    }
    
    public String getDebugTest() {
        return debugTest;
    }
    
    public List<Map> getBrochureProperties() {
        return brochureProperties;
    }
}
