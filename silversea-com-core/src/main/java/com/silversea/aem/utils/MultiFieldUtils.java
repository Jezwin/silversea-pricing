package com.silversea.aem.utils;

import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;

public class MultiFieldUtils {
    private MultiFieldUtils() {

    }

    public static <T> List<T> retrieveMultiField(Resource resource, String child, Class<T> adaptable) {
        return retrieveMultiField(resource, child, element -> element.adaptTo(adaptable)).collect(Collectors.toList());
    }


    public static <T> Stream<T> retrieveMultiField(Resource resource, String child, Function<Resource, T> map) {
        return ofNullable(resource)
                .map(value -> value.getChild(child))
                .map(Resource::getChildren)
                .map(iterator -> stream(iterator.spliterator(), false))
                .map(stream -> stream.map(map).filter(Objects::nonNull))
                .orElse(Stream.empty());
    }
}
