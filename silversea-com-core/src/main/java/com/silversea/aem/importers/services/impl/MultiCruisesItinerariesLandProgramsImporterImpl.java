package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.MultiCruisesItinerariesLandProgramsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.LandsApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.LandItinerary77;
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

			// Initializing elements necessary to import landPrograms

			// cruises mapping
			final Map<Integer, Map<String, String>> cruisesMapping = ImportersUtils
					.getItemsMapping(resourceResolver,
							"/jcr:root/content/silversea-com//element(*,cq:PageContent)"
									+ "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
							"cruiseId");

			// itineraries
			final List<ItineraryModel> itinerariesMapping = ImportersUtils.getItineraries(resourceResolver);

			// landPrograms
			final Map<Integer, Map<String, String>> landProgramsMapping = ImportersUtils.getItemsMapping(
					resourceResolver,
					"/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/landprogram\"]",
					"landId");

			// Importing landPrograms
			List<LandItinerary> landPrograms;
			int itemsWritten = 0;
			apiPage = 1;

			do {
				landPrograms = landsApi.landsGetItinerary(null, null, null, apiPage, pageSize, null);

				// Iterating over land programs received from API
				for (LandItinerary landProgram : landPrograms) {

					// Trying to deal with one land program
					try {
						if (!modifiedCruises.contains(landProgram.getVoyageId())) {
							throw new ImporterException("Cruise " + landProgram.getVoyageId() + " is not modified");
						}

						final Integer landProgramId = landProgram.getLandId();
						boolean imported = false;

						if (!landProgramsMapping.containsKey(landProgramId)) {
							throw new ImporterException(
									"Land program " + landProgramId + " is not present in land programs cache");
						}

						// Iterating over itineraries in cache to write land program
						for (final ItineraryModel itineraryModel : itinerariesMapping) {

							// Checking if the itinerary correspond to land programs informations
							if (itineraryModel.isItinerary(landProgram.getVoyageId(),
									landProgram.getDate().toGregorianCalendar(), landProgram.getCityId())) {

								// Trying to write land program data on itinerary
								try {
									final Resource itineraryResource = itineraryModel.getResource();

									LOGGER.trace("importing land program {} in itinerary {}", landProgramId,
											itineraryResource.getPath());

									final Node itineraryNode = itineraryResource.adaptTo(Node.class);
									final Node landProgramsNode = JcrUtils.getOrAddNode(itineraryNode, "land-programs",
											"nt:unstructured");

									// TODO to check : getLandItineraryId() is not unique over API
									if (!landProgramsNode.hasNode(String.valueOf(landProgram.getLandItineraryId()))) {
										final Node landProgramNode = landProgramsNode
												.addNode(JcrUtil.createValidChildName(landProgramsNode,
														String.valueOf(landProgram.getLandItineraryId())));
										final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

										// associating landProgram page
										if (landProgramsMapping.get(landProgramId).containsKey(lang)) {
											landProgramNode.setProperty("landProgramReference",
													landProgramsMapping.get(landProgramId).get(lang));
										}

										landProgramNode.setProperty("landProgramId", landProgramId);
										landProgramNode.setProperty("landProgramItineraryId",
												landProgram.getLandItineraryId());
										landProgramNode.setProperty("date",
												landProgram.getDate().toGregorianCalendar());
										landProgramNode.setProperty("sling:resourceType",
												"silversea/silversea-com/components/subpages/itinerary/landprogram");

										importResult.incrementSuccessNumber();
										itemsWritten++;

										imported = true;

										if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
											try {
												session.save();

												LOGGER.info("{} land programs imported, saving session", +itemsWritten);
											} catch (RepositoryException e) {
												session.refresh(true);
											}
										}
									}
								} catch (RepositoryException e) {
									LOGGER.error("Cannot write land program {}", landProgram.getLandId(), e);

									importResult.incrementErrorNumber();
								}
							}
						}

						LOGGER.trace("Land program {} voyage id: {} city id: {} imported : {}", landProgram.getLandId(),
								landProgram.getVoyageId(), landProgram.getCityId(), imported);
					} catch (ImporterException e) {
						LOGGER.warn("Cannot deal with land program {} - {}", landProgram.getLandId(), e.getMessage());

						importResult.incrementErrorNumber();
					}
				}

				apiPage++;
			} while (landPrograms.size() > 0);

			// Importing diff landPrograms
			List<LandItinerary77> landProgramsDiff;
			int itemsWrittenDiff = 0;
			apiPageDiff = 1;

			LOGGER.info("Launching itineraries LP diff import");
			do {
				landProgramsDiff = landsApi.landsGetItineraryChanges(lastModificationDate, apiPageDiff, pageSize, null);

				// Iterating over land programs received from API
				for (LandItinerary77 landProgramDiff : landProgramsDiff) {

					// Trying to deal with one land program
					try {
						final Integer landProgramId = landProgramDiff.getLandId();
						boolean imported = false;

						if (!landProgramsMapping.containsKey(landProgramId)) {
							throw new ImporterException(
									"Land program " + landProgramId + " is not present in land programs cache");
						}

						// Iterating over itineraries in cache to write land program
						for (final ItineraryModel itineraryModel : itinerariesMapping) {

							// Checking if the itinerary correspond to land programs informations
							if (itineraryModel.isItinerary(landProgramDiff.getVoyageId(),
									landProgramDiff.getDate().toGregorianCalendar(), landProgramDiff.getCityId())) {

								// Trying to write land program data on itinerary
								try {
									final Resource itineraryResource = itineraryModel.getResource();

									LOGGER.trace("importing land program {} in itinerary {}", landProgramId,
											itineraryResource.getPath());

									final Node itineraryNode = itineraryResource.adaptTo(Node.class);
									final Node landProgramsNode = JcrUtils.getOrAddNode(itineraryNode, "land-programs",
											"nt:unstructured");

									if (!BooleanUtils.isTrue(landProgramDiff.getIsDeleted())) {
										// TODO to check : getLandItineraryId() is not unique over API
										if (!landProgramsNode
												.hasNode(String.valueOf(landProgramDiff.getLandItineraryId()))) {
											// Should create
											final Node landProgramNode = landProgramsNode
													.addNode(JcrUtil.createValidChildName(landProgramsNode,
															String.valueOf(landProgramDiff.getLandItineraryId())));
											final String lang = LanguageHelper.getLanguage(pageManager,
													itineraryResource);

											// associating landProgram page
											if (landProgramsMapping.get(landProgramId).containsKey(lang)) {
												landProgramNode.setProperty("landProgramReference",
														landProgramsMapping.get(landProgramId).get(lang));
											}

											landProgramNode.setProperty("landProgramId", landProgramId);
											landProgramNode.setProperty("landProgramItineraryId",
													landProgramDiff.getLandItineraryId());
											landProgramNode.setProperty("date",
													landProgramDiff.getDate().toGregorianCalendar());
											landProgramNode.setProperty("sling:resourceType",
													"silversea/silversea-com/components/subpages/itinerary/landprogram");

										} else {
											// Should update
											final String lang = LanguageHelper.getLanguage(pageManager,
													itineraryResource);
											final Node landProgramNodeToUpdate = landProgramsNode
													.getNode(String.valueOf(landProgramDiff.getLandItineraryId()));

											landProgramNodeToUpdate.setProperty("date",
													landProgramDiff.getDate().toGregorianCalendar());

										}
									} else {
										// Should delete
										final Node landProgramNodeToDelete = landProgramsNode
												.getNode(String.valueOf(landProgramDiff.getLandItineraryId()));
										landProgramNodeToDelete.remove();
									}

									importResult.incrementSuccessNumber();
									itemsWrittenDiff++;

									imported = true;

									// set current cruise to activate state
									final Map<String, String> cruisePaths = cruisesMapping
											.get(itineraryModel.getCruiseId());
									for (Map.Entry<String, String> cruisePath : cruisePaths.entrySet()) {
										final Node cruiseContentNode = session
												.getNode(cruisePath.getValue() + "/jcr:content");
										final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
										final Boolean isVisible = cruiseContentNode.getProperty("isVisible")
												.getBoolean();

										if (startDate.after(Calendar.getInstance()) && isVisible) {
											cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
										}
									}

									if (itemsWrittenDiff % sessionRefresh == 0 && session.hasPendingChanges()) {
										try {
											session.save();

											LOGGER.info("{} land programs diff imported, saving session",
													+itemsWrittenDiff);
										} catch (RepositoryException e) {
											session.refresh(true);
										}
									}
								} catch (RepositoryException e) {
									LOGGER.error("Cannot write land program {}", landProgramDiff.getLandId(), e);

									importResult.incrementErrorNumber();
								}
							}
						}

						LOGGER.trace("Land program {} voyage id: {} city id: {} imported : {}",
								landProgramDiff.getLandId(), landProgramDiff.getVoyageId(), landProgramDiff.getCityId(),
								imported);
					} catch (ImporterException e) {
						LOGGER.warn("Cannot deal with land program {} - {}", landProgramDiff.getLandId(),
								e.getMessage());

						importResult.incrementErrorNumber();
					}
				}

				apiPageDiff++;
			} while (landProgramsDiff.size() > 0);

			ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
					"lastModificationDateMultiCruisesItinerariesLandPrograms", false);

			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.info("{} itineraries LP imported, saving session", +itemsWritten);
					LOGGER.info("{} itineraries LP Diff imported, saving session", +itemsWrittenDiff);
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

		LOGGER.info("Ending multi Cruise itineraries land programs import, success: {}, errors: {}, api calls : {} and {} for Diff",
				+importResult.getSuccessNumber(), +importResult.getErrorNumber(), apiPage, apiPageDiff);

		return importResult;
	}
}
