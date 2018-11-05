package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Sets;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.UrlHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;

public class SmartButtonUse extends AbstractGeolocationAwareUse {


    static private final Logger LOGGER = LoggerFactory.getLogger(SmartButtonUse.class);
    static private final String GEO_PROP = "geoTag";
    static private final String GEO_NODE = "geoList";
    static private final String LIST_PROPERTIES =
            "data-sscclicktype,href,target,data-scrollElement,data-type,data-toggle,data-target,data-video,data-selectors,data-suffix";
    private static final String[] DEVICES = new String[]{"Desktop", "Tablet", "Mobile"};
    private static final Set<String> IGNORED_KEYS = Sets.newHashSet("jcr", "sling", "cq", "sscUUID");

    private ValueMap sbProperties;
    private Externalizer externalizer;
    private String selectorUrl;
    private String suffixUrl;

    @Override
    public void activate() throws Exception {
        super.activate();
        externalizer = getResourceResolver().adaptTo(Externalizer.class);
        ValueMap dataProperties = get(WCMBindings.PROPERTIES, ValueMap.class);
        sbProperties = new ValueMapDecorator(new LinkedHashMap<>());

        Page currentPage = getCurrentPage();
        String[] selectorSuffixUrl = UrlHelper.createSuffixAndSelectorUrl(currentPage);
        this.selectorUrl = selectorSuffixUrl[0];
        this.suffixUrl = selectorSuffixUrl[1];

        //use the key and value here
        setContent(dataProperties);
        dataProperties.forEach((key, value) -> {
            if (!IGNORED_KEYS.contains(key)) {
                sbProperties.put(key, value);
            }
        });

        LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " start geolocation process");

        for (String device : DEVICES) {
            setGeolocationProperties(GEO_PROP + device, GEO_NODE + device).ifPresent(deviceProp -> {
                LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " geo {} process {}", device,
                        deviceProp.size());
                sbProperties.putAll(deviceProp);
            });
            setPropByDevices(sbProperties, device);
        }
        sbProperties.put("sscFwBackgroundColorHoverDesktop", sbProperties.get("sscFwBackgroundColorDesktop"));
    }

    private void setContent(ValueMap dataProperties) {
        String contentDesktop = dataProperties.get("sscFwContentDesktop", String.class);
        if (StringUtils.isEmpty(dataProperties.get("sscFwContentTablet", String.class))) {
            sbProperties.put("sscFwContentTablet", contentDesktop);
        }
        if (StringUtils.isEmpty(dataProperties.get("contentMobile", String.class))) {
            sbProperties.put("sscFwContentMobile", contentDesktop);
        }
    }

    private void setPropByDevices(ValueMap sbProperties, String device) {

        String[] propertiesByDevices =
                {"linkUrl", "enableLightbox", "isExternalLink", "type", "analyticType", "enableToScrollElement",
                        "openNewTab"};
        for (String prop : propertiesByDevices) {
            String value = sbProperties.get(prop + device, String.class);
            if (!device.equalsIgnoreCase("desktop")) {
                value = StringUtils.isEmpty(value) ? sbProperties.get(prop + "Desktop", String.class) : value;
            }
            value = prop.equals("openNewTab") ? (Boolean.valueOf(value) ? "_blank" : "") : value;
            sbProperties.put(prop + device, value);
        }

        String linkUrl = sbProperties.get("linkUrl" + device, String.class);
        String type = sbProperties.get("type" + device, String.class);

        if (StringUtils.isNotEmpty(type) && type.equalsIgnoreCase("video")) {
            sbProperties.put("dataVideo" + device, linkUrl);
            sbProperties.put("dataTarget" + device, ".bs-modal-lg");
            sbProperties.put("dataToggle" + device, "modal");
            linkUrl = "#";
        } else if (StringUtils.isNotEmpty(linkUrl)) {
            linkUrl = externalizer.relativeLink(getRequest(), linkUrl);
            String enableLightbox = sbProperties.get("enableLightbox" + device, String.class);
            if (Boolean.valueOf(enableLightbox)) {
                sbProperties.put("dataToggle" + device, "modal");
                sbProperties.put("dataTarget" + device, ".bs-modal-lg");
            }
            String isExternalLink = sbProperties.get("isExternalLink" + device, String.class);
            if (!Boolean.valueOf(isExternalLink)) {
                linkUrl = linkUrl + ".html";
            }
        }

        sbProperties.put("linkUrl" + device, linkUrl);
    }

    private Optional<ValueMap> setGeolocationProperties(String propName, String nodeName) {
        if (getResource().hasChildren() && getResource().getChild(nodeName) != null) {
            ValueMap geoProperties = new ValueMapDecorator(new LinkedHashMap<>());
            for (Resource child : getResource().getChild(nodeName).getChildren()) {
                getProp(child.getValueMap(), propName).ifPresent(geoTagProperty -> {
                    for (String tag : geoTagProperty.split(",")) {
                        String[] geoTag = tag.split(WcmConstants.GEOLOCATION_TAGS_PREFIX);
                        if (geoTag.length > 1 && isBestMatch(geoTag[1])) {
                            ValueMap geoMap = child.getValueMap();
                            geoMap.forEach((key, value) -> {
                                if (value != null) {
                                    geoProperties.put(key, value);
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
            return Optional.ofNullable(valueMap.get(key)).map(Object::toString);
        } catch (Exception exception) {
            LOGGER.error("Error retrieving {} from {}", key, valueMap, exception);
            return Optional.empty();
        }
    }

    public ValueMap getSbProperties() {
        return sbProperties;
    }

    public String getListProperties() {
        return LIST_PROPERTIES;
    }

    public String getSelectorUrl() {
        return selectorUrl;
    }

    public String getSuffixUrl() {
        return suffixUrl;
    }
}
