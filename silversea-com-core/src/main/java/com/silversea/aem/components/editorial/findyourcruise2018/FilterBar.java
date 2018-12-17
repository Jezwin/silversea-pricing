package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.collect.Range;
import com.google.gson.JsonArray;
import com.silversea.aem.models.*;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRow.singleton;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.DISABLED;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

public class FilterBar {

    public static final AbstractFilter<DestinationItem> DESTINATION =
            new AbstractFilter<DestinationItem>("destination",  Comparator.comparing(cruise -> cruise.getDestination().getName()), AbstractFilter.Sorting.HIDDEN) {
                @Override
                protected Stream<FilterRow<DestinationItem>> projection(CruiseModelLight cruiseModelLight) {
                    DestinationItem destination = cruiseModelLight.getDestination();
                    return Stream.of(new FilterRow<>(destination, DestinationItem::getTitle, cruiseModelLight.getDestinationId(), ENABLED));
                }
            };
    public static final AbstractFilter<String> TYPE =
            new AbstractFilter<String>("type", Comparator.comparing(CruiseModelLight::getCruiseType), AbstractFilter.Sorting.HIDDEN) {
                @Override
                protected Stream<FilterRow<String>> projection(CruiseModelLight cruiseModelLight) {
                    return singleton(cruiseModelLight.getCruiseType());
                }
            };

    public static final AbstractFilter<PortItem> PORT =
            new AbstractFilter<PortItem>("port", Comparator.comparing(cruise -> ""), AbstractFilter.Sorting.HIDDEN) {
                @Override
                protected Stream<FilterRow<PortItem>> projection(CruiseModelLight cruise) {
                    return cruise.getPorts().stream()
                            .map(port -> new FilterRow<>(port, PortItem::getTitle, port.getName(), ENABLED));
                }

                @Override
                protected void postFilter() {
                    getRows().removeIf(FilterRow::isDisabled);
                }
            };

    public static final AbstractFilter<ExclusiveOfferModelLight> OFFERS =
            new AbstractFilter<ExclusiveOfferModelLight>("eo",  Comparator.comparing(cruise -> ""), AbstractFilter.Sorting.HIDDEN) {
                @Override
                protected Stream<FilterRow<ExclusiveOfferModelLight>> projection(CruiseModelLight cruiseModelLight) {
                    return cruiseModelLight.getExclusiveOffers().stream()
                            .map(offer -> new FilterRow<>(offer, ExclusiveOfferModelLight::getTitle, offer.getPath(), ENABLED));
                }

                @Override
                public boolean isVisible() {
                    return false;//this is a hidden filter!
                }
            };


    public static final AbstractFilter<FeatureModelLight> FEATURES = new FeatureFilter();

    public static final AbstractFilter<Range<Integer>> DURATION = new DurationFilter();

    public static final AbstractFilter<YearMonth> DEPARTURE = new DepartureFilter();

    public static final AbstractFilter<ShipItem> SHIP =
            new AbstractFilter<ShipItem>("ship", Comparator.comparing(cruise -> cruise.getShip().getName()), AbstractFilter.Sorting.HIDDEN) {
                @Override
                protected Stream<FilterRow<ShipItem>> projection(CruiseModelLight cruise) {
                    return Stream.of(new FilterRow<>(cruise.getShip(), ShipItem::getTitle, cruise.getShip().getId(), ENABLED));
                }
            };

    public static final AbstractFilter<String> PRICE =
            new PriceFilter();


    public static final Collection<AbstractFilter<?>> FILTERS =
            asList(DURATION, SHIP, DEPARTURE, DESTINATION, FEATURES, PORT, TYPE, OFFERS, PRICE);

    public static Comparator<? super CruiseModelLight> getComparator(FindYourCruise2018Use findYourCruise2018Use) {
        for (AbstractFilter<?> filter : FilterBar.FILTERS) {
            if(AbstractFilter.Sorting.ASC.equals(filter.getSorting()) || AbstractFilter.Sorting.DESC.equals(filter.getSorting())) {
                return filter.getSortedBy(findYourCruise2018Use);
            }
        }
        return Comparator.comparing(CruiseModelLight::getStartDate);
    }

    void init(FindYourCruise2018Use use, Map<String, String[]> httpRequest, List<CruiseModelLight> allCruises) {
        Map<String, String[]> properties = use.filteringSettings();
        for (AbstractFilter<?> filter : FILTERS) {
            filter.initAllValues(use, filter.selectedKeys(properties, httpRequest), allCruises);
        }
        setOpen(httpRequest.getOrDefault("open", new String[]{"allclose"}));
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

}
