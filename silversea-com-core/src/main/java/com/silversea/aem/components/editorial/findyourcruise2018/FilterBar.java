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
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class FilterBar {

    private final AbstractFilter<YearMonth> departure = new DepartureFilter();
    private final AbstractFilter<DestinationItem> destination = new DestinationFilter();
    private final AbstractFilter<Range<Integer>> duration = new DurationFilter();
    private final AbstractFilter<ExclusiveOfferModelLight> offers = new OffersFilter();
    private final AbstractFilter<FeatureModelLight> features = new FeatureFilter();
    private final AbstractFilter<PortItem> port = new PortFilter();
    private final AbstractFilter<String> waitlist = new WaitlistFilter();
    private final AbstractFilter<ShipItem> ship = new ShipFilter();
    private final AbstractFilter<String> type = new TypeFilter();
    private final AbstractFilter<String> country = new CountryFilter();


    private final Collection<AbstractFilter<?>> filters =
            asList(duration, ship, departure, destination, features, port, country, type, offers, waitlist);
    private final DepartureSortOption DEFAULT_SORT = new DepartureSortOption(ASC);

    private static final Map<String, Function<SortOptionState, AbstractSortOption>> SORT_OPTIONS = new HashMap<>();

    static {
        SORT_OPTIONS.put(DepartureSortOption.kind, DepartureSortOption::new);
        SORT_OPTIONS.put(DurationSortOption.kind, DurationSortOption::new);
        SORT_OPTIONS.put(PriceSortOption.KIND, PriceSortOption::new);
    }


    private Map<String, AbstractSortOption> sortedBy;

    void init(FindYourCruise2018Use use, Map<String, String[]> httpRequest, List<CruiseModelLight> allCruises) {
        Map<String, String[]> properties = use.filteringSettings();
        for (AbstractFilter<?> filter : filters) {
            filter.initAllValues(use, filter.selectedKeys(properties, httpRequest), allCruises);
        }
        setOpen(httpRequest.getOrDefault("open", new String[]{"allclose"}));
        setSortBy(httpRequest.getOrDefault("sortby", new String[]{"departure-asc"}));
    }

    boolean isCruiseMatching(CruiseModelLight cruiseModelLight) {
        for (AbstractFilter<?> filter : filters) {
            if (!filter.matches(cruiseModelLight)) {
                return false;
            }
        }
        return true;
    }

    void updateFilters(List<CruiseModelLight> allCruises, List<CruiseModelLight> filteredCruises) {
        updateNonSelectedFilters(filteredCruises);
        updateSelectedFilters(allCruises);

    }


    private void updateNonSelectedFilters(List<CruiseModelLight> cruises) {
        for (AbstractFilter<?> filter : filters) {
            if (!filter.isSelected()) {
                filter.disableMissingLabels(cruises);
            }
        }
    }

    private void updateSelectedFilters(List<CruiseModelLight> allCruises) {
        for (AbstractFilter<?> filter : filters) {
            if (filter.isSelected()) {
                Set<? extends FilterRow<?>> possibleRows = complementaryProjection(allCruises, filter);
                filter.getRows().forEach(row -> {
                    if (!row.isChosen() && !possibleRows.contains(row)) {
                        //if necessary we can do the revers and set ENABLED if the setChosen put DISABLED by default
                        filter.disableRow(row);
                    }
                });
            }
        }
    }

    private <T> Set<FilterRow<T>> complementaryProjection(List<CruiseModelLight> allCruises, AbstractFilter<T> selectedFilter) {
        Stream<CruiseModelLight> stream = allCruises.stream();
        for (AbstractFilter<?> filter : filters) {
            if (!filter.equals(selectedFilter) && filter.isSelected()) {
                stream = stream.filter(filter::matches);
            }
        }
        return stream.flatMap(selectedFilter::projection).collect(toSet());
    }

    boolean anyFilterSelected() {
        return filters.stream().anyMatch(AbstractFilter::isSelected);
    }

    @Override
    public String toString() {
        JsonArray array = new JsonArray();
        for (AbstractFilter<?> filter : filters) {
            array.add(filter.toJson());
        }
        return array.toString();
    }

    public void setOpen(String... kinds) {
        for (AbstractFilter filter : filters) {
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
        return getSort(PriceSortOption.KIND);
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

    public AbstractFilter<YearMonth> getDeparture() {
        return departure;
    }

    public AbstractFilter<DestinationItem> getDestination() {
        return destination;
    }

    public AbstractFilter<Range<Integer>> getDuration() {
        return duration;
    }

    public AbstractFilter<ExclusiveOfferModelLight> getOffers() {
        return offers;
    }

    public AbstractFilter<FeatureModelLight> getFeatures() {
        return features;
    }

    public AbstractFilter<PortItem> getPort() {
        return port;
    }

    public AbstractFilter<String> getWaitlist() {
        return waitlist;
    }

    public AbstractFilter<ShipItem> getShip() {
        return ship;
    }

    public AbstractFilter<String> getType() {
        return type;
    }

    public Collection<AbstractFilter<?>> getFilters() {
        return filters;
    }

    public AbstractFilter<String> getCountry() {
        return country;
    }
}
