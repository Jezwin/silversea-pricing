package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.collect.Range;
import com.google.gson.JsonArray;
import com.silversea.aem.components.editorial.findyourcruise2018.AbstractSortOption.DepartureSortOption;
import com.silversea.aem.components.editorial.findyourcruise2018.AbstractSortOption.DurationSortOption;
import com.silversea.aem.components.editorial.findyourcruise2018.AbstractSortOption.PriceSortOption;
import com.silversea.aem.components.editorial.findyourcruise2018.AbstractSortOption.SortOptionState;
import com.silversea.aem.components.editorial.findyourcruise2018.filters.*;
import com.silversea.aem.models.*;

import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.AbstractSortOption.SortOptionState.ASC;
import static com.silversea.aem.components.editorial.findyourcruise2018.AbstractSortOption.SortOptionState.NON_SELECTED;
import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.DISABLED;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class FilterBar {

    public static final AbstractFilter<YearMonth> DEPARTURE = new DepartureFilter();
    public static final AbstractFilter<DestinationItem> DESTINATION = new DestinationFilter();
    public static final AbstractFilter<Range<Integer>> DURATION = new DurationFilter();
    public static final AbstractFilter<ExclusiveOfferModelLight> OFFERS = new OffersFilter();
    public static final AbstractFilter<FeatureModelLight> FEATURES = new FeatureFilter();
    public static final AbstractFilter<PortItem> PORT = new PortFilter();
    public static final AbstractFilter<String> WAITLIST = new WaitlistFilter();
    public static final AbstractFilter<ShipItem> SHIP = new ShipFilter();
    public static final AbstractFilter<String> TYPE = new TypeFilter();

    public static final Collection<AbstractFilter<?>> FILTERS =
            asList(DURATION, SHIP, DEPARTURE, DESTINATION, FEATURES, PORT, TYPE, OFFERS, WAITLIST);
    private static final DepartureSortOption DEFAULT_SORT = new DepartureSortOption(ASC);

    private static final Map<String, Function<SortOptionState, AbstractSortOption>> SORT_OPTIONS = new HashMap<>();

    static {
        SORT_OPTIONS.put(DepartureSortOption.kind, DepartureSortOption::new);
        SORT_OPTIONS.put(DurationSortOption.kind, DurationSortOption::new);
        SORT_OPTIONS.put(PriceSortOption.kind, PriceSortOption::new);
    }


    private Map<String, AbstractSortOption> sortedBy;

    void init(FindYourCruise2018Use use, Map<String, String[]> httpRequest, List<CruiseModelLight> allCruises) {
        Map<String, String[]> properties = use.filteringSettings();
        for (AbstractFilter<?> filter : FILTERS) {
            filter.initAllValues(use, filter.selectedKeys(properties, httpRequest), allCruises);
        }
        setOpen(httpRequest.getOrDefault("open", new String[]{"allclose"}));
        setSortBy(httpRequest.getOrDefault("sortby", new String[]{"departure-asc"}));
    }

    boolean isCruiseMatching(CruiseModelLight cruiseModelLight) {
        for (AbstractFilter<?> filter : FILTERS) {
            if (!filter.matches(cruiseModelLight)) {
                return false;
            }
        }
        return true;
    }

    void updateFilters(List<CruiseModelLight> allCruises, List<CruiseModelLight> filteredCruises) {
        updateNonSelectedFilters(filteredCruises);
        updateSelectedFilters(allCruises);
        postFiltering();
    }

    private void postFiltering() {
        for (AbstractFilter<?> filter : FILTERS) {
            filter.postFilter();
        }
    }

    private void updateNonSelectedFilters(List<CruiseModelLight> cruises) {
        for (AbstractFilter<?> filter : FILTERS) {
            if (!filter.isSelected()) {
                filter.disableMissingLabels(cruises);
            }
        }
    }

    private void updateSelectedFilters(List<CruiseModelLight> allCruises) {
        for (AbstractFilter<?> filter : FILTERS) {
            if (filter.isSelected()) {
                Set<? extends FilterRow<?>> possibleRows = complementaryProjection(allCruises, filter);
                filter.getRows().forEach(row -> {
                    if (!row.isChosen() && !possibleRows.contains(row)) {
                        //if necessary we can do the revers and set ENABLED if the setChosen put DISABLED by default
                        row.setState(DISABLED);
                    }
                });
            }
        }
    }

    private <T> Set<FilterRow<T>> complementaryProjection(List<CruiseModelLight> allCruises, AbstractFilter<T> selectedFilter) {
        Stream<CruiseModelLight> stream = allCruises.stream();
        for (AbstractFilter<?> filter : FILTERS) {
            if (!filter.equals(selectedFilter) && filter.isSelected()) {
                stream = stream.filter(filter::matches);
            }
        }
        return stream.flatMap(selectedFilter::projection).collect(toSet());
    }

    boolean anyFilterSelected() {
        return FILTERS.stream().anyMatch(AbstractFilter::isSelected);
    }

    @Override
    public String toString() {
        JsonArray array = new JsonArray();
        for (AbstractFilter<?> filter : FILTERS) {
            array.add(filter.toJson());
        }
        return array.toString();
    }

    public void setOpen(String... kinds) {
        for (AbstractFilter filter : FILTERS) {
            for (String kind : kinds) {
                filter.setOpen(kind.equalsIgnoreCase(filter.getKind()));
            }
        }
    }

    public void setSortBy(String... selected) {
        //es price-desc
        Map<String, SortOptionState> sorted = stream(selected).filter(value -> value.contains("-"))
                .collect(toMap(
                        value -> value.split("-")[0],
                        value -> of(value.split("-")).filter(splitted -> splitted.length > 1).map(splitted -> splitted[1]).
                                map(splitted -> SortOptionState.valueOf(splitted.toUpperCase())).orElse(NON_SELECTED)));
        sortedBy = SORT_OPTIONS.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().apply(sorted.getOrDefault(entry.getKey(), NON_SELECTED))));
    }

    private String getSort(String kind) {
        return sortedBy.get(kind).getState().toString();
    }

    public String getDepartureSorting() {
        return getSort(DepartureSortOption.kind);
    }

    public String getPriceSorting() {
        return getSort(PriceSortOption.kind);
    }

    public String getDurationSorting() {
        return getSort(DurationSortOption.kind);
    }

    private Map<String, AbstractSortOption> getSortedBy() {
        return sortedBy;
    }

    public Comparator<? super CruiseModelLight> comparator(FindYourCruise2018Use use) {
        return getSortedBy().values().stream()
                .filter(sorter -> !sorter.getState().equals(NON_SELECTED))
                .map(sorter -> sorter.comparator(use))
                .reduce(Comparator::thenComparing)
                .orElseGet(() -> DEFAULT_SORT.comparator(use));
    }

}
