package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

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
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.CitiesApi;
import io.swagger.client.model.City;

/**
 * @author aurelienolivier
 */
@Service
@Component(label = "Silversea.com - Cities importer")
public class CitiesImporterImpl extends BaseImporter implements CitiesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(CitiesImporterImpl.class);

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

	@Override
	public void importData() throws IOException {

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

		// final String authorizationHeader =
		// getAuthorizationHeader(apiConfig.apiUrlConfiguration("citiesUrl"));
		CitiesApi citiesApi = new CitiesApi();
		// citiesApi.getApiClient().addDefaultHeader("Authorization",
		// authorizationHeader);
		try {
			ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Session session = resourceResolver.adaptTo(Session.class);
			
			Page citiesRootPage;

			Page RootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
			List<String> local = new ArrayList<>();
			local = ImporterUtils.finAllLanguageCopies(resourceResolver);

			for (String loc : local) {
				citiesRootPage = ImporterUtils.getPagePathByLocale(resourceResolver, RootPage, loc);
				LOGGER.debug("Importing city for langue : {}", loc);

				if (citiesRootPage != null) {
					List<City> cities;
					int i = 1;

					do {
						cities = apiCallService.getCities(i, pageSize, citiesApi);
						int j = 0;
						for (City city : cities) {
							try {

								LOGGER.debug("Importing city: {}", city.getCityName());
								String portFirstLetter = "";
								if (city.getCityName() != null) {
									portFirstLetter = String.valueOf(city.getCityName().charAt(0));
								}

								final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

								Page portFirstLetterPage;
								if (citiesRootPage.hasChild(portFirstLetterName)) {
									portFirstLetterPage = pageManager
											.getPage(ImportersConstants.BASEPATH_PORTS + "/" + portFirstLetterName);

//									LOGGER.debug("Page {} already exists", portFirstLetterName);
								} else {
									portFirstLetterPage = pageManager.create(citiesRootPage.getPath(),
											portFirstLetterName, TemplateConstants.PATH_PAGE_PORT, portFirstLetter,
											false);
//									LOGGER.debug("Creating page {}", portFirstLetterName);
								}

								session.save();
								// if (replicat.getReplicationStatus(session,
								// citiesRootPage.getPath()).isActivated()) {
								try {
//									if (!replicat.getReplicationStatus(session, portFirstLetterPage.getPath())
//											.isActivated()) {
										replicat.replicate(session, ReplicationActionType.ACTIVATE,
												portFirstLetterPage.getPath());
//									}
								} catch (ReplicationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// }

								Iterator<Resource> resources = resourceResolver
										.findResources("/jcr:root/content/silversea-com/" + loc
												+ "//element(*,cq:Page)[jcr:content/cityId=\"" + city.getCityId()
												+ "\"]", "xpath");

								Page portPage;

								if (resources.hasNext()) {
									portPage = resources.next().adaptTo(Page.class);
									LOGGER.debug("Port page {} with ID {} already exists, for language :{}",
											city.getCityName(), city.getCityCod(), loc);
								} else {
									portPage = pageManager.create(portFirstLetterPage.getPath(),
											JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
													StringHelper.getFormatWithoutSpecialCharcters(city.getCityName())),
											TemplateConstants.PATH_PORT,
											StringHelper.getFormatWithoutSpecialCharcters(city.getCityName()), false);

									LOGGER.debug("Creating port {} for language {}", city.getCityName(), loc);
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
								portPageContentNode.setProperty("countryIso2", city.getCountryIso2());
								portPageContentNode.setProperty("countryIso3", city.getCountryIso3());
								succesNumber = succesNumber + 1;
								j++;
								session.save();

								replicat.replicate(session, ReplicationActionType.ACTIVATE, portPage.getPath());

								try {
									replicat.replicate(session, ReplicationActionType.ACTIVATE,
											portFirstLetterPage.getPath());
									LOGGER.debug("Activation of port {} for language {}", city.getCityName(),loc);
								} catch (ReplicationException e) {
									// TODO Auto-generated catch block
									LOGGER.debug("Error of port {} for language {}", city.getCityName(),loc);
									e.printStackTrace();
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
								LOGGER.debug("cities error, number of faulures :", errorNumber);
								j++;
							}
						}

						i++;
					} while (cities.size() > 0 && cities !=null);

					try {
						Node rootNode = citiesRootPage.getContentResource().adaptTo(Node.class);
						rootNode.setProperty("lastModificationDate", Calendar.getInstance());
						session.save();
					} catch (RepositoryException e) {
						session.refresh(false);
					}

					// end
				}
			}

			resourceResolver.close();
		} catch (ApiException | LoginException | RepositoryException e) {
			LOGGER.error("Exception importing cities", e);
		}
	}

	@Override
	public void importCity(final String cityId) {
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public int getSuccesNumber() {
		return succesNumber;
	}
}
