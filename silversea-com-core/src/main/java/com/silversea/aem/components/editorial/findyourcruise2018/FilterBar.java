package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.JsonArray;
import com.silversea.aem.models.*;

import java.util.*;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRow.singleton;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

public class FilterBar {

    public static final AbstractFilter<DestinationItem> DESTINATION =
            new AbstractFilter<DestinationItem>("destination") {
                @Override
                protected Stream<FilterRow<DestinationItem>> projection(CruiseModelLight cruiseModelLight) {
                    DestinationItem destination = cruiseModelLight.getDestination();
                    return FilterRow.singleton(destination, destination.getTitle(), destination.getName());
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

    public static final AbstractFilter<Integer> DURATION =
            new AbstractFilter<Integer>("duration") {
                @Override
                protected Stream<FilterRow<Integer>> projection(CruiseModelLight cruise) {
                    return singleton(parseInt(cruise.getDuration()), cruise.getDuration());
                }

                protected Comparator<FilterRow<Integer>> comparator() {
                    return Comparator.comparing(FilterRow::getValue);
                }
            };

    public static final AbstractFilter<Calendar> DEPARTURE =
            new AbstractFilter<Calendar>("departure") {
                @Override
                protected Stream<FilterRow<Calendar>> projection(CruiseModelLight cruise) {
                    return singleton(cruise.getStartDate(), "TODO", cruise.getStartDate().getTimeInMillis() + "");
                }
            };

    public static final AbstractFilter<ShipItem> SHIP =
            new AbstractFilter<ShipItem>("ship") {
                @Override
                protected Stream<FilterRow<ShipItem>> projection(CruiseModelLight cruise) {
                    return singleton(cruise.getShip(), cruise.getShip().getTitle(), cruise.getShip().getId());
                }
            };


    private final List<CruiseModelLight> cruises;
    public static final Collection<AbstractFilter<?>> FILTERS =
            asList(DURATION, SHIP, DEPARTURE, DESTINATION, FEATURES, PORT, TYPE);

    FilterBar(List<CruiseModelLight> cruises) {
        this.cruises = cruises;
        FILTERS.forEach(this::addUnselectedFilter);
    }

    private void addUnselectedFilter(AbstractFilter<?> filter) {
        filter.initAllValues(cruises);
    }

    void addSelectedFilter(String label, String[] selectedKeys) {
        for (AbstractFilter<?> filter : FILTERS) {
            if (filter.getKind().equals(label)) {
                filter.setEnabled(new HashSet<>(Arrays.asList(selectedKeys)));
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


    void updateFilters(List<CruiseModelLight> cruises) {
        FILTERS.forEach(filter -> {
            if (!filter.isSelected()) {
                filter.disableMissingLabels(cruises);
            }
        });
    }

    @Override
    public String toString() {
        JsonArray array = new JsonArray();
        FILTERS.forEach(filter -> array.add(filter.toJson()));
        return array.toString();
    }
}
