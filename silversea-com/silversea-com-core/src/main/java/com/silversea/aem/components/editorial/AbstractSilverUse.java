

package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.InheritanceValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;

public abstract class AbstractSilverUse extends WCMUsePojo {
    public static final String TABLET = "Tablet";
    public static final String MOBILE = "Mobile";
    public static final String DESKTOP = "Desktop";

    public static <T> Optional<T> getProp(ValueMap map, String key, Class<T> type) {
        return ofNullable(map).map(props -> props.get(key, type));
    }

    public static Optional<String> getProp(ValueMap map, String key) {
        return getProp(map, key, String.class);
    }

    public static <T> Optional<T> getInheritedProp(InheritanceValueMap map, String key, Class<T> type) {
        return ofNullable(map).map(props -> props.getInherited(key, type));
    }

    public static Optional<String> getInheritedProp(InheritanceValueMap map, String key) {
        return getInheritedProp(map, key, String.class);
    }

    public static <T> Stream<T> retrieveMultiField(Resource resource, String child, Function<Resource, T> map) {
        return ofNullable(resource).map(value -> value.getChild(child)).map(Resource::getChildren)
                .map(iterator -> stream(iterator.spliterator(), false)).map(stream -> stream.map(map).filter(Objects::nonNull))
                .orElse(Stream.empty());
    }

    protected Optional<String> getProp(String key) {
        return getProp(key, String.class);
    }

    protected String getProp(String key, String defaultValue) {
        return getProp(key, String.class, defaultValue);
    }

    protected int getInt(String key, int defaultValue) {
        return getProp(key).map(Integer::parseInt).orElse(defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getProp(key).map("true"::equals).orElse(defaultValue);
    }

    protected boolean hasSelector(String selector) {
        for (String actualSelector : getRequest().getRequestPathInfo().getSelectors()) {
            if (actualSelector.equals(selector)) {
                return true;
            }
        }
        return false;
    }

    protected Optional<String> firstSelectorDifferentFrom(String excludedSelectors) {
        for (String selector : getRequest().getRequestPathInfo().getSelectors()) {
            if (!excludedSelectors.contains(selector)) {
                return of(selector);
            }
        }
        return Optional.empty();
    }

    protected <T> Optional<T> getProp(String key, Class<T> type) {
        return getProp(getProperties(), key, type);
    }

    protected <T> Optional<T> getProp(String key, Resource resource, Class<T> type) {
        return of(resource.getValueMap()).map(props -> props.get(key, type));
    }

    protected <T> T getProp(String key, Class<T> type, T defaultValue) {
        return getProp(key, type).orElse(defaultValue);
    }

    private <T> T fromVarArgs(T[] args, int index) {
        if (args == null || args.length == 0) {
            return null;
        }
        int length = args.length;
        if (length <= index) {
            return args[length - 1];
        }
        return args[index];
    }

    protected DeviceProperty<String> getDeviceProp(String key, String... defaultValue) {
        return getDeviceProp(key, String.class, defaultValue);
    }

    protected DeviceProperty<Integer> getDeviceProp(String key, Integer... defaultValue) {
        return getDeviceProp(key, Integer.class, defaultValue);
    }

    protected DeviceProperty<Boolean> getDeviceProp(String key, Boolean... defaultValue) {
        return getDeviceProp(key, Boolean.class, defaultValue);
    }

    protected <T> DeviceProperty<T> getDeviceProp(String key, Class<T> typeClass, T[] defaults) {
        return new DeviceProperty<>(getProp(key + DESKTOP, typeClass, fromVarArgs(defaults, 0)),
                getProp(key + TABLET, typeClass, fromVarArgs(defaults, 1)), getProp(key + MOBILE, typeClass, fromVarArgs(defaults, 2)));
    }

    protected <T> List<T> retrieveMultiField(String child, Class<T> adaptable) {
        return retrieveMultiField(getResource(), child, element -> element.adaptTo(adaptable)).collect(Collectors.toList());
    }

    protected <T> Stream<T> retrieveMultiField(String child, Function<Resource, T> map) {
        return retrieveMultiField(getResource(), child, map);
    }
}


