package com.silversea.aem.components.editorial;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.ComboCruiseModel;
import com.silversea.aem.models.PriceModel;

import java.util.Locale;

public class ComboCruiseHeaderUse extends AbstractGeolocationAwareUse {

    private ComboCruiseModel comboCruiseModel;

    private PriceModel lowestPrice = null;

    private boolean isWaitList = true;

    private Locale locale;

    @Override
    public void activate() throws Exception {
        super.activate();

        final String pageReference = getProperties().get("pageReference", getCurrentPage().getPath());
        if (pageReference != null) {
            final Page comboCruisePage = getPageManager().getPage(pageReference);

            if (comboCruisePage != null) {
                comboCruiseModel = comboCruisePage.adaptTo(ComboCruiseModel.class);
            }
        }

        locale = getCurrentPage().getLanguage(false);

        // init lowest price based on geolocation
        for (final PriceModel priceModel : comboCruiseModel.getPrices()) {
            if (priceModel.getGeomarket() != null && priceModel.getGeomarket().equals(geomarket) && priceModel.getCurrency().equals(currency)) {
                // Init lowest price
                if (!priceModel.isWaitList()) {
                    if (lowestPrice == null) {
                        lowestPrice = priceModel;
                    } else if (lowestPrice.getComputedPrice() > priceModel.getComputedPrice()) {
                        lowestPrice = priceModel;
                    }

                    // Init wait list
                    isWaitList = false;
                }
            }
        }
    }

    public ComboCruiseModel getComboCruiseModel() {
        return comboCruiseModel;
    }

    public PriceModel getLowestPrice() {
        return lowestPrice;
    }

    public boolean isWaitList() {
        return isWaitList;
    }

    public String getComputedPriceFormatted() {
        return PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
    }
}
