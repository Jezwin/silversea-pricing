package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.ValueMap;

import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.of;

public abstract class AbstractFrameworkHelper extends WCMUsePojo {
    private static final String[] DEVICES = new String[]{"Desktop", "Tablet", "Mobile"};

    private String[] cssAttributes;
    private ValueMap componentProperties;

    @Override
    public void activate() throws Exception {
        cssAttributes = get("property", String.class).split(",");
        componentProperties = getProperties();
    }

    private <T> String forEach(T[] elements, Function<T, String> function) {
        StringBuilder builder = new StringBuilder();
        for (T element : elements) {
            builder.append(function.apply(element)).append(" ");
        }
        return builder.toString();
    }

    protected String getString(String key) {
        return componentProperties.get(key, String.class);
    }

    protected String getMethod(String property, String device) {
        return getString(property + device + "Method");
    }

    public String getAllDevices() {
        return forEach(cssAttributes, attribute -> forEach(DEVICES,
                device -> generateOutput(attribute, device, getMethod(attribute, device))));
    }

    public String getAllDevicesViewport() {
        return forEach(cssAttributes,
                attribute -> forEach(DEVICES, device -> generateOutput(attribute, device, "Viewport")));
    }

    public String getAllDevicesPixel() {
        return forEach(cssAttributes,
                attribute -> forEach(DEVICES, device -> generateOutput(attribute, device, "Pixel")));
    }

    public String getNoDevices() {
        return forEach(cssAttributes, atttribute -> generateOutput(atttribute, "", ""));

    }

    public abstract String generateOutput(String property, String device, String method);
}
