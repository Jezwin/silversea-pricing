package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.base.Strings;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.FeatureModelLight;
import com.silversea.aem.models.PortItem;

import java.util.function.Function;

import static java.util.stream.Collectors.joining;

public enum FilterLabel {
    DESTINATION("destination", cruise -> cruise.getDestination().getName()),
    DEPARTURE("departure", cruise -> "TODO"),//TODO
    DURATION("duration", CruiseModelLight::getDuration),
    SHIP("ship", cruise -> cruise.getShip().getName()),
    TYPE("type", CruiseModelLight::getCruiseType),
    PORT("port", cruise -> cruise.getPorts().stream().map(PortItem::getName).filter(name -> !"day-at-sea".equals(name))
            .collect(joining(":"))),
    FEATURES("features", cruise -> cruise.getFeatures().stream().map(FeatureModelLight::getName)
            .filter(string -> !Strings.isNullOrEmpty(string)).collect(joining(":")));

    private String param;
    private String label;

    private Function<CruiseModelLight, String> mapper;

    FilterLabel(String label, Function<CruiseModelLight, String> mapper) {
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

    public Function<CruiseModelLight, String> getMapper() {
        return mapper;
    }
}
