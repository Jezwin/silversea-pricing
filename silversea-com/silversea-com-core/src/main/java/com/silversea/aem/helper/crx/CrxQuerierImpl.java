package com.silversea.aem.helper.crx;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class CrxQuerierImpl implements CrxQuerier {
    private ResourceResolver resourceResolver;

    public CrxQuerierImpl(ResourceResolver resourceResolver) {
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
}
