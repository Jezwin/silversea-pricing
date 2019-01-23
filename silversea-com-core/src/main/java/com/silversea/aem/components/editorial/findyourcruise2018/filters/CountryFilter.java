package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;

import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;

public class CountryFilter extends AbstractFilter<String> {
    public static final String KIND = "country";

    public CountryFilter() {
        super(KIND);
    }

    @Override
    public Stream<FilterRow<String>> projection(CruiseModelLight cruise) {
        return cruise.getCountries().stream().map(iso -> new FilterRow<>(iso, iso, ENABLED));
    }

}
