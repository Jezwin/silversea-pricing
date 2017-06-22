package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.LandProgramImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.LandsApi;
import io.swagger.client.model.Land;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Service
@Component(label = "Silversea.com - Land Program importer")
public class LandProgramImporterImpl extends BaseImporter implements LandProgramImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(LandProgramImporterImpl.class);

	private int errorNumber = 0;
	private int succesNumber = 0;
	private int sessionRefresh = 100;
	private int pageSize = 100;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private Replicator replicat;

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
			LOGGER.debug("travel agencies updates importer login exception ", e);
		}
	}

	@Override
	public void importData() throws IOException {
		init();
		/**
		 * authentification pour le swagger
		 */
		getAuthentification(apiConfig.getLogin(), apiConfig.getPassword());
		/**
		 * Récuperation du domain de l'api Swager
		 */
		getApiDomain(apiConfig.getApiBaseDomain());
		/**
		 * Récuperation de la session refresh
		 */
		if (apiConfig.getSessionRefresh() != 0) {
			sessionRefresh = apiConfig.getSessionRefresh();
		}
		/**
		 * Récuperation de per page
		 */
		if (apiConfig.getPageSize() != 0) {
			pageSize = apiConfig.getPageSize();
		}

		// LandsApi landsApi = new LandsApi();

		try {

			Page citiesRootPage;
			Page RootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
			List<String> local = new ArrayList<>();
			local = ImporterUtils.finAllLanguageCopies(resourceResolver);

			for (String loc : local) {
				citiesRootPage = ImporterUtils.getPagePathByLocale(resourceResolver, RootPage, loc);
				LOGGER.debug("Importing city for langue : {}", loc);

				if (citiesRootPage != null) {

					int i = 1;

					List<Land> lands;

					do {

						lands = apiCallService.getLandProgram(i, pageSize);

						int j = 0;
						if (lands != null) {

							for (Land land : lands) {

								try {

									Iterator<Resource> resources = resourceResolver
											.findResources("/jcr:root/content/silversea-com/" + loc
													+ "//element(*,cq:Page)[jcr:content/landId=\"" + land.getLandId()
													+ "\"]", "xpath");

									Page landPage = null;

									if (resources.hasNext()) {
										landPage = resources.next().adaptTo(Page.class);
									} else {
										Integer cityId = land.getCities().size() > 0
												? land.getCities().get(0).getCityId() : null;

										if (cityId != null) {
											Iterator<Resource> portsResources = resourceResolver
													.findResources("/jcr:root/content/silversea-com/" + loc
															+ "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId
															+ "\"]", "xpath");

											if (portsResources.hasNext()) {
												Page portPage = portsResources.next().adaptTo(Page.class);

												LOGGER.debug("Found port {} with ID {}", portPage.getTitle(), cityId);

												Page landsPage;
												if (portPage.hasChild("landprogram")) {
													landsPage = pageManager
															.getPage(portPage.getPath() + "/land-programs");
												} else {
													landsPage = pageManager.create(portPage.getPath(), "land-programs",
															"/apps/silversea/silversea-com/templates/page",
															"Land Program", false);
												}
												session.save();
												if (!replicat.getReplicationStatus(session, landsPage.getPath())
														.isActivated()) {
													replicat.replicate(session, ReplicationActionType.ACTIVATE,
															landsPage.getPath());
												}

												landPage = pageManager.create(landsPage.getPath(),
														JcrUtil.createValidChildName(landsPage.adaptTo(Node.class),
																StringHelper.getFormatWithoutSpecialCharcters(
																		land.getLandName())),
														TemplateConstants.PATH_LANDPROGRAM, StringHelper
																.getFormatWithoutSpecialCharcters(land.getLandName()),
														false);

												LOGGER.debug("Creating land {}", land.getLandCod());
											} else {
												LOGGER.debug("No city found with id {}", cityId);
											}
										} else {
											LOGGER.debug("Excursion have no city attached, not imported");
										}
									}

									if (landPage != null) {
										Node hotelPageContentNode = landPage.getContentResource().adaptTo(Node.class);
										hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE, land.getLandName());
										hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
												land.getDescription());
										hotelPageContentNode.setProperty("landId", land.getLandId());
										hotelPageContentNode.setProperty("landCode", land.getLandCod());
										succesNumber = succesNumber + 1;
										j++;

										try {
											session.save();
											replicat.replicate(session, ReplicationActionType.ACTIVATE,
													landPage.getPath());
										} catch (RepositoryException e) {
											session.refresh(true);
										}

									}

									if (j % sessionRefresh == 0) {
										if (session.hasPendingChanges()) {
											try {
												session.save();
											} catch (RepositoryException e) {
												session.refresh(true);
											}
										}
									}
								} catch (Exception e) {
									errorNumber = errorNumber + 1;
									LOGGER.debug("Hotel error, number of faulures :", errorNumber);
									j++;
								}
							}
						}

						i++;
					} while (lands != null && lands.size() > 0);

					if (session.hasPendingChanges()) {
						try {
							// save migration date
							Node rootNode = citiesRootPage.getContentResource().adaptTo(Node.class);
							rootNode.setProperty("lastModificationDateLp", Calendar.getInstance());
							session.save();
						} catch (RepositoryException e) {
							session.refresh(false);
						}
					}
				}
			}
			resourceResolver.close();
		} catch (ApiException | RepositoryException e) {
			LOGGER.error("Exception importing shorexes", e);
		}
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public int getSuccesNumber() {
		return succesNumber;
	}
}
