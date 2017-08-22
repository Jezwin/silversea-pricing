package com.silversea.aem.importers.utils;

import com.day.cq.commons.jcr.JcrUtil;
import com.silversea.aem.importers.ImporterException;
import io.swagger.client.model.Price;
import io.swagger.client.model.VoyagePriceMarket;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Map;

/**
 * @author aurelienolivier
 */
public class CruisesImportUtils {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesImportUtils.class);

    static public void importCruisePrice(final Session session, final Node cruiseContentNode, final Map.Entry<String, String> cruise,
                                         final Map<String, Map<String, Resource>> suitesMapping,
                                         final VoyagePriceMarket priceMarket,
                                         final Node suitesNode,
                                         int successNumber, int errorNumber, int itemsWritten,
                                         int sessionRefresh) throws RepositoryException {
        // Iterating over prices variation
        for (final Price cruiseOnlyPrice : priceMarket.getCruiseOnlyPrices()) {
            try {
                if (cruiseContentNode.getProperty("shipReference") == null) {
                    throw new ImporterException("Cruise " + cruise.getKey() + " do not contains a" +
                            " ship reference");
                }

                final String suiteCatId = PathUtils.getName(cruiseContentNode.getProperty("shipReference")
                        .getString()) + "-" + cruiseOnlyPrice.getSuiteCategoryCod();

                if (!suitesMapping.containsKey(suiteCatId)) {
                    throw new ImporterException("Cannot get suite with category " + suiteCatId);
                }

                // Getting suite corresponding to suite category
                final Map<String, Resource> suites = suitesMapping.get(suiteCatId);
                final String suiteName = suites.get(cruise.getKey()).getName();

                final Node suiteNode = JcrUtils.getOrAddNode(suitesNode, suiteName);

                final String priceVariationNodeName = cruiseOnlyPrice.getSuiteCategoryCod() +
                        priceMarket.getMarketCod() + cruiseOnlyPrice.getCurrencyCod();

                final Node priceVariationNode = suiteNode.addNode(JcrUtil.createValidChildName(suiteNode,
                        priceVariationNodeName));

                priceVariationNode.setProperty("suiteCategory", cruiseOnlyPrice.getSuiteCategoryCod());
                priceVariationNode.setProperty("price", cruiseOnlyPrice.getCruiseOnlyFare());
                if (cruiseOnlyPrice.getEarlyBookingBonus() != null) {
                    priceVariationNode.setProperty("earlyBookingBonus",
                            cruiseOnlyPrice.getEarlyBookingBonus());
                }
                priceVariationNode.setProperty("currency", cruiseOnlyPrice.getCurrencyCod());
                priceVariationNode.setProperty("availability", cruiseOnlyPrice.getSuiteAvailability());
                priceVariationNode.setProperty("cq:tags", new String[]{"geotagging:" +
                        priceMarket.getMarketCod().toLowerCase()});

                // Writing suite reference based on lang
                priceVariationNode.setProperty("suiteReference", suites.get(cruise.getKey()).getPath());

                priceVariationNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/prices/pricevariation");

                successNumber++;
                itemsWritten++;

                if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                    try {
                        session.save();

                        LOGGER.info("{} prices imported, saving session", +itemsWritten);
                    } catch (RepositoryException e) {
                        session.refresh(true);
                    }
                }
            } catch (ImporterException | RepositoryException e) {
                LOGGER.warn("Cannot import price for category, {}", e.getMessage());

                errorNumber++;
            }
        }
    }
}
