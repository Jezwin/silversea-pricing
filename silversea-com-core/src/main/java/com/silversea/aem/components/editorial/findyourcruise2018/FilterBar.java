package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.JsonArray;
import com.silversea.aem.models.*;

import java.util.*;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRow.singleton;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class FilterBar {

    public static final AbstractFilter<DestinationItem> DESTINATION =
            new AbstractFilter<DestinationItem>("destination") {
                @Override
                protected Collection<FilterRow<DestinationItem>> projection(CruiseModelLight cruiseModelLight) {
                    DestinationItem destination = cruiseModelLight.getDestination();
                    return singleton(destination, destination.getTitle(), destination.getName());
                }
            };
    public static final AbstractFilter<String> TYPE =
            new AbstractFilter<String>("type") {
                @Override
                protected Collection<FilterRow<String>> projection(CruiseModelLight cruiseModelLight) {
                    return singleton(cruiseModelLight.getCruiseType());
                }
            };

    public static final AbstractFilter<PortItem> PORT =
            new AbstractFilter<PortItem>("port") {
                @Override
                protected Collection<FilterRow<PortItem>> projection(CruiseModelLight cruise) {
                    return cruise.getPorts().stream()
                            .map(port -> new FilterRow<>(port, port.getTitle(), ENABLED))
                            .collect(toList());
                }
            };

    public static final AbstractFilter<FeatureModelLight> FEATURES =
            new AbstractFilter<FeatureModelLight>("feature") {
                @Override
                protected Collection<FilterRow<FeatureModelLight>> projection(CruiseModelLight cruise) {
                    return cruise.getFeatures().stream().filter(feature -> feature.getTitle() != null)
                            .map(feature -> new FilterRow<>(feature, feature.getTitle(), ENABLED))
                            .collect(toList());
                }
            };

    public static final AbstractFilter<Integer> DURATION =
            new AbstractFilter<Integer>("duration") {
                @Override
                protected Collection<FilterRow<Integer>> projection(CruiseModelLight cruise) {
                    return singleton(parseInt(cruise.getDuration()), cruise.getDuration());
                }

                @Override
                protected Comparator<FilterRow<Integer>> comparator() {
                    return Comparator.comparing(FilterRow::getValue);
                }
            };

    public static final AbstractFilter<Calendar> DEPARTURE =
            new AbstractFilter<Calendar>("departure") {
                @Override
                protected Collection<FilterRow<Calendar>> projection(CruiseModelLight cruise) {
                    return singleton(cruise.getStartDate(), "TODO", cruise.getStartDate().getTimeInMillis() + "");
                }
            };

    public static final AbstractFilter<ShipItem> SHIP =
            new AbstractFilter<ShipItem>("ship") {
                @Override
                protected Collection<FilterRow<ShipItem>> projection(CruiseModelLight cruise) {
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
        return FILTERS.stream().allMatch(filter -> filter.matches(cruiseModelLight));
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
