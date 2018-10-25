package com.silversea.aem.utils;

import com.silversea.aem.models.PriceModel;
import org.apache.sling.api.resource.Resource;

import java.util.Iterator;
import java.util.List;

/**
 * @author aurelienolivier
 */
public class CruiseUtils {

    /**
     * Recursive collection of prices for this cruise
     * @param prices the list of prices
     * @param resource resource from where to get the prices
     */
    public static void collectPrices(final List<PriceModel> prices, final Resource resource) {
        if (resource.isResourceType("silversea/silversea-com/components/subpages/prices/pricevariation")) {
            final PriceModel priceModel = resource.adaptTo(PriceModel.class);

            if (priceModel != null) {
                prices.add(priceModel);
            }
        } else {
            final Iterator<Resource> children = resource.listChildren();

            while (children.hasNext()) {
                collectPrices(prices, children.next());
            }
        }
    }

    @SafeVarargs
    public static <T> T firstNonNull(T... args) {
        for (T arg : args) {
            if (arg != null) {
                return arg;
            }
        }
        return null;
    }
}
