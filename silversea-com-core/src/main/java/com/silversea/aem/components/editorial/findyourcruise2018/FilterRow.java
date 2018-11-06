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

    private FilterRowState state;

    public FilterRow(T value, String label, FilterRowState state) {
        this.value = value;
        this.label = notUsed -> label;
        this.key = label;
        this.state = state;
    }

    public FilterRow(T value, Function<T, String> label, String key, FilterRowState state) {
        this.value = value;
        this.label = label;
        this.key = key;
        this.state = state;

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
        return singleton(value, notUsed -> label, label);
    }

    static Stream<FilterRow<String>> singleton(String value) {
        return singleton(value, notUsed -> value, value);
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
        return key.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        try {
            String anotherKey = ((FilterRow<?>) o).key;
            return key.hashCode() == anotherKey.hashCode() && key.equals(anotherKey);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public int compareTo(FilterRow<T> other) {
        return this.getKey().compareTo(other.getKey());
    }


}
