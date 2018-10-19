package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Set;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.CHOSEN;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.DISABLED;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;

public class FilterRow<T> implements Comparable<FilterRow<T>> {

    private final T value;
    private final String label;
    private final String key;

    private FilterRowState state;

    public FilterRow(T value, String label, FilterRowState state) {
        this.value = value;
        this.label = label;
        this.key = label;
        this.state = state;
    }

    public FilterRow(T value, String label, String key, FilterRowState state) {
        this.value = value;
        this.label = label;
        this.key = key;
        this.state = state;

    }

    public T getValue() {
        return value;
    }

    public FilterRowState getState() {
        return state;
    }

    public String getLabel() {
        return label;
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

    static <T> Set<FilterRow<T>> singleton(T value, String label) {
        return singleton(value, label, label);
    }

    static Set<FilterRow<String>> singleton(String value) {
        return singleton(value, value, value);
    }

    static <T> Set<FilterRow<T>> singleton(T value, String label, String key) {
        return Collections.singleton(new FilterRow<>(value, label, key, ENABLED));
    }


    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("label", label);
        json.addProperty("key", key);
        json.addProperty("state", state.toString());
        return json;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterRow<?> filterRow = (FilterRow<?>) o;
        return Objects.equal(key, filterRow.key);
    }

    @Override
    public int compareTo(FilterRow<T> other) {
        return this.getKey().compareTo(other.getKey());
    }


}
