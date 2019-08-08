package com.silversea.ssc.aem.helper;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;


public class GeoContentHelper extends AbstractGeolocationAwareUse {

    private String requestedProperty;
    private String retrievedValue;
    private ValueMap componentProperties;
    static private final String GEO_PROP = "listText";
    static private final Logger LOGGER = LoggerFactory.getLogger(GeoContentHelper.class);

    @Override
    public void activate() throws Exception {
        super.activate();
        requestedProperty = get("property", String.class);
        componentProperties = getProperties();
        setFromAdvancedGeoLocation();
        if (retrievedValue == null) {
            if(componentProperties.get(requestedProperty) != null ) {
                retrievedValue = componentProperties.get(requestedProperty).toString();
            }
        }
    }

    private void setFromAdvancedGeoLocation() {
        if (getResource().hasChildren() && getResource().getChild(GEO_PROP) != null) {
            for (Resource child : getResource().getChild(GEO_PROP).getChildren()) {
                Node node = child.adaptTo(Node.class);
                try {
                    if (node.hasProperty("geoTag") && node.getProperty("geoTag") != null) {
                        String[] geoTagList = node.getProperty("geoTag") != null ? node.getProperty("geoTag").getString().split(",") : null;
                        for (String tag : geoTagList) {
                            String[] geoTag = tag.split(WcmConstants.GEOLOCATION_TAGS_PREFIX);
                            if (geoTag != null && geoTag.length > 0) {
                                if(super.isBestMatch(geoTag[1])) {
                                    this.retrievedValue = node.hasProperty(requestedProperty +"Geo")  ? node.getProperty(requestedProperty + "Geo").getString() : null;
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Error to get geoTag {}", e.getMessage());
                }
            }
        }
    }

    public String getValue() {
        return retrievedValue;
    }


}