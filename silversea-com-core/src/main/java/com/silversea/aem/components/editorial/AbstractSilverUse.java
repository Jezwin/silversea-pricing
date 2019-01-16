package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;

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

    protected Optional<String> getSelectorValue(String[] selectors, String fixedSelectors) {
        for (String selector : selectors) {
            if (!fixedSelectors.contains(selector)) return of(selector);
        }
        return Optional.empty();
    }

    protected <T> Optional<T> getProp(String key, Class<T> type) {
        return ofNullable(getProperties()).map(props -> props.get(key, type));
    }

    protected <T> Optional<T> getProp(String key, Resource resource, Class<T> type) {
        return of(resource.getValueMap()).map(props -> props.get(key, type));
    }


    protected <T> T getProp(String key, Class<T> type, T defaultValue) {
        return getProp(key, type).orElse(defaultValue);

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

}
