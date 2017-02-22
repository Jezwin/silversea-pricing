package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.DamConstants;

public class BrochureTeaserListUse extends WCMUsePojo {

    private Map brochureProperties;
    
    private String debugTest;
    
    @Override
    public void activate() throws Exception {
        
        Resource resourceDam = getResourceResolver().getResource("/content/dam/siversea-com/brochures");
        Node node = resourceDam.adaptTo(Node.class);
        NodeIterator iterator = node.getNodes();
        
        brochureProperties = new HashMap<String, String>();
        
        while (iterator.hasNext()) {
            Node currentNode = (Node) iterator.next();
            Resource assetResource = getResourceResolver().getResource(currentNode.getPath());
            if(currentNode.getPath().endsWith(".pdf")) {
                Resource metadataResource = assetResource.getChild("jcr:content/metadata");
                ValueMap prop = ResourceUtil.getValueMap(metadataResource);
                brochureProperties.put("title", prop.get(DamConstants.DC_TITLE, String.class));
                brochureProperties.put("description", prop.get(DamConstants.DC_DESCRIPTION, String.class));
            }
        }
    }
    
    public String getDebugTest() {
        return debugTest;
    }
    
    public Map getBrochureProperties() {
        return brochureProperties;
    }
}
