package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.gson.JsonArray;
import com.silversea.aem.models.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRow.singleton;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.DISABLED;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

public class FilterBar {

    public static final AbstractFilter<DestinationItem> DESTINATION =
            new AbstractFilter<DestinationItem>("destination") {
                @Override
                protected Stream<FilterRow<DestinationItem>> projection(CruiseModelLight cruiseModelLight) {
                    DestinationItem destination = cruiseModelLight.getDestination();
                    return FilterRow.singleton(destination, DestinationItem::getTitle, destination.getName());
                }
            };
    public static final AbstractFilter<String> TYPE =
            new AbstractFilter<String>("type") {
                @Override
                protected Stream<FilterRow<String>> projection(CruiseModelLight cruiseModelLight) {
                    return singleton(cruiseModelLight.getCruiseType());
                }
            };

    public static final AbstractFilter<PortItem> PORT =
            new AbstractFilter<PortItem>("port") {
                @Override
                protected Stream<FilterRow<PortItem>> projection(CruiseModelLight cruise) {
                    return cruise.getPorts().stream()
                            .map(port -> new FilterRow<>(port, port.getTitle(), ENABLED));
                }
            };

    public static final AbstractFilter<FeatureModelLight> FEATURES =
            new AbstractFilter<FeatureModelLight>("feature") {
                @Override
                protected Stream<FilterRow<FeatureModelLight>> projection(CruiseModelLight cruise) {
                    return cruise.getFeatures().stream().filter(feature -> feature.getTitle() != null)
                            .map(feature -> new FilterRow<>(feature, feature.getTitle(), ENABLED));
                }
            };

    private static final RangeSet<Integer> DURATIONS = TreeRangeSet.create();

    static {
        DURATIONS.add(Range.closed(1, 8));
        DURATIONS.add(Range.closed(9, 12));
        DURATIONS.add(Range.closed(13, 18));
        DURATIONS.add(Range.atLeast(19));
    }

    public static final AbstractFilter<Range<Integer>> DURATION =
            new AbstractFilter<Range<Integer>>("duration") {
                @Override
                protected Stream<FilterRow<Range<Integer>>> projection(CruiseModelLight cruise) {
                    Range<Integer> integerRange = DURATIONS.rangeContaining(parseInt(cruise.getDuration()));
                    return singleton(integerRange, range -> range.toString(), integerRange.lowerEndpoint().toString());
                }

                protected Comparator<FilterRow<Range<Integer>>> comparator() {
                    return Comparator.comparing(row -> row.getValue().lowerEndpoint());
                }
            };

    public static final AbstractFilter<YearMonth> DEPARTURE =
            new AbstractFilter<YearMonth>("departure") {

                @Override
                protected Stream<FilterRow<YearMonth>> projection(CruiseModelLight cruise) {
                    Calendar startDate = cruise.getStartDate();
                    YearMonth yearMonth = YearMonth.of(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH));
                    return singleton(yearMonth, param -> param.format(DEPARTURE_FORMATTER), yearMonth.toString());
                }

            };

    public static final AbstractFilter<ShipItem> SHIP =
            new AbstractFilter<ShipItem>("ship") {
                @Override
                protected Stream<FilterRow<ShipItem>> projection(CruiseModelLight cruise) {
                    return singleton(cruise.getShip(), ShipItem::getTitle, cruise.getShip().getId());
                }
            };


    public static final Collection<AbstractFilter<?>> FILTERS =
            asList(DURATION, SHIP, DEPARTURE, DESTINATION, FEATURES, PORT, TYPE);
    private static final DateTimeFormatter DEPARTURE_FORMATTER =
            DateTimeFormatter.ofPattern("LLLL yyyy", Locale.ENGLISH);

    void init(List<CruiseModelLight> cruises) {
        FILTERS.forEach(filter -> filter.initAllValues(cruises));
    }

    void addSelectedFilter(String label, String[] selectedKeys) {
        for (AbstractFilter<?> filter : FILTERS) {
            if (filter.getKind().equals(label)) {
                filter.setChosen(new HashSet<>(Arrays.asList(selectedKeys)), ENABLED);
                return;
            }
        }
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
    }

    private void updateNonSelectedFilters(List<CruiseModelLight> cruises) {
        FILTERS.forEach(filter -> {
            if (!filter.isSelected()) {
                filter.disableMissingLabels(cruises);
            }
        });
    }

    private void updateSelectedFilters(List<CruiseModelLight> allCruises) {
        FILTERS.forEach(filter -> {
            if (filter.isSelected()) {
                Set<? extends FilterRow<?>> possibleRows = complementaryProjection(allCruises, filter);
                filter.getRows().forEach(row -> {
                    if (!row.isChosen() && !possibleRows.contains(row)) {
                        //if necessary we can do the revers and set ENABLED if the setChosen put DISABLED by default
                        row.setState(DISABLED);
                    }
                });
            }
        });
    }

    private <T> Set<FilterRow<T>> complementaryProjection(List<CruiseModelLight> allCruises,
                                                          AbstractFilter<T> selectedFilter) {
        Stream<CruiseModelLight> stream = allCruises.stream();
        for (AbstractFilter<?> filter : FILTERS) {
            if (!filter.equals(selectedFilter) && filter.isSelected()) {
                stream = stream.filter(filter::matches);
            }
        }
        return stream.flatMap(selectedFilter::projection).collect(Collectors.toSet());
    }

    boolean anyFilterSelected() {
        return FILTERS.stream().anyMatch(AbstractFilter::isSelected);
    }

    @Override
    public String toString() {
        JsonArray array = new JsonArray();
        FILTERS.forEach(filter -> array.add(filter.toJson()));
        return array.toString();
    }
}
