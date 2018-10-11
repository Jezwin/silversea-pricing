package com.silversea.aem.components.editorial.findyourcruise2018;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilterBar {
    private final Map<FilterLabel, Filter> filters;

    public FilterBar() {
        Map<FilterLabel, Filter> filters = new HashMap<>();
        for (FilterLabel filter : FilterLabel.values()) {
            filters.put(filter, Filter.buildDefaultFilter(filter, null, null));
        }
        this.filters = filters;
    }

    public Collection<Filter> getFilters() {
        return filters.values();
    }

    public void updateFilter(Filter filter) {
        filters.put(filter.getLabel(), filter);
    }

}
