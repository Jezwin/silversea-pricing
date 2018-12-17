package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Model(adaptables = Resource.class)
public class SmartButtonModel {

    @Inject
    @Optional
    private String type;

    @Inject
    @Optional
    private String analyticType;

    @Inject
    @Optional
    private String linkUrl;

    @Inject
    @Optional
    private String sscFwContent;

    @Inject
    @Optional
    private String sscFwBackgroundColor;

    @Inject
    @Optional
    private String sscFwColor;

    @Inject
    @Optional
    private String sscFwBorderColor;

    @Inject
    @Optional
    private String isTrasparentButton;

    @Inject
    @Optional
    private String enableToScrollElement;

    @Inject
    @Optional
    private String enableLightbox;

    @Inject
    @Optional
    private String openNewTab;

    @Inject
    @Optional
    private String isExternalLink;

    @Inject
    @Optional
    private String sscFwDisplay;

    @Inject
    @Optional
    private String sscFwDisplaySnd;

    @Inject
    @Optional
    private String[] geoTag;

    public String getType() {
        return type;
    }

    public String getAnalyticType() {
        return analyticType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getSscFwContent() {
        return sscFwContent;
    }

    public String getSscFwBackgroundColor() {
        return sscFwBackgroundColor;
    }

    public String getSscFwColor() {
        return sscFwColor;
    }

    public String getSscFwBorderColor() {
        return sscFwBorderColor;
    }

    public String getIsTrasparentButton() {
        return isTrasparentButton;
    }

    public String getEnableToScrollElement() {
        return enableToScrollElement;
    }

    public String getEnableLightbox() {
        return enableLightbox;
    }

    public String getOpenNewTab() {
        return openNewTab;
    }

    public String getIsExternalLink() {
        return isExternalLink;
    }

    public String getSscFwDisplay() {
        return sscFwDisplay;
    }

    public String getSscFwDisplaySnd() {
        return sscFwDisplaySnd;
    }

    public String[] getGeoTag() {
        return geoTag;
    }

    /**
     * @param device: Desktop, Tablet, Mobile
     * @return key: methodName + Device, value: methodValue. where method is public, start with get, without parameters and return a String.
     * Example: key: sscFwDisplayDesktop, value: getSscFwDisplaySnd();
     */
    public Map<String, String> toMap(String device) {
        return Arrays.stream(this.getClass().getDeclaredMethods()).filter(method -> method.getName().startsWith("get")).filter(method -> method.getReturnType().equals(String.class)).filter(method
                -> method.getParameterCount() == 0).filter(method
                -> Modifier.isPublic(method.getModifiers())).collect(HashMap::new, (map, m) -> {
            try {
                String key = m.getName().replace("get", "");
                key = key.substring(0, 1).toLowerCase() + key.substring(1) + device;
                String value = (String) m.invoke(this);
                if (value != null && value != "") {
                    map.put(key, value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }, HashMap::putAll);
    }
}