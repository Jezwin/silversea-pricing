package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.*;

public class FilterRow<T> implements Comparable<FilterRow<T>> {

    private final T value;
    private final Function<T, String> label;
    private final String key;

    private final int hash;

    private FilterRowState state;

    public FilterRow(T value, String label, FilterRowState state) {
        this.value = value;
        this.label = notUsed -> label;
        this.key = label;
        this.state = state;

        this.hash = key.hashCode();
    }

    public FilterRow(T value, Function<T, String> label, String key, FilterRowState state) {
        this.value = value;
        this.label = label;
        this.key = key;
        this.state = state;

        this.hash = key.hashCode();
    }

    public FilterRowState getState() {
        return state;
    }

    public String getLabel() {
        return label.apply(value);
    }

    public String getKey() {
        return key;
    }

    public boolean isEnabled() {
        return ENABLED.equals(state);
    }

    public boolean isDisabled() {
        return DISABLED.equals(state);
    }

    public boolean isChosen() {
        return CHOSEN.equals(state);
    }

    public void setState(FilterRowState state) {
        this.state = state;
    }

    static <T> Stream<FilterRow<T>> singleton(T value, String label) {
        return Stream.of(new FilterRow<>(value, notUsed -> label, label, ENABLED));
    }

    static Stream<FilterRow<String>> singleton(String value) {
        return Stream.of(new FilterRow<>(value, notUsed -> value, value, ENABLED));
    }

    static <T> Stream<FilterRow<T>> singleton(T value, Function<T, String> label, String key) {
        return Stream.of(new FilterRow<>(value, label, key, ENABLED));
    }


    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("label", getLabel());
        json.addProperty("key", key);
        json.addProperty("state", state.toString());
        return json;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        try {
            FilterRow<?> anotherKey = ((FilterRow<?>) o);
            return hash == anotherKey.hash && key.equals(anotherKey.key);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public int compareTo(FilterRow<T> other) {
        return key.compareTo(other.key);
    }


}
