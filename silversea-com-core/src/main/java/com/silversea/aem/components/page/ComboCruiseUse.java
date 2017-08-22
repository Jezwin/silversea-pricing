package com.silversea.aem.components.page;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.ComboCruiseModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.utils.PathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComboCruiseUse extends AbstractGeolocationAwareUse {

    private ComboCruiseModel comboCruiseModel;

    private List<SuitePrice> prices = new ArrayList<>();

    private PriceModel lowestPrice = null;

    private boolean isWaitList = true;

    private Locale locale;

    @Override
    public void activate() throws Exception {
        comboCruiseModel = getCurrentPage().adaptTo(ComboCruiseModel.class);

        if (comboCruiseModel == null) {
            throw new Exception("Cannot get combo cruise model");
        }

        locale = getCurrentPage().getLanguage(false);

        // init prices based on geolocation
        for (final PriceModel priceModel : comboCruiseModel.getPrices()) {
            if (priceModel.getGeomarket() != null
                    && priceModel.getGeomarket().equals(geomarket)
                    && priceModel.getCurrency().equals(currency)) {
                // Adding price to suites/prices mapping
                boolean added = false;

                for (SuitePrice price : prices) {
                    if (price.getSuite().equals(priceModel.getSuite())) {
                        price.add(priceModel);

                        added = true;
                    }
                }

                if (!added) {
                    prices.add(new SuitePrice(priceModel.getSuite(), priceModel, locale));
                }

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

    /**
     * @return return prices corresponding to the current geolocation
     */
    public List<SuitePrice> getPrices() {
        return prices;
    }

    /**
     * @return the lowest price for this cruise
     */
    public PriceModel getLowestPrice() {
        return lowestPrice;
    }

    public String getComputedPriceFormatted() {
        return PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
    }

    /**
     * @return true is the cruise is on wait list
     */
    public boolean isWaitList() {
        return isWaitList;
    }

    /**
     * Return path for request quote page
     */
    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage().getLanguage(false));
    }
}