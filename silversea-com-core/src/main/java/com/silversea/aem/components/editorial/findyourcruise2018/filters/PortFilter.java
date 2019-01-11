package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.PortItem;

import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;

public class PortFilter extends AbstractFilter<PortItem> {
    public PortFilter() {
        super("port");
    }

    @Override
    public Stream<FilterRow<PortItem>> projection(CruiseModelLight cruise) {
        return cruise.getPorts().stream()
                .map(port -> new FilterRow<>(port, PortItem::getTitle, port.getName(), ENABLED));
    }

    @Override
    public void postFilter() {
        getRows().removeIf(FilterRow::isDisabled);
    }
}
