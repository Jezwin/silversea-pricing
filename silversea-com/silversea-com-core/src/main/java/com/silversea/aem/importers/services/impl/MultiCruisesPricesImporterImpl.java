package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.MultiCruisesPricesImporter;
import com.silversea.aem.importers.utils.CruisesImportUtils;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.PricesApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Voyage77;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyagePriceMarket;

import org.apache.commons.lang3.BooleanUtils;
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

import java.util.*;

@Component
@Service
public class MultiCruisesPricesImporterImpl implements MultiCruisesPricesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(MultiCruisesPricesImporterImpl.class);

	private int sessionRefresh = 100;
	private int pageSize = 50;

	private boolean importRunning;

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
	public ImportResult importAllItems() throws ImporterException {
		if (importRunning) {
			throw new ImporterException("Import is already running");
		}

		LOGGER.debug("Starting multi cruise prices import");
		importRunning = true;

		final ImportResult importResult = new ImportResult();
		int apiPage = 1;

		// init authentication
		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (final ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			final Session session = resourceResolver.adaptTo(Session.class);

			final PricesApi pricesApi = new PricesApi(ImportersUtils.getApiClient(apiConfig));

			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}
			// getting last import date
			final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
			String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
					"lastModificationDateMultiCruisesPrices");

			if (lastModificationDate == null) {
				ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
						"lastModificationDateMultiCruisesPrices", false);
				lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
						"lastModificationDateMultiCruisesPrices");
			}

			// init modified voyages cruises
			final Set<Integer> modifiedCruises = new HashSet<>();

			final VoyagesApi voyagesApi = new VoyagesApi(ImportersUtils.getApiClient(apiConfig));
			List<Voyage77> cruises;
			do {
				cruises = voyagesApi.voyagesGetChanges(lastModificationDate, apiPage, pageSize, null, null);

				for (Voyage77 voyage : cruises) {
					modifiedCruises.add(voyage.getVoyageId());
				}

				apiPage++;
			} while (cruises.size() > 0);

			// Initializing elements necessary to import prices
			// cruises
			final Map<Integer, Map<String, String>> cruisesMapping = ImportersUtils.getItemsMapping(resourceResolver,
					"/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
					"cruiseId");

			// suites (by category code)
			final Iterator<Resource> suitesForMapping = resourceResolver.findResources("/jcr:root/content/silversea-com"
					+ "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/suitevariation\"]",
					"xpath");

			final Map<String, Map<String, Resource>> suitesMapping = new HashMap<>();
			while (suitesForMapping.hasNext()) {
				final Resource suite = suitesForMapping.next();
				final String language = LanguageHelper.getLanguage(pageManager, suite);

				final String[] suiteCategoryCodes = suite.getValueMap().get("suiteCategoryCode", String[].class);

				if (suiteCategoryCodes != null) {
					for (final String suiteCategoryCode : suiteCategoryCodes) {
						// generate unique key with ship name and suite category code
						final String suiteCatId = suite.getParent().getParent().getParent().getName() + "-"
								+ suiteCategoryCode;

						if (suitesMapping.containsKey(suiteCatId)) {
							suitesMapping.get(suiteCatId).put(language, suite.getParent());
						} else {
							final HashMap<String, Resource> suitesResources = new HashMap<>();
							suitesResources.put(language, suite.getParent());
							suitesMapping.put(suiteCatId, suitesResources);
						}
					}

					LOGGER.trace("Adding suite {} ({}) with lang {} to cache", suite.getPath(), suiteCategoryCodes,
							language);
				}
			}

			// Importing prices
			List<VoyagePriceComplete> prices;
			int itemsWritten = 0;
			apiPage = 1;
			
			//do {
			//temp
			pageSize = 10000;
			prices = pricesApi.pricesMultiGet3(apiPage, pageSize, null);

				// Iterating over prices received from API
				for (final VoyagePriceComplete price : prices) {
					final Integer cruiseId = price.getVoyageId();

					// Trying to deal with price item
					try {
						// TEMP DISABLE WAITING FOR REAL DIFF FROM IT
						/*
						 * if (update && !modifiedCruises.contains(price.getVoyageId())) { throw new
						 * ImporterException("Cruise " + price.getVoyageId() + " is not modified"); }
						 */

						// Checking if price correspond to an existing cruise
						if (!cruisesMapping.containsKey(cruiseId)) {
							throw new ImporterException("Cannot find cruise in cache with id " + cruiseId);
						}

						// Iterating over cruises where to import price
						for (Map.Entry<String, String> cruise : cruisesMapping.get(cruiseId).entrySet()) {
							try {
								final Node cruiseContentNode = session.getNode(cruise.getValue() + "/jcr:content");
								Boolean isCombo = false;
								if (cruiseContentNode.hasProperty("isCombo")) {
									isCombo = BooleanUtils
											.toBoolean(cruiseContentNode.getProperty("isCombo").getValue().getString());
								}

								if (isCombo) {
									if (cruiseContentNode.hasNode("suites")) {
										cruiseContentNode.getNode("suites").remove();
									}

									// Creating prices root node under the cruise
									final Node suitesNode = JcrUtils.getOrAddNode(cruiseContentNode, "suites");
									suitesNode.setProperty("sling:resourceType",
											"silversea/silversea-com/components/subpages/prices");

									// Iterating over markets
									for (final VoyagePriceMarket priceMarket : price.getMarketCurrency()) {
										final ImportResult importResultPrices = CruisesImportUtils.importCruisePrice(
												session, cruiseContentNode, cruise, suitesMapping, priceMarket,
												suitesNode, itemsWritten, sessionRefresh);

										importResult.incrementSuccessOf(importResultPrices.getSuccessNumber());
										importResult.incrementErrorOf(importResultPrices.getErrorNumber());

										// TEMP FORCE ACTIVATE
										final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
										final Boolean isVisible = cruiseContentNode.getProperty("isVisible")
												.getBoolean();

										if (startDate.after(Calendar.getInstance()) && isVisible) {
											cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
										} else {
											cruiseContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
										}
										// END TEMP FORCE ACTIVATE

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
				}

			//	apiPage++;
			//} while (prices.size() > 0);

			ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
					"lastModificationDateMultiCruisesPrices", false);

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

		LOGGER.info("Ending multi cruise prices import, success: {}, errors: {}, api calls : {}",
				+importResult.getSuccessNumber(), +importResult.getErrorNumber(), apiPage);

		return importResult;
	}
}
