package com.silversea.aem.components.included.lightboxes;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.utils.CruiseUtils;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.resource.JcrResourceConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LightboxSuiteUse extends AbstractGeolocationAwareUse {

    private SuitePrice suitePrice;
    private String appendSuffixUrl;
    private String suffixUrl;
    private String selectorUrl = WcmConstants.SELECTOR_FYC_RESULT;

    @Override
    public void activate() throws Exception {
        super.activate();
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        suitePrice = retrieveSuitePriceModel(selectors);
    }

    private SuitePrice retrieveSuitePriceModel(String[] selectors) {
        if (ArrayUtils.isNotEmpty(selectors)) {
            String suiteCode = selectors[2];
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
                            appendSuffixUrl = suitePriceModel.getSuite().getName() + WcmConstants.HTML_SUFFIX;
                            if (selectors.length > 3) {
                                suffixUrl = selectors[3];
                            }
                        }

                    }
                }
                return suitePriceModel;
            }
        }
        return null;
    }

    public SuitePrice getSuitePrice() {
        return suitePrice;
    }

    public String getRaqLink() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
    }

    public String getAppendSuffixUrl() {
        return appendSuffixUrl;
    }

    public String getSelectorUrl() {
        return selectorUrl;
    }

    public String getSuffixUrl() {
        return suffixUrl;
    }
}
