package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.DestinationItem;

import java.util.Comparator;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;

public class DestinationFilter extends AbstractFilter<DestinationItem> {
    public static final String KIND = "destination";


    public DestinationFilter() {
        super(KIND);
    }

    @Override
    public Stream<FilterRow<DestinationItem>> projection(CruiseModelLight cruiseModelLight) {
        DestinationItem destination = cruiseModelLight.getDestination();
        return Stream.of(new FilterRow<>(destination, DestinationItem::getTitle, cruiseModelLight.getDestinationId(), ENABLED));
    }

    @Override
    protected Comparator<FilterRow<DestinationItem>> comparator() {
        return Comparator.comparing(FilterRow::getLabel);
    }
}
