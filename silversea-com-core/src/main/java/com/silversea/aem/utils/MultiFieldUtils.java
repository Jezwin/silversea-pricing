package com.silversea.aem.utils;

import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class MultiFieldUtils {
    private MultiFieldUtils(){

    }

    public static <T> List<T> retrieveMultiField(Resource resource, String child, Class<T> adaptable) {
        return ofNullable(resource)
                .map(value -> value.getChild(child))
                .map(Resource::getChildren)
                .map(iterator -> stream(iterator.spliterator(), false))
                .map(stream -> stream.map(element -> element.adaptTo(adaptable)).filter(Objects::nonNull).collect(toList()))
                .orElse(emptyList());
    }
}
