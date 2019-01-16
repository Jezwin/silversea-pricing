package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;

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
import static org.apache.commons.lang.StringUtils.stripToEmpty;

public abstract class AbstractSilverUse extends WCMUsePojo {

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


    protected <T> Optional<T> getProp(String key, Class<T> type) {
        return ofNullable(getProperties()).map(props -> props.get(key, type));
    }

    protected <T> T getProp(String key, Class<T> type, T defaultValue) {
        return getProp(key, type).orElse(defaultValue);

    }

    protected <T> DeviceProperty<T> getDeviceProp(String key, Class<T> type, T defaultValue) {
        return new DeviceProperty<>(
                getProp(key + "Desktop", type, defaultValue),
                getProp(key + "Tablet", type, null),
                getProp(key + "Mobile", type, null)
        );
    }

    protected DeviceProperty<Boolean> getDeviceProp(String key, Boolean defaultValue) {
        return new DeviceProperty<>(
                getBoolean(key + "Desktop", defaultValue),
                getBoolean(key + "Tablet", defaultValue),
                getBoolean(key + "Mobile", defaultValue)
        );
    }

    protected <T> List<T> retrieveMultiField(String child, Class<T> adaptable) {
        return retrieveMultiField(child, element -> element.adaptTo(adaptable)).collect(Collectors.toList());
    }


    protected <T> Stream<T> retrieveMultiField(String child, Function<Resource, T> map) {
        return ofNullable(getResource())
                .map(value -> value.getChild(child))
                .map(Resource::getChildren)
                .map(iterator -> stream(iterator.spliterator(), false))
                .map(stream -> stream.map(map).filter(Objects::nonNull))
                .orElse(Stream.empty());
    }

    public static class DeviceProperty<T> {

        private final T desktop;
        private final T tablet;
        private final T mobile;

        public DeviceProperty(T desktop, T tablet, T mobile) {
            this.desktop = desktop;
            this.tablet = tablet == null ? desktop : tablet;
            this.mobile = mobile == null ? tablet : mobile;
        }

        public T getMobile() {
            return mobile;
        }

        public T getTablet() {
            return tablet;
        }

        public T getDesktop() {
            return desktop;
        }

        public T get(String device) {
            switch (device) {
                case "Tablet":
                    return getTablet();
                case "Mobile":
                    return getMobile();
                default:
                    return getDesktop();

            }
        }

        /**
         * @param mapFunction A function giving a new value using as parameters ["Desktop", "Tablet", "Mobile"] and the current value;
         * @return A new device property.
         */
        public <P> DeviceProperty<P> map(BiFunction<String, T, P> mapFunction) {
            return new DeviceProperty<>(mapFunction.apply("Desktop", desktop), mapFunction.apply("Tablet", tablet), mapFunction.apply("Mobile", mobile));
        }

        @Override
        public String toString() {
            return Stream.of(desktop, tablet, mobile).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(" "));
        }
    }

}
