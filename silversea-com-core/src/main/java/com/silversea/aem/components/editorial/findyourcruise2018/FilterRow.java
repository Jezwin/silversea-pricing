package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.base.Objects;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.CHOSEN;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.DISABLED;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;

public class FilterRow<T> {

    private final T value;
    private final String label;

    private FilterRowState state;

    public FilterRow(T value, String label, FilterRowState state) {
        this.value = value;
        this.label = label;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterRow<?> filterRow = (FilterRow<?>) o;
        return Objects.equal(label, filterRow.label);
    }
}
