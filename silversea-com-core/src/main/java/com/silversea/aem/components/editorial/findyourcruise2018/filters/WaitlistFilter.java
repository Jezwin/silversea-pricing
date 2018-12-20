package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;

public class WaitlistFilter extends AbstractFilter<String> {

    public WaitlistFilter() {
        super("waitlist");
    }

    @Override
    public Stream<FilterRow<String>> projection(CruiseModelLight cruiseModelLight) {
        String price = Optional.ofNullable(cruiseModelLight.getLowestPrices().get("ftUSD")).map(value -> "no_waitlist").orElse("waitlist");
        return Stream.of(new FilterRow<>(price, mappedPrice -> mappedPrice.equals("waitlist") ? "waitlist" : "", price, ENABLED));
    }

    @Override
    public String[] selectedKeys(Map<String, String[]> properties, Map<String, String[]> httpRequest) {
        if (Optional.ofNullable(properties.get("preFilterWaitlist")).map(arr -> arr[0]).map("true"::equals).orElse(false)) {
            this.setVisible(false);
            return new String[]{"no_waitlist"};
        }
        return super.selectedKeys(properties, httpRequest);
    }
}
