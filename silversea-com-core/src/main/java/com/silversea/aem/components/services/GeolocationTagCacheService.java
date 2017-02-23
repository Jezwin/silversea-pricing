/**
 * 
 */
package com.silversea.aem.components.services;

import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * @author mjedli
 *
 */
public interface GeolocationTagCacheService {

    String getTagIdFromCountryId(ResourceResolver resourceResolver, String countryId);
    String getTagIdFromCurrentRequest(ResourceResolver resourceResolver);
    Map getTags(ResourceResolver resourceResolver) throws RepositoryException;

}
