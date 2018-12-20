package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.ShipItem;

import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;

public  class ShipFilter extends AbstractFilter<ShipItem> {
    public ShipFilter() {
        super("ship");
    }

    @Override
    public Stream<FilterRow<ShipItem>> projection(CruiseModelLight cruise) {
        return Stream.of(new FilterRow<>(cruise.getShip(), ShipItem::getTitle, cruise.getShip().getId(), ENABLED));
    }
}
