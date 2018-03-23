package com.silversea.aem.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class)
public class GeolocationTagModel {

    @Inject @Self
    private Resource resource;

    @Inject @Named("iso2") @Optional
    private String countryCode;

    @Inject @Named("iso3") @Optional
    private String countryCodeIso3;

    @Inject @Optional
    private String market;

    @Inject @Optional
    private String region;

    @Inject @Named("Currency") @Optional
    private String currency;

    @Inject @Optional
    private String prefix;

    @Inject @Named("jcr:title") @Optional
    private String title;

    @Inject @Optional
    private String phone;

    private String path;
    
    private String regionFromPath;

    @PostConstruct
    private void init() {
        path = resource.getPath();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryCodeIso3() {
        return countryCodeIso3;
    }

    public String getMarket() {
        return market.toLowerCase();
    }

    public String getRegion() {
        return region;
    }

    public String getCurrency() {
        // TODO issues with imported data
        if (currency.equals("Euro")) {
            return "EUR";
        }

        return currency;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTitle() {
        return title;
    }

    public String getPhone() {
        return phone;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return market;
    }
    
    public String getRegionFromPath() { 
    	String region = null;
    	if (StringUtils.isNotEmpty(path)) {
    		String[] splitPath = path.split("geotagging/");
    		if(splitPath != null && splitPath.length > 0) {
    			String[] areaSplit = splitPath[1].split("/");
    			if (areaSplit != null & areaSplit.length > 1) {
    				region = areaSplit[1];
    			}
    		}
    	}
    	return region;
    }
}
