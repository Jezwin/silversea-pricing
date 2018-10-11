package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMBindings;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

public class SmartButtonUse extends AbstractGeolocationAwareUse {

    static private final Logger LOGGER = LoggerFactory.getLogger(SmartButtonUse.class);
    static private final String GEO_PROP = "geoTag";
    static private final String GEO_NODE = "geoList";
    static private final String SEPARATOR = "~";

    private ValueMap sbProperties;
    private StringBuilder dataProperties = new StringBuilder();

    @Override
    public void activate() throws Exception {
        super.activate();
        sbProperties = get(WCMBindings.PROPERTIES, ValueMap.class);

        //use the key and value here
        sbProperties.entrySet().forEach(e -> {
            if (!(e.getKey().contains("jcr") || e.getKey().contains("sling") || e.getKey().contains("cq") || e.getKey().contains("sscFw") || e.getKey().contains("sscUUID"))) {
                dataProperties.append(e.getKey());
                dataProperties.append("=" + e.getValue().toString()+ SEPARATOR);
            }
        });

        LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " create dataProperties {}", dataProperties.toString());
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
            for (Resource child : getResource().getChild(nodeName).getChildren()) {
                ValueMap geoProperties = getResource().getValueMap();
                Node node = child.adaptTo(Node.class);
                getProp(node, propName).ifPresent(geoTagProperty -> {
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
        }
        return Optional.empty();
    }

    private Optional<String> getProp(Node node, String key) {
        try {
            return Optional.ofNullable(node.getProperty(key)).map(property -> {
                try {
                    return property.getString();
                } catch (RepositoryException exception) {
                    LOGGER.error("Error retrieving {} from {}", key, node, exception);
                    return null;
                }
            });
        } catch (RepositoryException exception) {
            LOGGER.error("Error retrieving {} from {}", key, node, exception);
            return Optional.empty();
        }
    }

    public ValueMap getSbProperties() {
        return sbProperties;
    }


    public String getDataProperties() {
        return dataProperties.toString();
    }
}
