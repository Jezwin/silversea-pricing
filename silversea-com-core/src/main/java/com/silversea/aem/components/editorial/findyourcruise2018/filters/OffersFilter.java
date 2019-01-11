package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.ExclusiveOfferModelLight;

import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;

public class OffersFilter extends AbstractFilter<ExclusiveOfferModelLight> {
    public OffersFilter() {
        super("eo");
    }

    @Override
    public Stream<FilterRow<ExclusiveOfferModelLight>> projection(CruiseModelLight cruiseModelLight) {
        return cruiseModelLight.getExclusiveOffers().stream()
                .map(offer -> new FilterRow<>(offer, ExclusiveOfferModelLight::getTitle, offer.getPath(), ENABLED));
    }

    @Override
    public boolean isVisible() {
        return false;//this is a hidden filter!
    }
}
