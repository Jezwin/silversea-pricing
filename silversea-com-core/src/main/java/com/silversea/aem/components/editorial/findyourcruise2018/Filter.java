package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.Set;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.DISABLED;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.util.stream.Collectors.toMap;

public class Filter {
    private FilterLabel label;
    private Map<String, FilterRowState> rows;


    public Filter(FilterLabel label, Map<String, FilterRowState> rows) {
        this.label = label;
        this.rows = rows;
    }

    public static Filter buildSelectedFilter(FilterLabel label, Set<String> allValues, Set<String> selectedValues) {
        return buildFilter(label, allValues, selectedValues, FilterRowState.CHOSEN, ENABLED);
    }

    public static Filter buildUnselectedFilter(FilterLabel label, Set<String> allValues) {
        return new Filter(label, allValues.stream().collect(toMap(row -> row, row -> ENABLED)));
    }

    public static Filter buildUnselectedFilter(FilterLabel label, Set<String> allValues, Set<String> availableOptions) {
        return buildFilter(label, allValues, availableOptions, ENABLED, DISABLED);
    }

    private static Filter buildFilter(FilterLabel label, Set<String> allValues, Set<String> subSet,
                                      FilterRowState subSetState, FilterRowState subSetComplementaryState) {
        Map<String, FilterRowState> map = allValues.stream()
                .collect(toMap(row -> row, row -> subSet.contains(row) ? subSetState : subSetComplementaryState));
        return new Filter(label, map);
    }

    public boolean isSelected() {
        return rows.values().stream().anyMatch(FilterRowState.CHOSEN::equals);
    }

    public boolean matches(Set<String> matchingValues) {
        //TODO
        return true;
    }

    public void newValidValues(Set<String> validValues) {
        rows.replaceAll((key, state) -> validValues.contains(key) ? ENABLED : DISABLED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return label == filter.label;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    public FilterLabel getLabel() {
        return label;
    }

    public Map<String, FilterRowState> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

}
