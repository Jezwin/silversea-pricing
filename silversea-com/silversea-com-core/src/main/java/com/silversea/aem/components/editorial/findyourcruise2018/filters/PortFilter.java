package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.PortItem;

import java.util.List;
import java.util.logging.Filter;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;
import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.NOT_VISIBLE;

public class PortFilter extends AbstractFilter<PortItem> {
    public static final String KIND = "port";
    private List<FilterRow<PortItem>> visiblePorts;
    public PortFilter() {
        super(KIND);
    }

    @Override
    public Stream<FilterRow<PortItem>> projection(CruiseModelLight cruise) {
        return cruise.getPorts().stream()
                .map(port -> new FilterRow<>(port, PortItem::getTitle, port.getName(), ENABLED));
    }

    @Override
    public void disableRow(FilterRow<?> row) {
        row.setState(NOT_VISIBLE);
    }

    public List<FilterRow<PortItem>> getVisibleRows() {
        if (visiblePorts == null) {
            visiblePorts = getRows().stream().filter(row-> !row.isNotVisible()).collect(Collectors.toList());
        }
        return visiblePorts;
    }
}
