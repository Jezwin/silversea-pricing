package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.MultiCruisesItinerariesHotelsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.HotelItinerary77;
import io.swagger.client.model.ShorexItinerary;
import io.swagger.client.model.Voyage77;

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
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.*;

@Service
@Component
public class MultiCruisesItinerariesHotelsImporterImpl implements MultiCruisesItinerariesHotelsImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(MultiCruisesItinerariesHotelsImporterImpl.class);

	private int sessionRefresh = 100;
	private int pageSize = 100;

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

		LOGGER.debug("Starting multi Cruise  hotels import");
		importRunning = true;

		final ImportResult importResult = new ImportResult();
		int apiPage = 1;

		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (final ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			final Session session = resourceResolver.adaptTo(Session.class);

			final HotelsApi hotelsApi = new HotelsApi(ImportersUtils.getApiClient(apiConfig));

			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}

			// getting last import date
			final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
			String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
					"lastModificationDateMultiCruisesItinerariesHotels");

			if (lastModificationDate == null) {
				ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
						"lastModificationDateMultiCruisesItinerariesHotels", false);
				lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
						"lastModificationDateMultiCruisesItinerariesHotels");
			}

			// init modified voyages cruises
			final Set<Voyage77> modifiedCruises = new HashSet<>();
			final VoyagesApi voyagesApi = new VoyagesApi(ImportersUtils.getApiClient(apiConfig));
			List<Voyage77> cruises;
			do {
				cruises = voyagesApi.multiVoyagesGetChanges(lastModificationDate, apiPage, pageSize, null, null);

				for (Voyage77 voyage : cruises) {
					modifiedCruises.add(voyage);
				}

				apiPage++;
			} while (cruises.size() > 0);

			// Initializing elements necessary to import hotels

			// cruises mapping
			final Map<Integer, Map<String, Page>> cruisesMapping = ImportersUtils
					.getItemsPageMapping(resourceResolver,
							"/jcr:root/content/silversea-com//element(*,cq:PageContent)"
									+ "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
							"cruiseId");

			// hotels
			final Map<Integer, Map<String, String>> hotelsMapping = ImportersUtils.getItemsMapping(resourceResolver,
					"/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/hotel\"]",
					"hotelId");

			// Importing hotels
			List<HotelItinerary> hotels;
			Map<Integer, List<HotelItinerary>> hotelsCache;
			int itemsWritten = 0;
			apiPage = 1;
			hotelsCache = new HashMap<>();

			for (Voyage77 mCruise : modifiedCruises) {
				apiPage = 1;
				if (cruisesMapping.containsKey(mCruise.getVoyageId())) {

					for (Map.Entry<String, Page> cruisePages : cruisesMapping.get(mCruise.getVoyageId()).entrySet()) {
						try {
							final Node cruiseContentNode = cruisePages.getValue().getContentResource()
									.adaptTo(Node.class);
							final CruiseModel cruise = cruisePages.getValue().adaptTo(CruiseModel.class);
							if (cruiseContentNode == null) {
								throw new ImporterException("Cannot get content cruise node");
							}

							if (cruise == null) {
								throw new ImporterException("Cannot get content cruise model for " + cruisePages.getValue().getPath());
							}

							String requestedVoyageId = "";
							for (ItineraryModel itiModel : cruise.getItineraries()) {
								try {
									Node itiNode = itiModel.getResource().adaptTo(Node.class);
									if (itiNode.hasNode("hotels")) {
										itiNode.getNode("hotels").remove();
										session.save();
									}
									

									Resource itiRsr = itiModel.getResource();
									ValueMap vmap = itiRsr.getValueMap();

									if (requestedVoyageId != vmap.get("voyage_id", String.class)) {
										apiPage = 1;
										requestedVoyageId = vmap.get("voyage_id", String.class);
										if (hotelsCache.get(Integer.parseInt(requestedVoyageId)) == null) {
											List<HotelItinerary> exc = new ArrayList<HotelItinerary>();
											do {
												hotels = hotelsApi.hotelsGetItinerary(null,
														Integer.parseInt(requestedVoyageId), null, apiPage, pageSize,
														null);
												exc.addAll(hotels);
												apiPage++;
											} while (hotels.size() > 0);
											hotelsCache.put(Integer.parseInt(requestedVoyageId), exc);
										}
									}

									for (HotelItinerary hotelItinerary : hotelsCache
											.get(Integer.parseInt(requestedVoyageId))) {
										try {
											if (hotelItinerary.getCityId().equals(vmap.get("city_id", Integer.class))
													&& hotelItinerary.getDate().toGregorianCalendar()
															.get(Calendar.DAY_OF_YEAR) == itiModel.getDate()
																	.get(Calendar.DAY_OF_YEAR)) {
												 if (!hotelsMapping.containsKey(hotelItinerary.getHotelId())) {
							                            throw new ImporterException("Hotel " + hotelItinerary.getHotelId() + " is not present in hotels cache");
							                        }
												 
												final Node hotelsNode = JcrUtils.getOrAddNode(itiNode, "hotels", "nt:unstructured");
												final Node hotelNode = hotelsNode
														.addNode(JcrUtil.createValidChildName(hotelsNode,
																String.valueOf(hotelItinerary.getHotelItineraryId())));

												final String lang = LanguageHelper.getLanguage(pageManager, itiRsr);

												// associating port page
												if (hotelsMapping.get(hotelItinerary.getHotelId()).containsKey(lang)) {
													hotelNode.setProperty("hotelReference",
															hotelsMapping.get(hotelItinerary.getHotelId()).get(lang));
												}

												hotelNode.setProperty("hotelId", hotelItinerary.getHotelId());
												hotelNode.setProperty("hotelItineraryId",
														hotelItinerary.getHotelItineraryId());
												hotelNode.setProperty("sling:resourceType",
														"silversea/silversea-com/components/subpages/itinerary/hotel");

												importResult.incrementSuccessNumber();

												itemsWritten++;
												if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
													try {
														session.save();

														LOGGER.info("{} hotels imported, saving session",
																+itemsWritten);
													} catch (RepositoryException e) {
														session.refresh(true);
													}
												}
											}
										} catch (RepositoryException e) {
											LOGGER.error("Cannot write hotels {}", hotelItinerary.getHotelId(), e);

											importResult.incrementErrorNumber();
										}
									}
								} catch (RepositoryException e) {
									LOGGER.error("Cannot write hotels for itinerary {}", itiModel.getPortId(), e);

									importResult.incrementErrorNumber();
								}
							}
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);

							importResult.incrementErrorNumber();
						}
					}

				}

			}

			ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
					"lastModificationDateMultiCruisesItinerariesHotels", false);

			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.info("{} itineraries hotels imported, saving session", +itemsWritten);
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}
		} catch (LoginException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (RepositoryException | ImporterException e) {
			LOGGER.error("Cannot import hotels", e);
		} catch (ApiException e) {
			LOGGER.error("Cannot read hotels from API", e);
		} finally {
			importRunning = false;
		}

		LOGGER.info("Ending multi Cruise itineraries hotels import, success: {}, errors: {}, api calls : {} ",
				+importResult.getSuccessNumber(), +importResult.getErrorNumber(), apiPage);

		return importResult;
	}
}
