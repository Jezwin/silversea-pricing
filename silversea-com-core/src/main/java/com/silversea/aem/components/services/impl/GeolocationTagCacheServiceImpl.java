/**
 * 
 */
package com.silversea.aem.components.services.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.components.ComponentContext;
import com.silversea.aem.components.services.GeolocationTagCacheService;

/**
 * @author mjedli
 *
 */
@Service
@Component(immediate=true, label = "Silversea.com - Geolocation Tag Cache Service")
public class GeolocationTagCacheServiceImpl implements GeolocationTagCacheService {

    public Map mapTags; 
    
    private boolean isInitService = false;
    
    private void initService(ResourceResolver resourceResolver) throws RepositoryException {
        
        mapTags = new HashMap();
        
        Resource resourceTag = resourceResolver.getResource("/etc/tags/geolocation/");
        
        Node geolocNode = resourceTag.adaptTo(Node.class);
        NodeIterator iteratorGeolocNode = geolocNode.getNodes();
        
        while (iteratorGeolocNode.hasNext()) {
            Node areaNode = (Node) iteratorGeolocNode.next();
            String area = areaNode.getName();
            NodeIterator iteratorAreaNode = areaNode.getNodes();
            
            while (iteratorAreaNode.hasNext()) {
                Node marketNode = (Node) iteratorAreaNode.next();
                String market = marketNode.getName();
                NodeIterator iteratorMarketNode = marketNode.getNodes();
                
                while (iteratorMarketNode.hasNext()) {
                    Node countryCodeNode = (Node) iteratorMarketNode.next();
                    String countryCode = countryCodeNode.getName();
                    
                    /* geolocation:<area>/<market>/<country> */
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("geolocation:");
                    sb.append(area);
                    sb.append("/").append(market);
                    sb.append("/").append(countryCode);
                    
                    mapTags.put(countryCode,sb.toString());
                }
            }
        }
        
        isInitService = true;
    }
    
    @Override
    public Map getTags(ResourceResolver resourceResolver) throws RepositoryException {
        if (!isInitService)
            initService(resourceResolver);
        return mapTags;
    }
    
    @Override
    public String getTagIdFromCountryId(ResourceResolver resourceResolver, String countryId) {
        
        return null;
    }

    @Override
    public String getTagIdFromCurrentRequest(ResourceResolver resourceResolver) {
        // TODO Auto-generated method stub
        return null;
    }


}
