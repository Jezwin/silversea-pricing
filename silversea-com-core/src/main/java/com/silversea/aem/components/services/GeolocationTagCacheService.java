/**
 * 
 */
package com.silversea.aem.components.services;

import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * @author mjedli
 *
 */
public interface GeolocationTagCacheService {

    String getTagIdFromCountryId(ResourceResolver resourceResolver, String countryId) throws RepositoryException;

    String getTagIdFromCurrentRequest(ResourceResolver resourceResolver, SlingHttpServletRequest request) throws RepositoryException;

    Map getTags(ResourceResolver resourceResolver) throws RepositoryException;

}
