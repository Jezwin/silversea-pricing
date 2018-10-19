package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.base.Objects;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.silversea.aem.models.CruiseModelLight;

import java.util.*;
import java.util.stream.Collectors;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.*;
import static java.util.stream.Collectors.toSet;

public abstract class AbstractFilter<T> {
    private final String kind;
    private Set<FilterRow<T>> rows;

    AbstractFilter(String kind) {
        this.kind = kind;
    }

    protected abstract Collection<FilterRow<T>> rows(CruiseModelLight cruiseModelLight);


    public Collection<FilterRow<T>> getRows() {
        return rows;
    }

    public String getKind() {
        return kind;
    }

    public final boolean isSelected() {
        return rows.stream().anyMatch(row -> CHOSEN.equals(row.getState()));
    }

    public final FilterRowState retrieveState(String key) {
        return rows.stream().filter(row -> row.getKey().equals(key)).findAny().map(FilterRow::getState)
                .orElseThrow(() -> new IllegalArgumentException("Could not find " + key));
    }

    final boolean matches(CruiseModelLight cruise) {
        Set<FilterRow<T>> selectedValues = rows.stream().filter(row -> CHOSEN.equals(row.getState())).collect(toSet());
        if (!selectedValues.isEmpty()) {
            return matches(selectedValues, cruise);
        }
        return true;
    }

    void setEnabled(Set<String> enabledKeys) {
        rows.forEach(row -> row.setState(enabledKeys.contains(row.getKey()) ? CHOSEN : ENABLED));
    }

    void disableMissingLabels(Collection<CruiseModelLight> cruises) {
        rows.forEach(row -> row.setState(cruiseMatchesTheRow(cruises, row) ? ENABLED : DISABLED));

    }

    final void initAllValues(List<CruiseModelLight> cruises) {
        this.rows =
                cruises.stream().flatMap(cruise -> rows(cruise).stream())
                        .collect(Collectors.toCollection(() -> new TreeSet<>(comparator())));//keep the order
    }

    protected Comparator<FilterRow<T>> comparator() {
        return Comparator.naturalOrder();
    }

    private boolean matches(Set<FilterRow<T>> selectedRows, CruiseModelLight cruiseModelLight) {
        Collection<FilterRow<T>> cruiseRows = rows(cruiseModelLight);
        for (FilterRow<T> cruiseRow : cruiseRows) {
            if (selectedRows.contains(cruiseRow)) {
                return true;
            }
        }
        return false;
    }

    private boolean cruiseMatchesTheRow(Collection<CruiseModelLight> cruises, FilterRow<T> row) {
        return cruises.stream().flatMap(cruise -> rows(cruise).stream()).map(FilterRow::getKey)
                .anyMatch(row.getKey()::equals);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFilter<?> that = (AbstractFilter<?>) o;
        return kind.equals(that.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(kind);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JsonElement toJson() {
        JsonArray array = new JsonArray();
        rows.forEach(row -> array.add(row.toJson()));
        return array;
    }
}
