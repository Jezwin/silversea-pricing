package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.PriceModelLight;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;

class PriceFilter extends AbstractFilter<String> {

    public PriceFilter() {
        super("price", retrieveComparator(), Sorting.NONE);
    }

    private static BiFunction<FindYourCruise2018Use, Sorting, Comparator<CruiseModelLight>> retrieveComparator() {
        return (use, sorting) -> (Comparator<CruiseModelLight>) (cruise1, cruise2) -> {
            long computedPrice1 = Optional.ofNullable(cruise1.getLowestPrices().get(use.getMarketCurrency())).map(PriceModelLight::getComputedPrice).orElseGet(() -> waitlistValue(sorting));
            long computedPrice2 = Optional.ofNullable(cruise2.getLowestPrices().get(use.getMarketCurrency())).map(PriceModelLight::getComputedPrice).orElseGet(() -> waitlistValue(sorting));
            if (Sorting.ASC.equals(sorting)) {
                return Long.compare(computedPrice1, computedPrice2);
            } else {
                return Long.compare(computedPrice2, computedPrice1);
            }
        };
    }

    private static Long waitlistValue(Sorting sorting) {
        switch (sorting) {
            case ASC:
                return Long.MAX_VALUE;
            case DESC:
                return Long.MIN_VALUE;
            default:
                return 0L;
        }
    }

    @Override
    protected Stream<FilterRow<String>> projection(CruiseModelLight cruiseModelLight) {
        String price = Optional.ofNullable(cruiseModelLight.getLowestPrices().get("ftUSD")).map(PriceModelLight::getComputedPrice).map(longValue -> longValue + "").orElse("WAITLIST");
        return Stream.of(new FilterRow<>(price, price, ENABLED));
    }

    @Override
    public boolean isVisible() {
        return false;//this is a hidden filter!
    }
}
