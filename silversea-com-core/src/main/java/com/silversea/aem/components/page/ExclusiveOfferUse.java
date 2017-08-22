package com.silversea.aem.components.page;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.models.ExclusiveOfferModel;

/**
 * Created by asiba on 21/06/2017.
 */
public class ExclusiveOfferUse extends AbstractGeolocationAwareUse {

    private ExclusiveOfferModel exclusiveOfferModel;

    private boolean available;

    private ExclusiveOfferItem exclusiveOfferItem;

    @Override
    public void activate() throws Exception {
        super.activate();
        exclusiveOfferModel = getCurrentPage().adaptTo(ExclusiveOfferModel.class);

        if (exclusiveOfferModel.getGeomarkets().contains(geomarket)) {
            available = true;
        }

        exclusiveOfferItem = new ExclusiveOfferItem(exclusiveOfferModel, countryCode);
    }

    public boolean isAvailable() {
        return available;
    }

    /**
     * @return the exclusiveOfferItem
     */
    public ExclusiveOfferItem getExclusiveOfferItem() {
        return exclusiveOfferItem;
    }
}