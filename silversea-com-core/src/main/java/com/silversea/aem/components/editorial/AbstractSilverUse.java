package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.InheritanceValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;
import static java.util.Optional.of;

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
        return ofNullable(resource)
                .map(value -> value.getChild(child))
                .map(Resource::getChildren)
                .map(iterator -> stream(iterator.spliterator(), false))
                .map(stream -> stream.map(map).filter(Objects::nonNull))
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

    protected Optional<String> getSelectorValue(String[] selectors, String fixedSelectors) {
        for (String selector : selectors) {
            if (!fixedSelectors.contains(selector)) return of(selector);
        }
        return Optional.empty();
    }

    protected <T> Optional<T> getProp(String key, Class<T> type) {
        return getProp(getProperties(), key, type);
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

    protected <T> DeviceProperty<T> getDeviceProp(String key, Class<T> type, T... defaultValue) {
        return new DeviceProperty<>(
                getProp(key + DESKTOP, type, fromVarArgs(defaultValue, 0)),
                getProp(key + TABLET, type, fromVarArgs(defaultValue, 1)),
                getProp(key + MOBILE, type, fromVarArgs(defaultValue, 2))
        );
    }

    protected DeviceProperty<Boolean> getDeviceProp(String key, Boolean... defaultValue) {
        return new DeviceProperty<>(
                getBoolean(key + DESKTOP, fromVarArgs(defaultValue, 0)),
                getBoolean(key + TABLET, fromVarArgs(defaultValue, 1)),
                getBoolean(key + MOBILE, fromVarArgs(defaultValue, 2))
        );
    }

    protected <T> List<T> retrieveMultiField(String child, Class<T> adaptable) {
        return retrieveMultiField(getResource(), child, element -> element.adaptTo(adaptable)).collect(Collectors.toList());
    }


    protected <T> Stream<T> retrieveMultiField(String child, Function<Resource, T> map) {
        return retrieveMultiField(getResource(), child, map);
    }



}
