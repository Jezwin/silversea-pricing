package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesPricesImporter;
import com.silversea.aem.importers.utils.CruisesImportUtils;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.services.BestPriceOfferConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.PricesApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Voyage77;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyagePriceMarket;

import org.apache.commons.lang3.ArrayUtils;
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
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import java.util.*;

@Component
@Service
public class CruisesPricesImporterImpl implements CruisesPricesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesPricesImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;
    private String bpOfferPath = null;
    private String aiOfferPath = null;
    

    private boolean importRunning;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;
    
    @Reference
    private BestPriceOfferConfigurationService bpOfferConfig;

    @Activate
    protected void activate(final ComponentContext context) {
        if (apiConfig.getSessionRefresh() != 0) {
            sessionRefresh = apiConfig.getSessionRefresh();
        }

        if (apiConfig.getPageSize() != 0) {
            pageSize = apiConfig.getPageSize();
        }
        
        bpOfferPath = bpOfferConfig.getBestPriceOffer();
        aiOfferPath = bpOfferConfig.getAllInclusiveOffer();
    }

    @Override
    public ImportResult importAllItems(final boolean update) throws ImporterException {
        if (importRunning) {
            throw new ImporterException("Import is already running");
        }

        LOGGER.debug("Starting prices import");
        importRunning = true;

        final ImportResult importResult = new ImportResult();
        int apiPage = 1;

        // init authentication
        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final PricesApi pricesApi = new PricesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            if (!update) {
                // Existing prices deletion
                LOGGER.debug("Cleaning already imported prices");

                ImportersUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                        + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/prices\"]");
            }

            // getting last import date
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage, "lastModificationDateCruisesPrices");

            // init modified voyages cruises
            final Set<Integer> modifiedCruises = new HashSet<>();
            if (update) {
                final VoyagesApi voyagesApi = new VoyagesApi(ImportersUtils.getApiClient(apiConfig));
                List<Voyage77> cruises;
                do {
                    cruises = voyagesApi.voyagesGetChanges(lastModificationDate, apiPage, pageSize, null, null);

                    for (Voyage77 voyage : cruises) {
                        modifiedCruises.add(voyage.getVoyageId());
                    }

                    apiPage++;
                } while (cruises.size() > 0);
            }

            // Initializing elements necessary to import prices
            // cruises
            final Map<Integer, Map<String, String>> cruisesMapping = ImportersUtils.getItemsMapping(resourceResolver,
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
                    for (final String suiteCategoryCode : suiteCategoryCodes) {
                        // generate unique key with ship name and suite category code
                        final String suiteCatId = suite.getParent().getParent().getParent().getName() + "-" +
                                suiteCategoryCode;

                        if (suitesMapping.containsKey(suiteCatId)) {
                            suitesMapping.get(suiteCatId).put(language, suite.getParent());
                        } else {
                            final HashMap<String, Resource> suitesResources = new HashMap<>();
                            suitesResources.put(language, suite.getParent());
                            suitesMapping.put(suiteCatId, suitesResources);
                        }
                    }

                    LOGGER.trace("Adding suite {} ({}) with lang {} to cache", suite.getPath(), suiteCategoryCodes, language);
                }
            }

            // Importing prices
            List<VoyagePriceComplete> prices;
            int itemsWritten = 0;
            apiPage = 1;
            int count = 0;

            do {
                prices = pricesApi.pricesGet3(apiPage, pageSize, null);

                // Iterating over prices received from API
                for (final VoyagePriceComplete price : prices) {
                    final Integer cruiseId = price.getVoyageId();

                    // Trying to deal with price item
                    try {
                    	//TEMP DISABLE WAITING FOR REAL DIFF FROM IT
                      /*  if (update && !modifiedCruises.contains(price.getVoyageId())) {
                            throw new ImporterException("Cruise " + price.getVoyageId() + " is not modified");
                        }*/

                        // Checking if price correspond to an existing cruise
                        if (!cruisesMapping.containsKey(cruiseId)) {
                            throw new ImporterException("Cannot find cruise in cache with id " + cruiseId);
                        }

                        // Iterating over cruises where to import price
                        for (Map.Entry<String, String> cruise : cruisesMapping.get(cruiseId).entrySet()) {
                            try {
                                final Node cruiseContentNode = session.getNode(cruise.getValue() + "/jcr:content");
                                boolean bestPriceActiveFlag = price.getBestPriceAvailable();
                                cruiseContentNode.setProperty("bestPriceAvailable", bestPriceActiveFlag);
                                if (cruiseContentNode.hasProperty("offer")) {
                                	Property offer = cruiseContentNode.getProperty("offer"); 
                                	ValueFactory valueFactory = session.getValueFactory();
                                    Value[] existingOffers = offer.getValues();
                                    if (!(existingOffers[0].toString() == aiOfferPath) &&
                                    		!(existingOffers[1].toString() == bpOfferPath) &&
                                    			bestPriceActiveFlag == true) {
				                                    Value[] bestPriceActiveOffer = new Value[2];
				                                    bestPriceActiveOffer[0] = valueFactory.createValue(aiOfferPath);
				                                    bestPriceActiveOffer[1] = valueFactory.createValue(bpOfferPath);
				                                    Value[] both = ArrayUtils.addAll(bestPriceActiveOffer, existingOffers);
				                                    //values[values.length-1] = valueFactory.createValue("/content/silversea-com/en/exclusive-offers/early-booking-offer1");
				                                    cruiseContentNode.setProperty("offer", both);
                                    }
                                    if ((existingOffers[0].toString() == aiOfferPath) &&
                                    		(existingOffers[1].toString() == bpOfferPath) &&
                                    			bestPriceActiveFlag == true) {
                                    				existingOffers = ArrayUtils.removeAll(existingOffers, 0,1);
                                    				cruiseContentNode.setProperty("offer", existingOffers);
                                    }
                                }
                                if (bestPriceActiveFlag == true && !cruiseContentNode.hasProperty("offer")) {
                                	ValueFactory valueFactory = session.getValueFactory();
                                	Value[] values = new Value[2];
                                	values[0] = valueFactory.createValue(aiOfferPath);
                                	values[1]  = valueFactory.createValue(bpOfferPath);
                                	cruiseContentNode.setProperty("bestPriceAvailable", values);
                                }
                                
                                if (cruiseContentNode.hasNode("suites")) {
                                    cruiseContentNode.getNode("suites").remove();
                                }

                                // Creating prices root node under the cruise
                                final Node suitesNode = JcrUtils.getOrAddNode(cruiseContentNode, "suites");
                                suitesNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/prices");

                                // Iterating over markets
                                for (final VoyagePriceMarket priceMarket : price.getMarketCurrency()) {
                                    final ImportResult importResultPrices = CruisesImportUtils.importCruisePrice(session, cruiseContentNode, cruise,
                                            suitesMapping, priceMarket, suitesNode, itemsWritten, sessionRefresh);

                                    importResult.incrementSuccessOf(importResultPrices.getSuccessNumber());
                                    importResult.incrementErrorOf(importResultPrices.getErrorNumber());

                                   //TEMP FORCE ACTIVATE 
                                    final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
                                    final Boolean isVisible = cruiseContentNode.getProperty("isVisible").getBoolean();

                                    if (startDate.after(Calendar.getInstance()) && isVisible) {
	                               		cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
	                               	}else{
	                                	cruiseContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
	                                }
                                    //END TEMP FORCE ACTIVATE
                                    
                                    if (importResultPrices.getSuccessNumber() > 0) {
                                        itemsWritten++;
                                    }

                                    if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                        try {
                                            session.save();

                                            LOGGER.debug("{} prices imported, saving session", +itemsWritten);
                                        } catch (RepositoryException e) {
                                            session.refresh(true);
                                        }
                                    }
                                }
                            } catch (RepositoryException e) {
                                LOGGER.warn("Cannot write prices for cruise {}", e.getMessage());

                                importResult.incrementErrorNumber();
                            }
                        }
                    } catch (ImporterException e) {
                        LOGGER.warn("Cannot write prices {}", e.getMessage());

                        importResult.incrementErrorNumber();
                    }
                    break;
                }

                apiPage++;
                count++;
                if(count==50) {
                	break;
                }
            } while (prices.size() > 0);

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
                    "lastModificationDateCruisesPrices", false);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.info("{} prices imported, saving session", +itemsWritten);
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
            importRunning = false;
        }

        LOGGER.info("Ending prices import, success: {}, errors: {}, api calls : {}",
                +importResult.getSuccessNumber(), +importResult.getErrorNumber(), apiPage);

        return importResult;
    }
}
