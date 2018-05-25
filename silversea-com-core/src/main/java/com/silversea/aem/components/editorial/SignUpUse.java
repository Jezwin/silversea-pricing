package com.silversea.aem.components.editorial;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.silversea.aem.components.AbstractGeolocationAwareUse;

public class SignUpUse extends AbstractGeolocationAwareUse {

    private String pageReference;

    @Override
    public void activate() throws Exception {
        super.activate();

        final InheritanceValueMap allInheritedProperties = new HierarchyNodeInheritanceValueMap(getResource());
        pageReference = allInheritedProperties.getInherited("pageReference", String.class);
    }

    public String getPageReference() {
        return pageReference;
    }

    public String getSiteCountry() {
        return countryCode;
    }

    public String getSiteCurrency() {
        return currency;
    }
    
    public String getGeoMarket(){
    	return geomarket;
    }
    
	public Boolean getIsGPDR(){
		if(countryCode.toLowerCase().equals("ca") || countryCode.toLowerCase().equals("can")){
			return false;
		}
		return true;
	}
}