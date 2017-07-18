package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.BooleanUtils;
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
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.exceptions.UpdateImporterExceptions;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.LandProgramUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Land77;

/**
 * Created by mbennabi on 17/03/2017.
 */
@Deprecated
@Service
@Component(label = "Silversea.com - Land Program Update importer")
public class LandProgramUpdateImporterImpl implements LandProgramUpdateImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(LandProgramUpdateImporterImpl.class);

	private int errorNumber = 0;
	private int succesNumber = 0;
	private int sessionRefresh = 100;
	private int pageSize = 100;

	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

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
			LOGGER.debug("land program update importer login exception ", e);
		}
	}

	@Override
	public void updateImporData() throws IOException, ReplicationException, UpdateImporterExceptions {
		init();
		try {

			Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
			Resource resParent = rootPage.adaptTo(Resource.class);
			String currentDate;

			currentDate = ImporterUtils.getLastModificationDate(rootPage, "lastModificationDateLp");
			if (currentDate != null) {
				final List<String> locales = ImporterUtils.getSiteLocales(pageManager);
				int i = 1;
				List<Land77> lands;

				do {
					lands = apiCallService.getLandProgramUpdate(currentDate, i, pageSize);

					int j = 0;
					if (lands != null) {

						for (String loc : locales) {
							if (loc != null) {

								for (Land77 land : lands) {

									try {

										Iterator<Resource> resources = resourceResolver
												.findResources("/jcr:root/content/silversea-com/" + loc
														+ "//element(*,cq:Page)[jcr:content/landId=\""
														+ land.getLandId() + "\"]", "xpath");

										Page landPage = null;
										Integer cityId = null;
										if (resources.hasNext()) {
											landPage = resources.next().adaptTo(Page.class);
											if (BooleanUtils.isTrue(land.getIsDeleted())) {
												ImporterUtils.updateReplicationStatus(replicat, session, true,
														landPage.getPath());
											}
										} else {
											if (land.getCities() != null && !land.getCities().isEmpty()) {
												cityId = land.getCities().get(0).getCityId();
											}
											if (cityId != null) {
												Iterator<Resource> portsResources = resourceResolver
														.findResources("/jcr:root/content/silversea-com/" + loc
																+ "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId
																+ "\"]", "xpath");

												if (portsResources.hasNext()) {
													Page portPage = portsResources.next().adaptTo(Page.class);

													LOGGER.debug("Found port {} with ID {}", portPage.getTitle(),
															cityId);

													Page landsPage;
													if (portPage.hasChild("land-programs")) {
														landsPage = pageManager
																.getPage(portPage.getPath() + "/land-programs");
													} else {
														landsPage = pageManager.create(portPage.getPath(),
																"land-programs",
																"/apps/silversea/silversea-com/templates/page",
																"Land Program", false);
													}
													if (!replicat.getReplicationStatus(session, landsPage.getPath())
															.isActivated()) {
														session.save();
														ImporterUtils.updateReplicationStatus(replicat, session, false,
																landsPage.getPath());
													}

													landPage = pageManager.create(landsPage.getPath(),
															JcrUtil.createValidChildName(landsPage.adaptTo(Node.class),
																	StringHelper.getFormatWithoutSpecialCharcters(
																			land.getLandName())),
															TemplateConstants.PATH_LANDPROGRAM,
															StringHelper.getFormatWithoutSpecialCharcters(
																	land.getLandName()),
															false);

													LOGGER.debug("Creating land {}", land.getLandCod());
												} else {
													LOGGER.debug("No city found with id {}", cityId);
												}
											} else {
												LOGGER.debug("land program have no city attached, not imported");
											}
										}

										if (landPage != null && (BooleanUtils.isFalse(land.getIsDeleted())
												|| land.getIsDeleted() == null)) {
											Node hotelPageContentNode = landPage.getContentResource()
													.adaptTo(Node.class);
											hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE,
													land.getLandName());
											hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
													land.getDescription());
											hotelPageContentNode.setProperty("landId", land.getLandId());
											hotelPageContentNode.setProperty("landCode", land.getLandCod());
											succesNumber = succesNumber + 1;
											j++;

											if (BooleanUtils.isNotTrue(land.getIsDeleted())) {
												ImporterUtils.updateReplicationStatus(replicat, session, false,
														landPage.getPath());
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
										LOGGER.debug("land program error, number of faulures :", errorNumber);
										j++;
									}

								}
								i++;

							}
						}
					}
				} while (lands.size() > 0);

				ImporterUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
						"lastModificationDateLp");

				resourceResolver.close();
			} else {
				throw new UpdateImporterExceptions();
			}
		} catch (ApiException e) {
			LOGGER.error("Exception importing land program", e);
		}
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public int getSuccesNumber() {
		return succesNumber;
	}
}
