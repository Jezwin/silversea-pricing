package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;
import static java.util.Optional.ofNullable;

public class WaitlistFilter extends AbstractFilter<String> {

    public static final String[] HIDE_WAITLIST = {"no_waitlist"};

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
            return HIDE_WAITLIST;
        }
        setVisible(true);
        return httpRequest.getOrDefault(getKind(), HIDE_WAITLIST);
    }
}
