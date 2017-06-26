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
import com.silversea.aem.importers.services.ShoreExcursionsUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Shorex77;

/**
 * Created by mbennabi on 17/03/2017.
 */
@Deprecated
@Service
@Component(label = "Silversea.com - Shorexes Update importer")
public class ShoreExcursionsUpdateImporterImpl implements ShoreExcursionsUpdateImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(ShoreExcursionsUpdateImporterImpl.class);

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

			if (apiConfig.getSessionRefresh() != 0) {
				sessionRefresh = apiConfig.getSessionRefresh();
			}

			if (apiConfig.getPageSize() != 0) {
				pageSize = apiConfig.getPageSize();
			}

			Page citiesRootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
			Resource resParent = citiesRootPage.adaptTo(Resource.class);

			String currentDate;
			currentDate = ImporterUtils.getLastModificationDate(citiesRootPage, "lastModificationDateEx");

			if (currentDate != null) {

				List<Shorex77> shorexes;
				int i = 1;

				do {
					shorexes = apiCallService.getShorexUpdate(currentDate, i, pageSize);
					int j = 0;

					if (shorexes != null) {
						for (Shorex77 shorex : shorexes) {
							try {
								LOGGER.debug("Importing shorex: {}", shorex.getShorexCod());

								Iterator<Resource> resources = resourceResolver.findResources(
										"//element(*,cq:Page)[jcr:content/shorexId=\"" + shorex.getShorexId() + "\"]",
										"xpath");

								Page excursionPage = null;
								Integer cityId = null;
								if (resources.hasNext()) {
									excursionPage = resources.next().adaptTo(Page.class);
									if (BooleanUtils.isTrue(shorex.getIsDeleted())) {
										ImporterUtils.updateReplicationStatus(replicat, session, true,
												excursionPage.getPath());
									}
									LOGGER.debug("Shorex page {} with ID {} already exists", shorex.getShorexName(),
											shorex.getShorexId());
								} else {
									if (shorex.getCities() != null && !shorex.getCities().isEmpty()) {
										cityId = shorex.getCities().get(0).getCityId();
									}

									if (cityId != null) {
										Iterator<Resource> portsResources = resourceResolver.findResources(
												"//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

										if (portsResources.hasNext()) {
											Page portPage = portsResources.next().adaptTo(Page.class);

											LOGGER.debug("Found port {} with ID {}", portPage.getTitle(), cityId);

											Page excursionsPage;
											if (portPage.hasChild("excursions")) {
												excursionsPage = pageManager
														.getPage(portPage.getPath() + "/excursions");
											} else {
												excursionsPage = pageManager.create(portPage.getPath(), "excursions",
														"/apps/silversea/silversea-com/templates/page", "Excursions",
														false);
											}
											if (!replicat.getReplicationStatus(session, excursionsPage.getPath())
													.isActivated()) {
												session.save();
												ImporterUtils.updateReplicationStatus(replicat, session, false,
														excursionsPage.getPath());
											}

											excursionPage = pageManager.create(excursionsPage.getPath(),
													JcrUtil.createValidChildName(excursionsPage.adaptTo(Node.class),
															StringHelper.getFormatWithoutSpecialCharcters(
																	shorex.getShorexName())),
													TemplateConstants.PATH_EXCURSION, StringHelper
															.getFormatWithoutSpecialCharcters(shorex.getShorexName()),
													false);

											LOGGER.debug("Desactivation of shorex  {} ", shorex.getShorexId());
										} else {
											LOGGER.debug("No city found with id {}", cityId);
										}
									} else {
										LOGGER.debug("Excursion have no city attached, not imported");
									}
								}

								if (excursionPage != null && (BooleanUtils.isFalse(shorex.getIsDeleted())
										|| shorex.getIsDeleted() == null)) {
									Node excursionPageContentNode = excursionPage.getContentResource()
											.adaptTo(Node.class);

									excursionPageContentNode.setProperty(JcrConstants.JCR_TITLE,
											shorex.getShorexName());
									excursionPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
											shorex.getShortDescription());
									excursionPageContentNode.setProperty("codeExcursion", shorex.getShorexCod());
									excursionPageContentNode.setProperty("apiLongDescription", shorex.getDescription());
									excursionPageContentNode.setProperty("pois", shorex.getPointsOfInterests());
									excursionPageContentNode.setProperty("shorexId", shorex.getShorexId());
									succesNumber = succesNumber + 1;
									j++;
									if (BooleanUtils.isNotTrue(shorex.getIsDeleted())) {
										session.save();
										ImporterUtils.updateReplicationStatus(replicat, session, false,
												excursionPage.getPath());
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
								LOGGER.debug("shorex update error, number of faulures :", +errorNumber);
								j++;
							}
						}

						i++;
					}
				} while (shorexes.size() > 0);

				if (session.hasPendingChanges()) {
					try {
						Node rootNode = resParent.getChild(JcrConstants.JCR_CONTENT).adaptTo(Node.class);
						rootNode.setProperty("lastModificationDateEx", Calendar.getInstance());
						session.save();
					} catch (RepositoryException e) {
						session.refresh(false);
					}
				}

				resourceResolver.close();
			} else {
				throw new UpdateImporterExceptions();
			}
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
