package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silversea.aem.models.*;

import java.util.*;

import static com.silversea.aem.components.editorial.findyourcruise2018.AbstractFilter.ValueLabel.singleton;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class FilterBar {

    public static final AbstractFilter<DestinationItem> DESTINATION =
            new AbstractFilter<DestinationItem>("destination") {
                @Override
                protected Collection<ValueLabel<DestinationItem>> valueLabels(CruiseModelLight cruiseModelLight) {
                    return singleton(cruiseModelLight.getDestination().getName(), cruiseModelLight.getDestination());
                }
            };
    public static final AbstractFilter<String> TYPE =
            new AbstractFilter<String>("type") {
                @Override
                protected Collection<ValueLabel<String>> valueLabels(CruiseModelLight cruiseModelLight) {
                    return ValueLabel.singleton(cruiseModelLight.getCruiseType());
                }
            };

    public static final AbstractFilter<PortItem> PORT =
            new AbstractFilter<PortItem>("port") {
                @Override
                protected Collection<ValueLabel<PortItem>> valueLabels(CruiseModelLight cruise) {
                    return cruise.getPorts().stream().map(port -> new ValueLabel<>(port.getName(), port))
                            .collect(toList());
                }
            };

    public static final AbstractFilter<FeatureModelLight> FEATURES =
            new AbstractFilter<FeatureModelLight>("feature") {
                @Override
                protected Collection<ValueLabel<FeatureModelLight>> valueLabels(CruiseModelLight cruise) {
                    return cruise.getFeatures().stream().map(feature -> new ValueLabel<>(feature.getName(), feature))
                            .collect(toList());
                }
            };

    public static final AbstractFilter<Integer> DURATION =
            new AbstractFilter<Integer>("duration") {
                @Override
                protected Collection<ValueLabel<Integer>> valueLabels(CruiseModelLight cruise) {
                    if (cruise.getDuration() == null) {
                        return Collections.emptyList();
                    }
                    return ValueLabel
                            .singleton(cruise.getDuration(), parseInt(cruise.getDuration()));
                }

            };

    public static final AbstractFilter<Calendar> DEPARTURE =
            new AbstractFilter<Calendar>("departure") {
                @Override
                protected Collection<ValueLabel<Calendar>> valueLabels(CruiseModelLight cruise) {
                    if (cruise.getStartDate() == null) {
                        return Collections.emptyList();
                    }
                    return ValueLabel.singleton("TODO", firstNonNull(cruise.getStartDate(), Calendar.getInstance()));
                }
            };

    public static final AbstractFilter<ShipItem> SHIP =
            new AbstractFilter<ShipItem>("ship") {
                @Override
                protected Collection<ValueLabel<ShipItem>> valueLabels(CruiseModelLight cruise) {
                    return ValueLabel.singleton(cruise.getShip().getName(), cruise.getShip());
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

    void addSelectedFilter(String label, String[] selectedValues) {
        for (AbstractFilter<?> filter : FILTERS) {
            if (filter.getKind().equals(label)) {
                filter.setLabels(new HashSet<>(Arrays.asList(selectedValues)));
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
        Gson gson = new GsonBuilder().create();
        return gson.toJson(FILTERS);
    }
}
