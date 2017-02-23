/**
 * 
 */
package com.silversea.aem.components.services.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import com.silversea.aem.components.services.GeolocationTagCacheService;

/**
 * @author mjedli
 *
 */
@Service
@Component(label = "Silversea.com - Geolocation Tag Cache Service")
public class GeolocationTagCacheServiceImpl implements GeolocationTagCacheService {

    @Override
    public String getTagIdFromCountryId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTagIdFromCurrentRequest() {
        // TODO Auto-generated method stub
        return null;
    }

}
