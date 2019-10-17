package com.silversea.aem.content;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;

public class CrxContentLoader implements ContentLoader {
    private ResourceResolver resourceResolver;

    public CrxContentLoader(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    @Override
    public <T> T get(String path, Class<T> objectClass) throws Exception {
        Resource crxResource = resourceResolver.getResource(path);

        if(crxResource == null) {
            String message = String.format("Couldn't find %s at CRX path %s", objectClass.getSimpleName(), path);
            throw new Exception(message);
        }

        T adaptedResult = crxResource.adaptTo(objectClass);

        if(adaptedResult == null) {
            String message = String.format("Found CRX resource at %s but was unable to convert it to a %s", path, objectClass.getSimpleName());
            throw new Exception(message);
        }

        return adaptedResult;
    }

    @Override
    public HashMap<String, Object> get(String path) throws Exception {
        Resource crxResource = resourceResolver.getResource(path);

        if(crxResource == null) {
            String message = String.format("Couldn't find node at CRX path %s", path);
            throw new Exception(message);
        }

        HashMap<String, Object> map = new HashMap<>();

        ValueMap valueMap = crxResource.getValueMap();
        valueMap.keySet()
                .forEach(k -> map.put(k, valueMap.get(k)));
        return map;
    }
}
