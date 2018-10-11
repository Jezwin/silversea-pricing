package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Filter {

    private final FilterLabel label;
    private final Map<String, FilterState> values;

    private Filter(FilterLabel label, Map<String, FilterState> values) {
        this.label = label;
        this.values = values;
    }

    public static Filter buildSelectedFilter(FilterLabel label, Set<String> allValues,
                                             Set<String> selectedValues) {
        return buildFilter(label, allValues, selectedValues, FilterState.CHOSEN,
                FilterState.ENABLED);
    }


    public static Filter buildDefaultFilter(FilterLabel label, Set<String> allValues,
                                            Set<String> availableOptions) {
        return buildFilter(label, allValues, availableOptions, FilterState.ENABLED,
                FilterState.DISABLED);
    }

    private static Filter buildFilter(FilterLabel label, Set<String> allValues,
                                      Set<String> subSet,
                                      FilterState subSetState,
                                      FilterState subSetComplementaryState) {
        Map<String, FilterState> values = new HashMap<>();
        for (String value : allValues) {
            if (subSet.contains(value)) {
                values.put(value, subSetState);
            } else {
                values.put(value, subSetComplementaryState);
            }

        }
        return new Filter(label, values);
    }

    public FilterLabel getLabel() {
        return label;
    }

    public Map<String, FilterState> getValues() {
        return values;
    }

    public Set<String> availableValues() {
        return values.entrySet().stream().filter(entry -> !entry.getValue().equals(FilterState.DISABLED))
                .map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(getValues());
    }
}
