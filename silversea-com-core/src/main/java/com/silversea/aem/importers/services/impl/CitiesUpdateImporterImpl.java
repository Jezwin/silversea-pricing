package com.silversea.aem.importers.services.impl;

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
import com.silversea.aem.importers.services.CitiesUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.model.City77;
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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.*;

/**
 * @author mbennabi
 */
@Deprecated
@Service
@Component(label = "Silversea.com - Cities Update importer")
public class CitiesUpdateImporterImpl implements CitiesUpdateImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(CitiesUpdateImporterImpl.class);

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
			LOGGER.debug("Cities importer login exception ", e);
		}
	}

	@Override
	public void updateImportData() throws IOException, ReplicationException, UpdateImporterExceptions {
		init();

		try {

			if (apiConfig.getSessionRefresh() != 0) {
				sessionRefresh = apiConfig.getSessionRefresh();
			}

			if (apiConfig.getPageSize() != 0) {
				pageSize = apiConfig.getPageSize();
			}

			Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
			Resource resParent = rootPage.adaptTo(Resource.class);
			String currentDate;
			Page citiesRootPage;
			currentDate = ImporterUtils.getLastModificationDate(rootPage, "lastModificationDate");
			if (currentDate != null) {
				final List<String> locales = ImporterUtils.getSiteLocales(pageManager);
				List<City77> cities;
				int i = 1;

				do {
					cities = apiCallService.getCitiesUpdates(currentDate, i, pageSize);

					int j = 0;

					for (String loc : locales) {
						if (loc != null) {
							citiesRootPage = ImporterUtils.getPagePathByLocale(pageManager, rootPage, loc);
							for (City77 city : cities) {
								if (city != null) {
									try {
										LOGGER.debug("Importing city: {}", city.getCityName());

										Iterator<Resource> resources = resourceResolver
												.findResources("/jcr:root/content/silversea-com/" + loc
														+ "//element(*,cq:Page)[jcr:content/cityId=\""
														+ city.getCityId() + "\"]", "xpath");
										Page portPage;
										if (resources.hasNext()) {
											portPage = resources.next().adaptTo(Page.class);

											if (BooleanUtils.isTrue(city.getIsDeleted())) {
												ImporterUtils.updateReplicationStatus(replicat, session,
														city.getIsDeleted(), portPage.getPath());
												LOGGER.debug("unpublish Port page {} with ID {} already exists",
														city.getCityName(), city.getCityCod());
											}

											LOGGER.debug("Port page {} with ID {} already exists", city.getCityName(),
													city.getCityCod());
										} else {

											final String portFirstLetter = String.valueOf(city.getCityName().charAt(0));
											final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

											Page portFirstLetterPage;

											if (citiesRootPage.hasChild(portFirstLetterName)) {
												portFirstLetterPage = pageManager.getPage(
														citiesRootPage.getPath() + "/" + portFirstLetterName);

												LOGGER.debug("Page {} already exists", portFirstLetterName);
											} else {
												portFirstLetterPage = pageManager.create(citiesRootPage.getPath(),
														portFirstLetterName, TemplateConstants.PATH_PAGE,
														portFirstLetter, false);

												LOGGER.debug("Creating page {}", portFirstLetterName);
											}
											session.save();
											ImporterUtils.updateReplicationStatus(replicat, session, false,
													portFirstLetterPage.getPath());

											portPage = pageManager.create(portFirstLetterPage.getPath(),
													JcrUtil.createValidChildName(
															portFirstLetterPage.adaptTo(Node.class),
															StringHelper.getFormatWithoutSpecialCharcters(
																	city.getCityName())),
													TemplateConstants.PATH_PORT,
													StringHelper.getFormatWithoutSpecialCharcters(city.getCityName()),
													false);
											LOGGER.debug(" create Port page {} with ID {} ", city.getCityName(),
													city.getCityId());

										}

										Node portPageContentNode = portPage.getContentResource().adaptTo(Node.class);

										portPageContentNode.setProperty(JcrConstants.JCR_TITLE, city.getCityName());
										portPageContentNode.setProperty("apiTitle", city.getCityName());
										portPageContentNode.setProperty("apiDescription", city.getShortDescription());
										portPageContentNode.setProperty("apiLongDescription", city.getDescription());
										portPageContentNode.setProperty("cityCode", city.getCityCod());
										portPageContentNode.setProperty("cityId", city.getCityId());
										portPageContentNode.setProperty("latitude", city.getLatitude());
										portPageContentNode.setProperty("longitude", city.getLongitude());
										portPageContentNode.setProperty("countryId", city.getCountryId());
										portPageContentNode.setProperty("countryIso3", city.getCountryIso3());
										succesNumber = succesNumber + 1;
										session.save();
										if (BooleanUtils.isFalse(city.getIsDeleted()) || city.getIsDeleted() == null) {
											session.save();
											ImporterUtils.updateReplicationStatus(replicat, session, false,
													portPage.getPath());
										}

										j++;
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
										LOGGER.debug("cities import error {}, number of faulures : {}", city,
												+errorNumber);
										j++;
									}

								}
							}
						}
					}
					i++;
				} while (cities.size() > 0);

//				if (session.hasPendingChanges()) {
					try {
						Node rootNode = resParent.getChild(JcrConstants.JCR_CONTENT).adaptTo(Node.class);
						rootNode.setProperty("lastModificationDate", Calendar.getInstance());
						session.save();
					} catch (RepositoryException e) {
						session.refresh(false);
					}
//				}

				resourceResolver.close();
			} else {
				throw new UpdateImporterExceptions();
			}
		} catch (ApiException | RepositoryException e) {
			LOGGER.error("Exception importing cities", e);
		}
	}

	@Override
	public void importUpdateCity(final String cityId) {
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public int getSuccesNumber() {
		return succesNumber;
	}

}
