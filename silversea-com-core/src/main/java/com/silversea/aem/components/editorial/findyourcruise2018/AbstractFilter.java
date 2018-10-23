package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.silversea.aem.models.CruiseModelLight;

import java.util.*;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.*;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

public abstract class AbstractFilter<T> {
    private final String kind;
    private Set<FilterRow<T>> rows;

    AbstractFilter(String kind) {
        this.kind = kind;
    }

    protected abstract Stream<FilterRow<T>> projection(CruiseModelLight cruiseModelLight);


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
        rows.forEach(row -> row.setState(rowShouldBeEnabled(cruises, row) ? ENABLED : DISABLED));

    }

    final void initAllValues(List<CruiseModelLight> cruises) {
        this.rows =
                cruises.stream().flatMap(this::projection)
                        .distinct()
                        .collect(toCollection(() -> new TreeSet<>(this.comparator())));//keep the order
    }

    protected Comparator<FilterRow<T>> comparator() {//this is overridden when needed
        return Comparator.naturalOrder();
    }

    /**
     * Matching a single Filter
     *
     * @param selectedFilterRows The selected rows of a filter.
     * @param cruiseModelLight   A cruise.
     * @return True if the cruise matches that filter.
     */
    private boolean matches(Set<FilterRow<T>> selectedFilterRows, CruiseModelLight cruiseModelLight) {
        return projection(cruiseModelLight).anyMatch(selectedFilterRows::contains);
    }

    /**
     * Should the row be enabled?
     * @param cruises All the cruises left.
     * @param row The row.
     * @return True is any cruise matches the row.
     */
    private boolean rowShouldBeEnabled(Collection<CruiseModelLight> cruises, FilterRow<T> row) {
        return cruises.parallelStream().flatMap(this::projection).anyMatch(row::equals);
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
        return kind.hashCode();
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
