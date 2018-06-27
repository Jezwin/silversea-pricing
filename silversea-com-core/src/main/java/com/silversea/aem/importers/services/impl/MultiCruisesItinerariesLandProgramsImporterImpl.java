package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.MultiCruisesItinerariesLandProgramsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.LandsApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.LandItinerary77;
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
public class MultiCruisesItinerariesLandProgramsImporterImpl implements MultiCruisesItinerariesLandProgramsImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(MultiCruisesItinerariesLandProgramsImporterImpl.class);

	protected int sessionRefresh = 100;
	protected int pageSize = 100;

	private boolean importRunning;

	@Reference
	protected ResourceResolverFactory resourceResolverFactory;

	@Reference
	protected ApiConfigurationService apiConfig;

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

		LOGGER.debug("Starting multi Cruise land programs import");
		importRunning = true;

		final ImportResult importResult = new ImportResult();
		int apiPage = 1;
		int apiPageDiff = 1;

		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (final ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			final Session session = resourceResolver.adaptTo(Session.class);

			final LandsApi landsApi = new LandsApi(ImportersUtils.getApiClient(apiConfig));

			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}
			
			// getting last import date
			final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
			String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
					"lastModificationDateMultiCruisesItinerariesLandPrograms");
			
			if(lastModificationDate == null) {
				ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
						"lastModificationDateMultiCruisesItinerariesLandPrograms", false);
				lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
						"lastModificationDateMultiCruisesItinerariesLandPrograms");
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

			// Initializing elements necessary to import landPrograms

			// cruises mapping
			final Map<Integer, Map<String, Page>> cruisesMapping = ImportersUtils
					.getItemsPageMapping(resourceResolver,
							"/jcr:root/content/silversea-com//element(*,cq:PageContent)"
									+ "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
							"cruiseId");

			// landPrograms
			final Map<Integer, Map<String, String>> landProgramsMapping = ImportersUtils.getItemsMapping(
					resourceResolver,
					"/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/landprogram\"]",
					"landId");

			// Importing landPrograms
			List<LandItinerary> landPrograms;
			Map<Integer, List<LandItinerary>> landProgramsCache;
			int itemsWritten = 0;
			apiPage = 1;
			landProgramsCache = new HashMap<>();
			
			for (Voyage77 mCruise : modifiedCruises) {
				apiPage = 1;
				if (cruisesMapping.containsKey(mCruise.getVoyageId())) {
					
					for (Map.Entry<String, Page> cruisePages : cruisesMapping.get(mCruise.getVoyageId()).entrySet()) {
						try {
						final Node cruiseContentNode = cruisePages.getValue().getContentResource().adaptTo(Node.class);
						final CruiseModel cruise = cruisePages.getValue().adaptTo(CruiseModel.class);
						if (cruiseContentNode == null) {
							throw new ImporterException("Cannot get content cruise node");
						}

						if (cruise == null) {
							throw new ImporterException("Cannot get content cruise model");
						}

						String requestedVoyageId = "";
						for (ItineraryModel itiModel : cruise.getItineraries()) {
							try {
								Node itiNode = itiModel.getResource().adaptTo(Node.class);
								if(itiNode.hasNode("land-programs")) {
									itiNode.getNode("land-programs").remove();
									session.save();
								}
								

								Resource itiRsr = itiModel.getResource();
								ValueMap vmap = itiRsr.getValueMap();

								if (requestedVoyageId != vmap.get("voyage_id", String.class)) {
									apiPage = 1;									
									requestedVoyageId = vmap.get("voyage_id", String.class);
									if(landProgramsCache.get(Integer.parseInt(requestedVoyageId)) == null) {
										List<LandItinerary> exc = new ArrayList<LandItinerary>();
										do {
											landPrograms = landsApi.landsGetItinerary(null, Integer.parseInt(requestedVoyageId), null,
													apiPage, pageSize, null);
											exc.addAll(landPrograms);
											apiPage++;
										} while (landPrograms.size() > 0);
										landProgramsCache.put(Integer.parseInt(requestedVoyageId), exc);
									}
								}

								for (LandItinerary landItinerary : landProgramsCache.get(Integer.parseInt(requestedVoyageId))) {
									try {
										if (landItinerary.getCityId().equals(vmap.get("city_id", Integer.class)) && landItinerary.getDate().toGregorianCalendar().get(Calendar.DAY_OF_YEAR) == itiModel.getDate().get(Calendar.DAY_OF_YEAR) ) {
											 if (!landProgramsMapping.containsKey(landItinerary.getLandId())) {
						                            throw new ImporterException("Land Program " + landItinerary.getLandId() + " is not present in LP cache");
						                        }
											final Node landProgramsNode = JcrUtils.getOrAddNode(itiNode, "land-programs",
													"nt:unstructured");
											final Node landProgramNode = landProgramsNode
													.addNode(JcrUtil.createValidChildName(landProgramsNode,
															String.valueOf(landItinerary.getLandItineraryId())));

											final String lang = LanguageHelper.getLanguage(pageManager, itiRsr);

											// associating landProgram page
											if (landProgramsMapping.get(landItinerary.getLandId()).containsKey(lang)) {
												landProgramNode.setProperty("landProgramReference",
														landProgramsMapping.get(landItinerary.getLandId()).get(lang));
											}

											landProgramNode.setProperty("landProgramId", landItinerary.getLandId());
											landProgramNode.setProperty("landProgramItineraryId",
													landItinerary.getLandItineraryId());
											landProgramNode.setProperty("date",
													landItinerary.getDate().toGregorianCalendar());
											landProgramNode.setProperty("sling:resourceType",
													"silversea/silversea-com/components/subpages/itinerary/landprogram");

											importResult.incrementSuccessNumber();

											itemsWritten++;
											if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
												try {
													session.save();

													LOGGER.info("{} land program imported, saving session",
															+itemsWritten);
												} catch (RepositoryException e) {
													session.refresh(true);
												}
											}
										}
									} catch (RepositoryException e) {
										LOGGER.error("Cannot write land program {}", landItinerary.getLandItineraryId(), e);

										importResult.incrementErrorNumber();
									}
								}
							} catch (RepositoryException e) {
								LOGGER.error("Cannot write land program for itinerary {}", itiModel.getPortId(), e);

								importResult.incrementErrorNumber();
							}
						}
						}catch (Exception e) {
							LOGGER.error(e.getMessage(), e);

							importResult.incrementErrorNumber();
						}
					}

				}

			}

			ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
					"lastModificationDateMultiCruisesItinerariesLandPrograms", false);

			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.info("{} itineraries LP imported, saving session", +itemsWritten);
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}
		} catch (LoginException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (RepositoryException | ImporterException e) {
			LOGGER.error("Cannot import land programs", e);
		} catch (ApiException e) {
			LOGGER.error("Cannot read land programs from API", e);
		} finally {
			importRunning = false;
		}

		LOGGER.info("Ending multi Cruise itineraries land programs import, success: {}, errors: {}, api calls : {}",
				+importResult.getSuccessNumber(), +importResult.getErrorNumber(), apiPage);

		return importResult;
	}
}
