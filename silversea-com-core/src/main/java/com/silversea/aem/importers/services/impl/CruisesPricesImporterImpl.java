package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesPricesImporter;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.PricesApi;
import io.swagger.client.model.Price;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyagePriceMarket;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Service
public class CruisesPricesImporterImpl implements CruisesPricesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesPricesImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Activate
    protected void activate(final ComponentContext context) {
        if (apiConfig.getSessionRefresh() != 0) {
            sessionRefresh = apiConfig.getSessionRefresh();
        }

        if (apiConfig.getPageSize() != 0) {
            pageSize = apiConfig.getPageSize();
        }
    }

    @Override
    public ImportResult importAllItems() {
        return importSampleSet(-1);
    }

    @Override
    public ImportResult importSampleSet(int size) {
        LOGGER.debug("Starting prices import ({})", size == -1 ? "all" : size);

        int successNumber = 0;
        int errorNumber = 0;
        int apiPage = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;

        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final PricesApi pricesApi = new PricesApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing prices deletion
            LOGGER.debug("Cleaning already imported prices");

            ImporterUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/prices\"]");

            // Initializing elements necessary to import prices
            // cruises
            final Map<Integer, Map<String, String>> cruisesMapping = ImporterUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
                    "cruiseId");

            // suites (by category code)
            final Iterator<Resource> suitesForMapping = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/suitevariation\"]", "xpath");

            final Map<String, Map<String, Resource>> suitesMapping = new HashMap<>();
            while (suitesForMapping.hasNext()) {
                final Resource suite = suitesForMapping.next();
                final String language = LanguageHelper.getLanguage(pageManager, suite);

                final String[] suiteCategoryCodes = suite.getValueMap().get("suiteCategoryCode", String[].class);

                if (suiteCategoryCodes != null) {
                    for (String suiteCategoryCode : suiteCategoryCodes) {
                        if (suitesMapping.containsKey(suiteCategoryCode)) {
                            suitesMapping.get(suiteCategoryCode).put(language, suite.getParent());
                        } else {
                            final HashMap<String, Resource> suitesResources = new HashMap<>();
                            suitesResources.put(language, suite.getParent());
                            suitesMapping.put(suiteCategoryCode, suitesResources);
                        }
                    }

                    LOGGER.trace("Adding suite {} ({}) with lang {} to cache", suite.getPath(), suiteCategoryCodes, language);
                }
            }

            // Importing prices
            List<VoyagePriceComplete> prices;
            int itemsWritten = 0;

            do {
                prices = pricesApi.pricesGet3(apiPage, pageSize, null);

                // Iterating over prices received from API
                for (final VoyagePriceComplete price : prices) {
                    final Integer cruiseId = price.getVoyageId();

                    // Trying to deal with price item
                    try {

                        // Checking if price correspond to an existing cruise
                        if (!cruisesMapping.containsKey(cruiseId)) {
                            throw new ImporterException("Cannot find cruise in cache with id " + cruiseId);
                        }

                        // Iterating over cruises where to import price
                        for (Map.Entry<String, String> cruise : cruisesMapping.get(cruiseId).entrySet()) {
                            try {
                                final Node cruiseContentNode = session.getNode(cruise.getValue() + "/jcr:content");

                                // Creating prices root node under the cruise
                                final Node suitesNode = JcrUtils.getOrAddNode(cruiseContentNode, "suites");
                                suitesNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/prices");

                                // Iterating over markets
                                for (final VoyagePriceMarket priceMarket : price.getMarketCurrency()) {
                                    // Iterating over prices variation
                                    for (final Price cruiseOnlyPrice : priceMarket.getCruiseOnlyPrices()) {
                                        try {
                                            if (!suitesMapping.containsKey(cruiseOnlyPrice.getSuiteCategoryCod())) {
                                                throw new ImporterException("Cannot get suite with category " + cruiseOnlyPrice.getSuiteCategoryCod());
                                            }

                                            // Getting suite corresponding to suite category
                                            final Map<String, Resource> suites = suitesMapping.get(cruiseOnlyPrice.getSuiteCategoryCod());
                                            final String suiteName = suites.get(cruise.getKey()).getName();

                                            final Node suiteNode = JcrUtils.getOrAddNode(suitesNode, suiteName);

                                            final String priceVariationNodeName = cruiseOnlyPrice.getSuiteCategoryCod() +
                                                    priceMarket.getMarketCod() +
                                                    cruiseOnlyPrice.getCurrencyCod();

                                            final Node priceVariationNode = suiteNode.addNode(JcrUtil.createValidChildName(suiteNode,
                                                    priceVariationNodeName));

                                            priceVariationNode.setProperty("suiteCategory", cruiseOnlyPrice.getSuiteCategoryCod());
                                            priceVariationNode.setProperty("price", cruiseOnlyPrice.getCruiseOnlyFare());
                                            priceVariationNode.setProperty("currency", cruiseOnlyPrice.getCurrencyCod());
                                            priceVariationNode.setProperty("availability", cruiseOnlyPrice.getSuiteAvailability());
                                            priceVariationNode.setProperty("cq:Tags", new String[]{"geotagging:" + priceMarket.getMarketCod().toLowerCase()});

                                            // Writing suite reference based on lang
                                            priceVariationNode.setProperty("suiteReference", suites.get(cruise.getKey()).getPath());

                                            priceVariationNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/prices/pricevariation");

                                            successNumber++;
                                            itemsWritten++;

                                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                                try {
                                                    session.save();

                                                    LOGGER.debug("{} prices imported, saving session", +itemsWritten);
                                                } catch (RepositoryException e) {
                                                    session.refresh(true);
                                                }
                                            }
                                        } catch (ImporterException | RepositoryException e) {
                                            LOGGER.warn("Cannot import price for category, {}", e.getMessage());

                                            errorNumber++;
                                        }

                                        if (size != -1 && itemsWritten >= size) {
                                            break;
                                        }
                                    }

                                    if (size != -1 && itemsWritten >= size) {
                                        break;
                                    }
                                }
                            } catch (RepositoryException e) {
                                LOGGER.warn("Cannot write prices for cruise {}", e.getMessage());

                                errorNumber++;
                            }

                            if (size != -1 && itemsWritten >= size) {
                                break;
                            }
                        }
                    } catch (ImporterException e) {
                        LOGGER.warn("Cannot write prices {}", e.getMessage());

                        errorNumber++;
                    }

                    if (size != -1 && itemsWritten >= size) {
                        break;
                    }
                }

                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (prices.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} itineraries prices imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import prices", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read prices from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.info("Ending prices import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        return null;
    }
}