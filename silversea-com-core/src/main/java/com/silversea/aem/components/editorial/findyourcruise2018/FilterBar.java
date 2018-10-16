package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silversea.aem.models.CruiseModelLight;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class FilterBar {

    private Map<FilterLabel, Filter> filters;
    private Map<FilterLabel, Set<String>> allValues;

    FilterBar(List<CruiseModelLight> cruises) {
        allValues = initAllValues(cruises);//TODO this could be static?
        filters = new HashMap<>();
        FilterLabel.VALUES.forEach(this::addUnselectedFilter);
    }

    private void addUnselectedFilter(FilterLabel label) {
        filters.put(label, Filter.buildUnselectedFilter(label, allValues(label)));
    }

    void addSelectedFilter(FilterLabel label, Set<String> selectedValues) {
        filters.put(label, Filter.buildSelectedFilter(label, allValues(label), selectedValues));
    }

    boolean isCruiseMatching(CruiseModelLight cruiseModelLight) {
        return filters.values().stream().allMatch(filter -> filter.matches(cruiseModelLight));
    }

    private Map<FilterLabel, Set<String>> initAllValues(List<CruiseModelLight> cruises) {
        Map<FilterLabel, Set<String>> allValues = new HashMap<>();
        for (FilterLabel label : FilterLabel.values()) {
            allValues.put(label, cruises.stream().map(label.getMapper()).flatMap(Collection::stream).collect(toSet()));
        }
        return allValues;
    }

    private Set<String> allValues(FilterLabel label) {
        return allValues.get(label);
    }

    void updateFilters(List<CruiseModelLight> cruises) {
        Map<FilterLabel, Set<String>> validValues = initAllValues(cruises);
        filters.values().forEach(filter -> filter.newValidValues(validValues.get(filter.getLabel())));
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(filters);
    }
}
