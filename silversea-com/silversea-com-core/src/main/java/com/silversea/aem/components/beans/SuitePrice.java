package com.silversea.aem.components.beans;

import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.models.SuiteModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class used to store mapping between one suite and price variations
 * Lowest price is updated when a <code>PriceModel</code> is added to the price variations list
 */
public class SuitePrice {

    private SuiteModel suiteModel;

    private List<PriceModel> pricesVariations = new ArrayList<>();

    private PriceModel lowestPrice;

    private boolean isWaitList = true;

    private Locale locale;
    
    private String suiteCategory;

    public SuitePrice(final SuiteModel suiteModel, final PriceModel price, final Locale locale, String suiteCategory) {
        this.suiteModel = suiteModel;
        pricesVariations.add(price);

        if (!price.isWaitList()) {
            lowestPrice = price;
            isWaitList = false;
            if(price.getComputedPrice() == 0){
                isWaitList = true;
            }
        }

        this.locale = locale;
        
        this.suiteCategory = suiteCategory;
    }

    public SuiteModel getSuite() {
        return suiteModel;
    }

    public List<PriceModel> getPricesVariations() {
        return pricesVariations;
    }

    public PriceModel getLowestPrice() {
        return lowestPrice;
    }

    public boolean isWaitList() {
        return isWaitList;
    }

    public String getComputedPriceFormated() {
        return PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
    }

    public void add(final PriceModel priceModel) {
        pricesVariations.add(priceModel);

        if (!priceModel.isWaitList()) {
            if (lowestPrice == null || priceModel.getComputedPrice() < lowestPrice.getComputedPrice()) {
                lowestPrice = priceModel;
            }

            isWaitList = false;
        }
    }

	public String getSuiteCategory() {
		return suiteCategory;
	}

}
