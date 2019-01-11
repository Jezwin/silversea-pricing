package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.DestinationItem;

import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;

public class DestinationFilter extends AbstractFilter<DestinationItem> {
    public DestinationFilter() {
        super("destination");
    }

    @Override
    public Stream<FilterRow<DestinationItem>> projection(CruiseModelLight cruiseModelLight) {
        DestinationItem destination = cruiseModelLight.getDestination();
        return Stream.of(new FilterRow<>(destination, DestinationItem::getTitle, cruiseModelLight.getDestinationId(), ENABLED));
    }
}
