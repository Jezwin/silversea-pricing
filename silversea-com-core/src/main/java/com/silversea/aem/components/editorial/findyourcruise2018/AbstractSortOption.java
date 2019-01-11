package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.PriceModelLight;

import java.util.Comparator;

import static java.util.Optional.ofNullable;

public abstract class AbstractSortOption {


    private final SortOptionState state;
    private final String kind;

    protected AbstractSortOption(SortOptionState state, String kind) {
        this.state = state;
        this.kind = kind;
    }

    protected abstract Comparator<CruiseModelLight> internalComparator(FindYourCruise2018Use use);

    public SortOptionState getState() {
        return state;
    }

    protected boolean isAsc() {
        return SortOptionState.ASC == getState();
    }

    public String getKind() {
        return kind;
    }


    public Comparator<CruiseModelLight> comparator(FindYourCruise2018Use use) {
        return isAsc() ? internalComparator(use) : internalComparator(use).reversed();
    }

    public static class PriceSortOption extends AbstractSortOption {
        public static final String kind = "price";


        public PriceSortOption(SortOptionState state) {
            super(state, "price");
        }

        @Override
        protected Comparator<CruiseModelLight> internalComparator(FindYourCruise2018Use use) {
            return (cruise1, cruise2) -> {
                long computedPrice1 = ofNullable(cruise1.getLowestPrices().get(use.getMarketCurrency())).map(PriceModelLight::getComputedPrice)
                        .orElseGet(this::waitlistValue);
                long computedPrice2 = ofNullable(cruise2.getLowestPrices().get(use.getMarketCurrency())).map(PriceModelLight::getComputedPrice)
                        .orElseGet(this::waitlistValue);
                return Long.compare(computedPrice1, computedPrice2);
            };
        }

        private Long waitlistValue() {
            if (isAsc()) {
                return Long.MAX_VALUE;
            }
            return Long.MIN_VALUE;
        }
    }

    public static class DepartureSortOption extends AbstractSortOption {
        public static final String kind = "departure";

        protected DepartureSortOption(SortOptionState state) {
            super(state, "departure");
        }

        @Override
        protected Comparator<CruiseModelLight> internalComparator(FindYourCruise2018Use use) {
            return Comparator.comparing(CruiseModelLight::getStartDate);
        }
    }

    public static class DurationSortOption extends AbstractSortOption {

        public static final String kind = "duration";

        protected DurationSortOption(SortOptionState state) {
            super(state, "duration");
        }

        @Override
        protected Comparator<CruiseModelLight> internalComparator(FindYourCruise2018Use use) {
            return Comparator.comparingInt(cruise -> Integer.parseInt(cruise.getDuration()));
        }
    }

    enum SortOptionState {
        ASC, DESC, NON_SELECTED
    }
}


