package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMBindings;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.LinkedHashMap;
import java.util.Optional;

public class SmartButtonUse extends AbstractGeolocationAwareUse {

    static private final Logger LOGGER = LoggerFactory.getLogger(SmartButtonUse.class);
    static private final String GEO_PROP = "geoTag";
    static private final String GEO_NODE = "geoList";
    static private final String SEPARATOR = "~";

    private ValueMap sbProperties;

    @Override
    public void activate() throws Exception {
        super.activate();
        ValueMap dataProperties = get(WCMBindings.PROPERTIES, ValueMap.class);
        sbProperties = new ValueMapDecorator(new LinkedHashMap<>());
        //use the key and value here
        dataProperties.entrySet().forEach(e -> {
            if (!(e.getKey().contains("jcr") || e.getKey().contains("sling") || e.getKey().contains("cq") || e.getKey().contains("sscUUID"))) {
                sbProperties.put(e.getKey(), e.getValue());
            }
        });

        LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " start geolocation process");

        setGeolocationProperties(GEO_PROP + "Desktop", GEO_NODE + "Desktop").ifPresent(desktop -> {
            LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " geo desktop process {}", desktop.size());
            sbProperties.putAll(desktop);
        });
        setGeolocationProperties(GEO_PROP + "Tablet", GEO_NODE + "Tablet").ifPresent(tablet -> {
            LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " geo tablet process {}", tablet.size());
            sbProperties.putAll(tablet);
        });
        setGeolocationProperties(GEO_PROP + "Mobile", GEO_NODE + "Mobile").ifPresent(mobile -> {
            LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " geo mobile process {}", mobile.size());
            sbProperties.putAll(mobile);
        });
    }

    private Optional<ValueMap> setGeolocationProperties(String propName, String nodeName) {
        if (getResource().hasChildren() && getResource().getChild(nodeName) != null) {
            ValueMap geoProperties = new ValueMapDecorator(new LinkedHashMap<>());
            for (Resource child : getResource().getChild(nodeName).getChildren()) {
                Node node = child.adaptTo(Node.class);
                getProp(child.getValueMap(), propName).ifPresent(geoTagProperty -> {
                    for (String tag : geoTagProperty.split(",")) {
                        String[] geoTag = tag.split(WcmConstants.GEOLOCATION_TAGS_PREFIX);
                        if (geoTag.length > 1 && isBestMatch(geoTag[1])) {
                            ValueMap geoMap = child.getValueMap();
                            geoMap.entrySet().forEach(e -> {
                                if (e.getValue() != null) {
                                    geoProperties.put(e.getKey(), e.getValue());
                                }
                            });
                            break;
                        }
                    }
                });
            }
            return Optional.of(geoProperties);
        }
        return Optional.empty();
    }

    private Optional<String> getProp(ValueMap valueMap, String key) {
        try {
            return Optional.ofNullable(valueMap.get(key)).map(property -> {
                return property.toString();
            });
        } catch (Exception exception) {
            LOGGER.error("Error retrieving {} from {}", key, valueMap, exception);
            return Optional.empty();
        }
    }

    public ValueMap getSbProperties() {
        return sbProperties;
    }

}
