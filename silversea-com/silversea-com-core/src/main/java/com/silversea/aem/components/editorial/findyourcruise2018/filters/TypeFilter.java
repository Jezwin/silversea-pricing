package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;

import java.util.stream.Stream;

public class TypeFilter extends AbstractFilter<String> {
    public TypeFilter() {
        super("type");
    }

    @Override
    public Stream<FilterRow<String>> projection(CruiseModelLight cruiseModelLight) {
        return FilterRow.singleton(cruiseModelLight.getCruiseType());
    }
}
