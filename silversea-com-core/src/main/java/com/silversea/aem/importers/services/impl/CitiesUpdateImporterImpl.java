package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.exceptions.UpdateImporterExceptions;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CitiesUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.CitiesApi;
import io.swagger.client.model.City77;

/**
 * @author mbennabi
 */
@Service
@Component(label = "Silversea.com - Cities Update importer")
public class CitiesUpdateImporterImpl extends BaseImporter implements CitiesUpdateImporter {

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

	@Override
	public void updateImporData() throws IOException, ReplicationException, UpdateImporterExceptions {

		try {
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

			ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Session session = resourceResolver.adaptTo(Session.class);

			Page citiesRootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));

			Resource resParent = citiesRootPage.adaptTo(Resource.class);
			Date date = resParent.getChild("jcr:content").getValueMap().get("lastModificationDate", Date.class);

			// get last importing date
			String dateFormat = "yyyyMMdd";
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			String currentDate;

			if (date != null) {
				currentDate = formatter.format(date.getTime()).toString();

//				final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("citiesUrl"));

//				CitiesApi citiesApi = new CitiesApi();
//				citiesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

				List<City77> cities;
				int i = 1;

				do {
//					cities = citiesApi.citiesGetChanges(currentDate, i, pageSize, null, null, null);
					cities = apiCallService.getCitiesUpdates(currentDate, i, pageSize);

					int j = 0;

					for (City77 city : cities) {
						try {
							LOGGER.debug("Importing city: {}", city.getCityName());

							Iterator<Resource> resources = resourceResolver.findResources(
									"//element(*,cq:Page)[jcr:content/cityId=\"" + city.getCityId() + "\"]", "xpath");

							Page portPage;

							if (resources.hasNext()) {
								portPage = resources.next().adaptTo(Page.class);
								if (BooleanUtils.isTrue(city.getIsDeleted())) {
									replicat.replicate(session, ReplicationActionType.DEACTIVATE, portPage.getPath());
									LOGGER.debug(" Desactivation of Port page {}  ", city);
								}

								LOGGER.debug("Port page {} with ID {} already exists", city.getCityName(),
										city.getCityCod());
							} else {

								final String portFirstLetter = String.valueOf(city.getCityName().charAt(0));
								final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

								Page portFirstLetterPage;

								if (citiesRootPage.hasChild(portFirstLetterName)) {
									portFirstLetterPage = pageManager
											.getPage(ImportersConstants.BASEPATH_PORTS + "/" + portFirstLetterName);

									LOGGER.debug("Page {} already exists", portFirstLetterName);
								} else {
									portFirstLetterPage = pageManager.create(citiesRootPage.getPath(),
											portFirstLetterName, TemplateConstants.PATH_PAGE, portFirstLetter, false);

									LOGGER.debug("Creating page {}", portFirstLetterName);
								}
								session.save();
								// if (replicat.getReplicationStatus(session,
								// citiesRootPage.getPath()).isActivated()) {
								try {
									// if
									// (!replicat.getReplicationStatus(session,
									// portFirstLetterPage.getPath())
									// .isActivated()) {
									replicat.replicate(session, ReplicationActionType.ACTIVATE,
											portFirstLetterPage.getPath());
									// }
								} catch (ReplicationException e) {
									e.printStackTrace();
								}
								// }

								portPage = pageManager.create(portFirstLetterPage.getPath(),
										JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
												StringHelper.getFormatWithoutSpecialCharcters(city.getCityName())),
										TemplateConstants.PATH_PORT,
										StringHelper.getFormatWithoutSpecialCharcters(city.getCityName()), false);
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
							j++;

							if (BooleanUtils.isFalse(city.getIsDeleted()) || city.getIsDeleted() == null) {
								session.save();
								replicat.replicate(session, ReplicationActionType.ACTIVATE, portPage.getPath());
								LOGGER.debug(" Activation of Port page {} with ID {} ", city.getCityName(),
										city.getCityId());
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
							LOGGER.debug("cities import error {}, number of faulures : {}",city, +errorNumber);
							j++;
						}
					}
					i++;
				} while (cities.size() > 0);

				if (session.hasPendingChanges()) {
					try {
						// save migration date
						Node rootNode = resParent.getChild(JcrConstants.JCR_CONTENT).adaptTo(Node.class);
						rootNode.setProperty("lastModificationDate", Calendar.getInstance());
						session.save();
					} catch (RepositoryException e) {
						session.refresh(false);
					}
				}

				resourceResolver.close();
			} else {
				throw new UpdateImporterExceptions();
			}
		} catch (ApiException | LoginException | RepositoryException e) {
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
