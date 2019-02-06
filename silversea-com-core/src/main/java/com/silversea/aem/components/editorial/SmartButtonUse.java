package com.silversea.aem.components.editorial;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Sets;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.helper.UrlHelper;
import com.silversea.aem.models.SmartButtonModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
        for (String device : DEVICES) {
            setPropByDevices(sbProperties, device);
        }
        sbProperties.put("sscFwBackgroundColorHoverDesktop", sbProperties.get("sscFwBackgroundColorDesktop"));
    }

    private ValueMap getPropertyByTagAndDevice() {
        LOGGER.debug(SmartButtonUse.class.toString().toUpperCase() + " start geolocation process");
        ValueMap properties = new ValueMapDecorator(new LinkedHashMap<>());
        for (String device : DEVICES) {
            geoList = retrieveMultiField("geoList" + device, SmartButtonModel.class);
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

    private void setPropByDevices(ValueMap propertiesToChange, String device) {
        String[] propertiesByDevices =
                {"linkUrl", "enableLightbox", "isExternalLink", "type", "analyticType", "enableToScrollElement",
                        "openNewTab"};
        for (String prop : propertiesByDevices) {
            String value = propertiesToChange.get(prop + device, String.class);
            if (!device.equalsIgnoreCase("desktop")) {
                value = StringUtils.isEmpty(value) ? propertiesToChange.get(prop + "Desktop", String.class) : value;
            }
            value = prop.equals("openNewTab") ? (Boolean.valueOf(value) ? "_blank" : "") : value;
            if (StringUtils.isNotEmpty(value)) {
                propertiesToChange.put(prop + device, value);
            }
        }

        String linkUrl = propertiesToChange.get("linkUrl" + device, String.class);
        String type = propertiesToChange.get("type" + device, String.class);

        if (StringUtils.isNotEmpty(type) && type.equalsIgnoreCase("video")) {
            propertiesToChange.put("dataVideo" + device, linkUrl);
            propertiesToChange.put("dataTarget" + device, ".bs-modal-lg");
            propertiesToChange.put("dataToggle" + device, "modal");
            linkUrl = "#";
        } else if (StringUtils.isNotEmpty(linkUrl)) {
            linkUrl = externalizer.relativeLink(getRequest(), linkUrl);
            String enableLightbox = propertiesToChange.get("enableLightbox" + device, String.class);
            if (Boolean.valueOf(enableLightbox)) {
                propertiesToChange.put("dataToggle" + device, "modal");
                propertiesToChange.put("dataTarget" + device, ".bs-modal-lg");
                propertiesToChange.put("dataLightbox" + device, "modalcontent");
            }
            String isExternalLink = propertiesToChange.get("isExternalLink" + device, String.class);
            if (!Boolean.valueOf(isExternalLink)) {
                linkUrl = linkUrl + ".html";
            }
        }
        if (StringUtils.isNotEmpty(linkUrl)) {
            propertiesToChange.put("linkUrl" + device, linkUrl);
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
