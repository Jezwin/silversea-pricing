package com.silversea.aem.components.included.lightboxes;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.resource.JcrResourceConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LightboxSuiteUse extends AbstractGeolocationAwareUse {

    private SuitePrice model;

    @Override
    public void activate() throws Exception {
        super.activate();
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        model = retrieveSuitePriceModel(selectors);
    }

    private SuitePrice retrieveSuitePriceModel(String[] selectors) {
        if (ArrayUtils.isNotEmpty(selectors)) {
            String suiteCode = selectors[1];
            List<PriceModel> prices = new ArrayList<>();
            Resource currentResource = getCurrentPage().getContentResource();
            Resource suitesResource = currentResource.hasChildren() ? currentResource.getChild("suites") : null;
            if (suitesResource != null) {
                CruiseUtils.collectPrices(prices, suitesResource);
                prices.sort((o1, o2) -> -o1.getPrice().compareTo(o2.getPrice()));
            }

            SuitePrice suitePriceModel = null;
            Locale locale = getCurrentPage().getLanguage(false);

            if (prices.size() > 0) {
                for (PriceModel priceModel : prices) {
                    if (priceModel.getGeomarket() != null && priceModel.getGeomarket().equals(super.geomarket)
                            && priceModel.getCurrency().equals(super.currency)) {
                        if (priceModel.getSuiteCategory().equalsIgnoreCase(suiteCode)) {
                            suitePriceModel = new SuitePrice(priceModel.getSuite(), priceModel, locale,
                                    priceModel.getSuiteCategory());
                        }
                        if (suitePriceModel != null && suitePriceModel.getSuite().equals(priceModel.getSuite())) {
                            suitePriceModel.add(priceModel);
                        }

                    }
                }
                return suitePriceModel;
            }
        }
        return null;
    }

    public SuitePrice getModel() {
        return model;
    }
}
