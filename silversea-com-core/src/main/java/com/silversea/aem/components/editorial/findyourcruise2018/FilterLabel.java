package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.base.Strings;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.FeatureModelLight;
import com.silversea.aem.models.PortItem;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

public enum FilterLabel {
    DESTINATION("destination", cruise -> singleton(cruise.getDestination().getName())),
    DEPARTURE("departure", cruise -> singleton("TODO")),//TODO
    DURATION("duration", cruiseModelLight -> singleton(cruiseModelLight.getDuration())),
    SHIP("ship", cruise -> singleton(cruise.getShip().getName())),
    TYPE("type", cruiseModelLight -> singleton(cruiseModelLight.getCruiseType())),
    PORT("port", cruise -> cruise.getPorts().stream().map(PortItem::getName).filter(name -> !"day-at-sea".equals(name))
            .collect(toSet())),
    FEATURES("features", cruise -> cruise.getFeatures().stream().map(FeatureModelLight::getName)
            .filter(string -> !Strings.isNullOrEmpty(string)).collect(toSet()));

    private String param;
    private String label;
    private Function<CruiseModelLight, Set<String>> mapper;

    public static final Collection<FilterLabel> VALUES = Arrays.asList(values());

    FilterLabel(String label, Function<CruiseModelLight, Set<String>> mapper) {
        this.param = label;
        this.label = label;
        this.mapper = mapper;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getParam() {
        return param;
    }

    public String getLabel() {
        return label;
    }

    public Function<CruiseModelLight, Set<String>> getMapper() {
        return mapper;
    }

}
