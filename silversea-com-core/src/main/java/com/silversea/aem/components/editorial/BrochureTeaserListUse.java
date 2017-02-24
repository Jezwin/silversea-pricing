package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    private List<String> brochureList;

    private String debugTest;
    
    private Map testTag;

    private GeolocationTagCacheService geolocService;
    
    @Override
    public void activate() throws Exception {

        geolocService = getSlingScriptHelper().getService(GeolocationTagCacheService.class);
        
        testTag = geolocService.getTags(getResourceResolver());
        
        //Iterator<Resource> resources = getResourceResolver().findResources("//element(*,cq:Page)[jcr:content/cityCode=\"" + city.getCityCod() + "\"]", "xpath");
        
        Iterator<Resource> resources = getResourceResolver().findResources("//element(*,dam:Asset)[jcr:content/metadata/@cq:tags='geolocation:EU/OtherEurope/FR-FRA-250']", "xpath");
        brochureList = new ArrayList<String>();

        while (resources.hasNext()) {
            Node node = resources.next().adaptTo(Node.class);
            if(node.getPath().endsWith(".pdf")) {
                brochureList.add(node.getPath());
            }
        }
    }

    public Map getTestTag() {
        return testTag;
    }
    
    public String getDebugTest() {
        return debugTest;
    }
    
    public List<String> getBrochureList() {
        return brochureList;
    }
}
