package com.silversea.aem.importers.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ShoreExcursionsImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Shorex;

@Service
@Component(label = "Silversea.com - Shorexes importer")
public class ShoreExcursionsImporterImpl extends BaseImporter implements ShoreExcursionsImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(ShoreExcursionsImporterImpl.class);

	private int sessionRefresh = 100;
	private int pageSize = 100;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private ApiCallService apiCallService;

	private ResourceResolver resourceResolver;
	private PageManager pageManager;
	private Session session;

	public void init() {
		try {
			Map<String, Object> authenticationPrams = new HashMap<String, Object>();
			authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
			pageManager = resourceResolver.adaptTo(PageManager.class);
			session = resourceResolver.adaptTo(Session.class);
		} catch (LoginException e) {
			LOGGER.debug("Cities importer login exception ", e);
		}
	}

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
	public ImportResult importAllShoreExcursions() {
		init();
		LOGGER.debug("Starting shore excursions import");

		int successNumber = 0;
		int errorNumber = 0;

		try {
			// Session initialization
			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}

			List<Shorex> shorexes;
			int i = 1, j = 0;

			LOGGER.debug("Importing shore excursions");

			do {
				shorexes = apiCallService.getShorex(i, pageSize);

				for (Shorex shorex : shorexes) {
					LOGGER.trace("Importing shore excursion: {}", shorex.getShorexName());

					try {
						// Getting cities with the city id read from the shore
						// excursion
						Integer cityId = shorex.getCities().size() > 0 ? shorex.getCities().get(0).getCityId() : null;

						if (cityId == null) {
							throw new ImporterException("Shore excursion have no city");
						}

						Iterator<Resource> portsResources = resourceResolver
								.findResources("/jcr:root/content/silversea-com"
										+ "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

						if (!portsResources.hasNext()) {
							throw new ImporterException("Cannot find city with id " + cityId);
						}

						while (portsResources.hasNext()) {
							// Getting port page
							Page portPage = portsResources.next().adaptTo(Page.class);

							if (portPage == null) {
								throw new ImporterException("Error getting port page " + cityId);
							}

							LOGGER.trace("Found port {} with ID {}", portPage.getTitle(), cityId);

							// Creating subpage "excursions" if not present
							Page excursionsPage;
							if (portPage.hasChild("excursions")) {
								excursionsPage = pageManager.getPage(portPage.getPath() + "/excursions");
							} else {
								excursionsPage = pageManager.create(portPage.getPath(), "excursions",
										"/apps/silversea/silversea-com/templates/page", "Excursions", false);

								LOGGER.trace("{} page is not existing, creating it", excursionsPage.getPath());
							}

							final Page excursionPage = pageManager.create(excursionsPage.getPath(),
									JcrUtil.createValidChildName(excursionsPage.adaptTo(Node.class),
											StringHelper.getFormatWithoutSpecialCharcters(shorex.getShorexName())),
									TemplateConstants.PATH_EXCURSION,
									StringHelper.getFormatWithoutSpecialCharcters(shorex.getShorexName()), false);

							LOGGER.trace("Creating excursion {} in city {}", shorex.getShorexName(),
									portPage.getPath());

							// If excursion is created, set the properties
							if (excursionPage == null) {
								throw new ImporterException(
										"Cannot create excursion page for shore excursion " + shorex.getShorexName());
							}

							Node excursionPageContentNode = excursionPage.getContentResource().adaptTo(Node.class);

							if (excursionPageContentNode == null) {
								throw new ImporterException(
										"Cannot set properties for shore excursion " + shorex.getShorexName());
							}

							excursionPageContentNode.setProperty(JcrConstants.JCR_TITLE, shorex.getShorexName());
							excursionPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
									shorex.getShortDescription());
							excursionPageContentNode.setProperty("codeExcursion", shorex.getShorexCod());
							excursionPageContentNode.setProperty("apiLongDescription", shorex.getDescription());
							excursionPageContentNode.setProperty("pois", shorex.getPointsOfInterests());
							excursionPageContentNode.setProperty("shorexId", shorex.getShorexId());

							LOGGER.trace("Shore excursion {} successfully created", excursionPage.getPath());

							successNumber++;
							j++;
						}

						if (j % sessionRefresh == 0 && session.hasPendingChanges()) {
							try {
								session.save();

								LOGGER.debug("{} shore excursions imported, saving session", +j);
							} catch (RepositoryException e) {
								session.refresh(true);
							}
						}
					} catch (WCMException | RepositoryException | ImporterException e) {
						errorNumber++;

						LOGGER.error("Import error", e);
					}
				}

				i++;
			} while (shorexes.size() > 0);

			setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
					"lastModificationDateShoreExcursions");

			resourceResolver.close();
		} catch (ImporterException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (ApiException e) {
			LOGGER.error("Cannot read shore excursions from API", e);
		}

		LOGGER.debug("Ending shore excursions import, success: {}, error: {}", +successNumber, +errorNumber);

		return new ImportResult(successNumber, errorNumber);
	}

	@Override
	public ImportResult updateShoreExcursions() {
		return null;
	}

	@Override
	public void importOneShoreExcursion(String shoreExcursionId) {

	}
}
