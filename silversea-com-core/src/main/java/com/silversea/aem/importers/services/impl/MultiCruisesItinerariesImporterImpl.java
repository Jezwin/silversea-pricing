package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.MultiCruisesItinerariesImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.ItinerariesApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Itinerary;
import io.swagger.client.model.Voyage77;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.mime.MimeTypeService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.*;

@Service
@Component
public class MultiCruisesItinerariesImporterImpl implements MultiCruisesItinerariesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(MultiCruisesItinerariesImporterImpl.class);

	private int sessionRefresh = 100;
	private int pageSize = 100;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private MimeTypeService mimeTypeService;

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

		LOGGER.debug("Starting multi Cruise itineraries import");

		int successNumber = 0, errorNumber = 0, apiPage = 1, itemsWritten = 0;

		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (final ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			final Session session = resourceResolver.adaptTo(Session.class);
			final ItinerariesApi itinerariesApi = new ItinerariesApi(ImportersUtils.getApiClient(apiConfig));

			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}

			// getting last import date
			final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
			String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
					"lastModificationDateMultiCruisesItineraries");
			
			if(lastModificationDate == null) {
				ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
						"lastModificationDateMultiCruisesItineraries", false);
				lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
						"lastModificationDateMultiCruisesItineraries");
			}

			// Initializing elements necessary to import itineraries
			// cruises mapping
			final Map<Integer, Map<String, String>> cruisesMapping = ImportersUtils
					.getItemsMapping(resourceResolver,
							"/jcr:root/content/silversea-com//element(*,cq:PageContent)"
									+ "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
							"cruiseId");

			// port mapping
			final Map<Integer, Map<String, String>> portsMapping = ImportersUtils
					.getItemsMapping(resourceResolver,
							"/jcr:root/content/silversea-com//element(*,cq:PageContent)"
									+ "[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]",
							"cityId");

			Set<Voyage77> modifiedCruises = new HashSet<>();

			// init modified voyages cruises
			final VoyagesApi voyagesApi = new VoyagesApi(ImportersUtils.getApiClient(apiConfig));
			List<Voyage77> cruises;
			do {
				cruises = voyagesApi.multiVoyagesGetChanges(lastModificationDate, apiPage, pageSize, null, null);

				for (Voyage77 voyage : cruises) {
					modifiedCruises.add(voyage);

					if (cruisesMapping.containsKey(voyage.getVoyageId())) {
						for (Map.Entry<String, String> cruisePath : cruisesMapping.get(voyage.getVoyageId())
								.entrySet()) {
							try {
								session.getNode(cruisePath.getValue() + "/jcr:content/itineraries").remove();

								itemsWritten++;

								if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
									try {
										session.save();

										LOGGER.info("{} multi Cruise itineraries cleaned, saving session", +itemsWritten);
									} catch (RepositoryException e) {
										session.refresh(true);
									}
								}
							} catch (RepositoryException e) {
								LOGGER.debug("Cannot remove itineraries node for path {}", cruisePath.getValue());
							}
						}
					}
				}

				apiPage++;
			} while (cruises.size() > 0);

			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.info("{} itineraries cleaned, saving session", +itemsWritten);
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}

			// Importing itineraries
			List<Itinerary> itineraries;
			apiPage = 1;

			for (Voyage77 cruise : modifiedCruises) {
				try {
					if (!cruisesMapping.containsKey(cruise.getVoyageId())) {
						throw new ImporterException("Cruise is not modified");
					}
					if (cruise.getIsCombo()) { // Is A Combo Cruise
						final Map<String, String> cruisePaths = cruisesMapping.get(cruise.getVoyageId());

						if (cruisePaths == null) {
							throw new ImporterException("Cannot find cruise with id " + cruise.getVoyageId());
						}

						itineraries = itinerariesApi.itinerariesGetById(cruise.getVoyageId(), null);

						for (Map.Entry<String, String> cruisePath : cruisePaths.entrySet()) {
							final Node cruiseContentNode = session.getNode(cruisePath.getValue() + "/jcr:content");

							for (final Itinerary itinerary : itineraries) {
								try {
									LOGGER.trace("Adding itinerary {} under cruise {}", itinerary.getItineraryId(),
											cruisePath.getValue());

									final Node itinerariesNode = JcrUtils.getOrAddNode(cruiseContentNode, "itineraries",
											"nt:unstructured");
									itinerariesNode.setProperty("sling:resourceType",
											"silversea/silversea-com/components/subpages/itineraries");
									if (itinerariesNode.hasNode(String.valueOf(itinerary.getItineraryId()))) {
										throw new ImporterException("Itinerary item already exists");
									}

									final Node itineraryNode = itinerariesNode
											.addNode(String.valueOf(itinerary.getItineraryId()));
									itineraryNode.setProperty("itineraryId", itinerary.getItineraryId());
									itineraryNode.setProperty("date",
											itinerary.getItineraryDate().toGregorianCalendar());
									itineraryNode.setProperty("arriveTime", itinerary.getArriveTime());
									itineraryNode.setProperty("arriveAmPm", itinerary.getArriveTimeAmpm());
									itineraryNode.setProperty("departTime", itinerary.getDepartTime());
									itineraryNode.setProperty("departAmPm", itinerary.getDepartTimeAmpm());
									itineraryNode.setProperty("overnight", itinerary.getIsOvernight());
									itineraryNode.setProperty("sling:resourceType",
											"silversea/silversea-com/components/subpages/itinerary");

									// associating port page if exists
									final Integer cityId = itinerary.getCityId();
									if (portsMapping.containsKey(cityId)) {
										if (portsMapping.get(cityId).containsKey(cruisePath.getKey())) {
											itineraryNode.setProperty("portReference",
													portsMapping.get(cityId).get(cruisePath.getKey()));
										}
									}
									final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
									final Boolean isVisible = cruiseContentNode.getProperty("isVisible").getBoolean();

									if (startDate.after(Calendar.getInstance()) && isVisible) {
										cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
									}

									successNumber++;
									itemsWritten++;

									if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
										try {
											session.save();

											LOGGER.info("{} itineraries imported, saving session", +itemsWritten);
										} catch (RepositoryException e) {
											session.refresh(true);
										}
									}
								} catch (RepositoryException | ImporterException e) {
									LOGGER.warn("Cannot write itinerary {} - {}", itinerary.getItineraryId(),
											e.getMessage());

									errorNumber++;
								}
							}
						}

					}
				} catch (RepositoryException | ImporterException e) {
					LOGGER.error("Cannot import itineraries", e);
				} catch (ApiException e) {
					LOGGER.error("Cannot read itineraries from API", e);
				}
			}

			ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
					"lastModificationDateMultiCruisesItineraries", false);

			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.info("{} itineraries updated, saving session", +itemsWritten);
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}

		} catch (LoginException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (RepositoryException | ImporterException e) {
			LOGGER.error("Cannot import itineraries", e);
		} catch (ApiException e) {
			LOGGER.error("Cannot read itineraries from API", e);
		}
		LOGGER.info("Ending multi Cruise  itineraries updated, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber,
				apiPage);

		return new ImportResult(successNumber, errorNumber);
	}
}
