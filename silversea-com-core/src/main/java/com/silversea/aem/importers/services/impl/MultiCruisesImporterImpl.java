package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.MultiCruisesImporter;
import com.silversea.aem.importers.utils.CruisesImportUtils;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Voyage;
import io.swagger.client.model.Voyage77;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
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
public class MultiCruisesImporterImpl implements MultiCruisesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(MultiCruisesImporterImpl.class);

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
	public ImportResult updateItems() {
		LOGGER.debug("Starting multi cruises update");

		int successNumber = 0;
		int errorNumber = 0;
		int apiPage = 1;

		final Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (final ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
			final Session session = resourceResolver.adaptTo(Session.class);

			final VoyagesApi voyagesApi = new VoyagesApi(ImportersUtils.getApiClient(apiConfig));

			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}

			// Initializing elements necessary to import cruises
			// cruises mapping
			final Map<Integer, Map<String, Page>> cruisesMapping = ImportersUtils
					.getItemsPageMapping(resourceResolver,
							"/jcr:root/content/silversea-com//element(*,cq:PageContent)"
									+ "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
							"cruiseId");

			// destinations
			final Map<String, List<String>> destinationsMapping = CruisesImportUtils
					.getDestinationsMapping(resourceResolver);

			// ships
			final Map<Integer, Map<String, String>> shipsMapping = ImportersUtils
					.getItemsMapping(resourceResolver,
							"/jcr:root/content/silversea-com//element(*,cq:PageContent)"
									+ "[sling:resourceType=\"silversea/silversea-com/components/pages/ship\"]",
							"shipId");

			// features
			final Map<Integer, String> featuresMapping = CruisesImportUtils.getFeaturesMap(resourceResolver);

			// getting last import date
			final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
			
			String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
					"lastModificationDateMultiCruises");
			
			if(lastModificationDate == null) {
				ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
						"lastModificationDateMultiCruises", false);
				lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage,
						"lastModificationDateMultiCruises");
			}
			
			LOGGER.debug("Last import date for cruises {}", lastModificationDate);

			List<Voyage77> cruises;
			int itemsWritten = 0;

			do {
				cruises = voyagesApi.multiVoyagesGetChanges(lastModificationDate, apiPage, pageSize, null, null);

				for (final Voyage77 cruise : cruises) {
					try {
						if (cruisesMapping.containsKey(cruise.getVoyageId()) && cruise.getIsDeleted() != null
								&& cruise.getIsDeleted()) {
							// deactivate cruise
							LOGGER.debug("deactivating cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

							for (Map.Entry<String, Page> cruisePages : cruisesMapping.get(cruise.getVoyageId())
									.entrySet()) {
								final Node cruiseContentNode = cruisePages.getValue().getContentResource()
										.adaptTo(Node.class);

								if (cruiseContentNode == null) {
									throw new ImporterException("Cannot get content cruise node");
								}

								cruiseContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

								successNumber++;
								itemsWritten++;

								batchSave(session, itemsWritten);
							}
						} else if (cruise.getIsDeleted() == null || !cruise.getIsDeleted()) {
							if (cruisesMapping.containsKey(cruise.getVoyageId())) {
								// update cruise
								LOGGER.debug("Updating cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

								for (Map.Entry<String, Page> cruisePages : cruisesMapping.get(cruise.getVoyageId())
										.entrySet()) {
									final Node cruiseContentNode = cruisePages.getValue().getContentResource()
											.adaptTo(Node.class);

									if (cruiseContentNode == null) {
										throw new ImporterException("Cannot get content cruise node");
									}

									updateCruiseData(assetManager, session, shipsMapping, featuresMapping, cruise,
											LanguageHelper.getLanguage(cruisePages.getValue()), cruiseContentNode,
											mimeTypeService, resourceResolver);

									successNumber++;
									itemsWritten++;

									batchSave(session, itemsWritten);
								}
							} else {
								// create cruise
								LOGGER.debug("Creating cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

								final List<String> destinationPaths = destinationsMapping
										.get(String.valueOf(cruise.getDestinationId()));

								if (destinationPaths == null) {
									throw new ImporterException("Cannot find destination with id "
											+ String.valueOf(cruise.getDestinationId()));
								}

								for (String destinationPath : destinationPaths) {
									final Resource destinationResource = resourceResolver.getResource(destinationPath);

									if (destinationResource == null) {
										throw new ImporterException(
												"Destination " + destinationPath + " cannot be found");
									}
									
									final Node cruiseContentNode = CruisesImportUtils.createCruisePage(pageManager,
											destinationPath, cruise.getVoyageName(), cruise.getVoyageCod());

									// update cruise data
									final Page destinationPage = destinationResource.adaptTo(Page.class);
									updateCruiseData(assetManager, session, shipsMapping, featuresMapping, cruise,
											LanguageHelper.getLanguage(destinationPage), cruiseContentNode,
											mimeTypeService, resourceResolver);

									successNumber++;
									itemsWritten++;

									batchSave(session, itemsWritten);
								}
							}
						}
					} catch (RepositoryException | WCMException | ImporterException e) {
						LOGGER.warn("Cannot write cruise {} ({}) - {}", cruise.getVoyageName(), cruise.getVoyageCod(),
								e.getMessage());

						errorNumber++;
					}
				}

				apiPage++;
			} while (cruises.size() > 0);

			for (Map.Entry<Integer, Map<String, Page>> cruise : cruisesMapping.entrySet()) {
				final Collection<Page> cruisePages = cruise.getValue().values();

				for (Page cruisePage : cruisePages) {
					final Node cruiseContentNode = cruisePage.getContentResource().adaptTo(Node.class);
					try {
						final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
						final Boolean isVisible = cruiseContentNode.getProperty("isVisible").getBoolean();

						if (startDate.before(Calendar.getInstance()) || !isVisible) {
							LOGGER.debug("Cruise {} in the past ({}) or not visible ({}), mark to deactivate",
									cruiseContentNode.getPath(), startDate.getTime(), isVisible);

							cruiseContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

							itemsWritten++;

							batchSave(session, itemsWritten);
						}
					} catch (RepositoryException e) {
						LOGGER.warn("Cannot extract start date from cruise {}", cruiseContentNode.getPath());
					}
				}
			}

			ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
					"lastModificationDateMultiCruises", false);

			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.debug("{} cruises imported, saving session", +itemsWritten);
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}
		} catch (LoginException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (RepositoryException | ImporterException e) {
			LOGGER.error("Cannot import cruises", e);
		} catch (ApiException e) {
			LOGGER.error("Cannot read cruises from API", e);
		}

		LOGGER.info("Ending cruises update, success: {}, error: {}, api calls: {}", +successNumber, +errorNumber,
				apiPage);

		return new ImportResult(successNumber, errorNumber);
	}

	@Override
	public JSONObject getJsonMapping() {
		Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		JSONObject jsonObject = new JSONObject();

		try {
			final ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(authenticationParams);

			Iterator<Resource> cruises = resourceResolver.findResources("/jcr:root/content/silversea-com"
					+ "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
					"xpath");

			while (cruises.hasNext()) {
				final Resource cruise = cruises.next();
				final Page cruisePage = cruise.adaptTo(Page.class);

				final Resource childContent = cruise.getChild(JcrConstants.JCR_CONTENT);

				if (cruisePage != null && childContent != null) {
					final ValueMap childContentProperties = childContent.getValueMap();
					final String cruiseCode = childContentProperties.get("cruiseCode", String.class);
					final String lang = cruisePage.getAbsoluteParent(2).getName();
					final String path = cruisePage.getPath();

					if (cruiseCode != null && lang != null) {
						try {
							if (jsonObject.has(cruiseCode)) {
								final JSONObject cruiseObject = jsonObject.getJSONObject(cruiseCode);
								cruiseObject.put(lang, path);
								jsonObject.put(cruiseCode, cruiseObject);
							} else {
								JSONObject shipObject = new JSONObject();
								shipObject.put(lang, path);
								jsonObject.put(cruiseCode, shipObject);
							}
						} catch (JSONException e) {
							LOGGER.error("Cannot add cruise {} with path {} to cruises array", cruiseCode,
									cruise.getPath(), e);
						}
					}
				}
			}

		} catch (LoginException e) {
			LOGGER.error("Cannot create resource resolver", e);
		}

		return jsonObject;
	}

	private void updateCruiseData(AssetManager assetManager, Session session,
			Map<Integer, Map<String, String>> shipsMapping, Map<Integer, String> featuresMapping, Voyage77 cruise,
			String language, Node cruiseContentNode, MimeTypeService mimeTypeService, ResourceResolver resourceResolver)
			throws RepositoryException {
		// Adding tags
		List<String> tagIds = new ArrayList<>();

		// setting cruise type
		if (cruise.getIsExpedition() != null && cruise.getIsExpedition()) {
			tagIds.add(WcmConstants.TAG_CRUISE_TYPE_EXPEDITION);
		} else {
			tagIds.add(WcmConstants.TAG_CRUISE_TYPE_CRUISE);
		}

		// setting features
		for (int featureId : cruise.getFeatures()) {
			if (featuresMapping.containsKey(featureId)) {
				tagIds.add(featuresMapping.get(featureId));
			}
		}

		cruiseContentNode.setProperty("cq:tags", tagIds.toArray(new String[tagIds.size()]));

		// setting ship reference
		if (shipsMapping.containsKey(cruise.getShipId())) {
			if (shipsMapping.get(cruise.getShipId()).containsKey(language)) {
				LOGGER.trace("Associating ship {} to cruise", shipsMapping.get(cruise.getShipId()).get(language));

				cruiseContentNode.setProperty("shipReference", shipsMapping.get(cruise.getShipId()).get(language));
			}
		}

		// setting other properties
		final String cruiseTitle = CruisesImportUtils.getTranslatedCruiseTitle(language, cruise.getVoyageName());
		cruiseContentNode.setProperty("apiTitle", cruiseTitle);
		cruiseContentNode.setProperty("importedDescription", cruise.getVoyageDescription());
		cruiseContentNode.setProperty("startDate", cruise.getDepartDate().toGregorianCalendar());
		cruiseContentNode.setProperty("endDate", cruise.getArriveDate().toGregorianCalendar());
		cruiseContentNode.setProperty("voyageHighlights", cruise.getVoyageHighlights());
		cruiseContentNode.setProperty("duration", cruise.getDays());
		cruiseContentNode.setProperty("cruiseCode", cruise.getVoyageCod());
		cruiseContentNode.setProperty("cruiseId", cruise.getVoyageId());
		cruiseContentNode.setProperty("isVisible", BooleanUtils.isTrue(cruise.getIsVisible()));
		cruiseContentNode.setProperty("isCombo", BooleanUtils.isTrue(cruise.getIsCombo()));

		
		// SSC-2406 Wrong Cruise URL + Global Search Title
		// set sling:alias and jcr:title to match voyage name
		final String alias = JcrUtil
				.createValidName(StringUtils.stripAccents(cruise.getVoyageName() + " - " + cruise.getVoyageCod()),
						JcrUtil.HYPHEN_LABEL_CHAR_MAPPING)
				.replaceAll("-+", "-");
		if (language.equals("fr") || language.equals("es") || language.equals("pt-br")) {
			alias.replace("-to-", "-a-");
		} else if (language.equals("de")) {
			alias.replace("-to-", "-nach-");
		}
		if (!cruiseContentNode.getParent().getName().equals(alias)) {
			cruiseContentNode.setProperty("sling:alias", alias);
		}
		cruiseContentNode.setProperty("jcr:title", cruise.getVoyageCod() + " - " + cruiseTitle);

		// TODO temporary not write livecopy informations to be compliant with PROD
		// content
		// Set livecopy mixin
		/*
		 * if (!language.equals("en")) {
		 * cruiseContentNode.addMixin("cq:LiveRelationship");
		 * cruiseContentNode.addMixin("cq:PropertyLiveSyncCancelled");
		 * 
		 * if (!cruiseContentNode.hasProperty("cq:propertyInheritanceCancelled")) {
		 * cruiseContentNode.setProperty("cq:propertyInheritanceCancelled", new
		 * String[]{"apiTitle", "importedDescription", "jcr:title", "sling:alias"}); } }
		 */

		if (StringUtils.isNotEmpty(cruise.getMapUrl())) {
			CruisesImportUtils.associateMapAsset(session, cruiseContentNode,
					cruiseContentNode.getParent().getParent().getName(), cruise.getMapUrl(),"itinerary" ,mimeTypeService,
					resourceResolver);
		}

		final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
		final Boolean isVisible = cruiseContentNode.getProperty("isVisible").getBoolean();

		if (startDate.after(Calendar.getInstance()) && isVisible) {
			cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
		} else {
			cruiseContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
		}
	}

	private void batchSave(Session session, int itemsWritten) throws RepositoryException {
		if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
			try {
				session.save();

				LOGGER.info("{} cruises imported, saving session", +itemsWritten);
			} catch (RepositoryException e) {
				session.refresh(true);
			}
		}
	}
}
