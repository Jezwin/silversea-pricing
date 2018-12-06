package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Sets;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.helper.UrlHelper;
import com.silversea.aem.models.SmartButtonModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class SmartButtonUse extends AbstractGeolocationAwareUse {


    static private final Logger LOGGER = LoggerFactory.getLogger(SmartButtonUse.class);
    static private final String LIST_PROPERTIES =
            "data-sscclicktype,href,target,data-scrollElement,data-type,data-toggle,data-target,data-video,data-selectors,data-suffix,data-lightbox";
    private static final String[] DEVICES = new String[]{"Desktop", "Tablet", "Mobile"};
    private static final Set<String> IGNORED_KEYS = Sets.newHashSet("jcr", "sling", "cq", "sscUUID");

    private ValueMap sbProperties;
    private Externalizer externalizer;
    private String selectorUrl;
    private String suffixUrl;

    private List<SmartButtonModel> geoList;

    @Override
    public void activate() throws Exception {
        super.activate();
        externalizer = getResourceResolver().adaptTo(Externalizer.class);
        ValueMap dataProperties = get("properties", ValueMap.class);
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

        sbProperties.putAll(getPropertyByTagAndDevice());
        sbProperties.put("sscFwBackgroundColorHoverDesktop", sbProperties.get("sscFwBackgroundColorDesktop"));
    }

    private ValueMap getPropertyByTagAndDevice() {
        LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " start geolocation process");
        ValueMap properties = new ValueMapDecorator(new LinkedHashMap<>());
        for (String device : DEVICES) {
            geoList = retrieveMultiField(getResource(), "geoList" + device, SmartButtonModel.class);
            LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " geo {} process {}", device);
            AtomicBoolean foundBestMatch = new AtomicBoolean(false);
            for (int i = 0; i < geoList.size() && !foundBestMatch.get(); i++) {
                SmartButtonModel entry = geoList.get(i);
                Optional.ofNullable(entry.getGeoTag()).ifPresent(tags -> {
                    for (String tag : tags) {
                        String country = tag.split(":")[1];
                        if (isBestMatch(country)) {
                            properties.putAll(entry.toMap(device));
                            foundBestMatch.set(true);
                            break;
                        }
                    }
                });
            }
            setPropByDevices(properties, device);
        }
        return properties;
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
                sbProperties.put("dataLightbox" + device, "modalcontent");
            }
            String isExternalLink = sbProperties.get("isExternalLink" + device, String.class);
            if (!Boolean.valueOf(isExternalLink)) {
                linkUrl = linkUrl + ".html";
            }
        }

        sbProperties.put("linkUrl" + device, linkUrl);
    }


    public static <T> List<T> retrieveMultiField(Resource resource, String child, Class<T> adaptable) {
        return ofNullable(resource)
                .map(value -> value.getChild(child))
                .map(Resource::getChildren)
                .map(iterator -> stream(iterator.spliterator(), false))
                .map(stream -> stream.map(element -> element.adaptTo(adaptable)).filter(Objects::nonNull).collect(toList()))
                .orElse(emptyList());
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
