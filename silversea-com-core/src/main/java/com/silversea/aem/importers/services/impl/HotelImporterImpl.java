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
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.services.HotelImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.model.Hotel;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Service
@Component(label = "Silversea.com - Hotels importer")
public class HotelImporterImpl extends BaseImporter implements HotelImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(HotelImporterImpl.class);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private Replicator replicat;

	@Reference
	private ApiCallService apiCallService;

	private int errorNumber = 0;
	private int succesNumber = 0;
	private int sessionRefresh = 100;
	private int pageSize = 100;

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
		try {
//			HotelsApi hotelsApi = new HotelsApi();

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
					int i = 1;
					List<Hotel> hotels;

					do {
						hotels = apiCallService.getHotels(i, pageSize);

						int j = 0;

						for (Hotel hotel : hotels) {

							try {

								Iterator<Resource> resources = resourceResolver
										.findResources("/jcr:root/content/silversea-com/" + loc
												+ "//element(*,cq:Page)[jcr:content/hotelId=\"" + hotel.getHotelId()
												+ "\"]", "xpath");

								Page hotelPage = null;

								if (resources.hasNext()) {
									hotelPage = resources.next().adaptTo(Page.class);
								} else {
									Integer cityId = hotel.getCities().size() > 0 ? hotel.getCities().get(0).getCityId()
											: null;

									if (cityId != null) {
										Iterator<Resource> portsResources = resourceResolver.findResources(
												"/jcr:root/content/silversea-com/" + loc
												+ "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

										if (portsResources.hasNext()) {
											Page portPage = portsResources.next().adaptTo(Page.class);

											LOGGER.debug("Found port {} with ID {}", portPage.getTitle(), cityId);

											Page hotelsPage;
											if (portPage.hasChild("hotels")) {
												hotelsPage = pageManager.getPage(portPage.getPath() + "/hotels");
											} else {
												hotelsPage = pageManager.create(portPage.getPath(), "hotels",
														"/apps/silversea/silversea-com/templates/page", "Hotels",
														false);
											}
											session.save();
											if (!replicat.getReplicationStatus(session, hotelsPage.getPath())
													.isActivated()) {
												replicat.replicate(session, ReplicationActionType.ACTIVATE,
														hotelsPage.getPath());
											}

											hotelPage = pageManager.create(hotelsPage.getPath(),
													JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
															StringHelper.getFormatWithoutSpecialCharcters(
																	hotel.getHotelName())),
													TemplateConstants.PATH_HOTEL,
													StringHelper.getFormatWithoutSpecialCharcters(hotel.getHotelName()),
													false);

											LOGGER.debug("Creating hotel {} for language {}", hotel.getHotelName(),loc);
										} else {
											LOGGER.debug("No city found with id {}for language {}", cityId,loc);
										}
									} else {
										LOGGER.debug("Hotel have no city attached, not imported");
									}
								}

								if (hotelPage != null) {
									Node hotelPageContentNode = hotelPage.getContentResource().adaptTo(Node.class);
									hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE, hotel.getHotelName());
									hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
											hotel.getDescription());
									hotelPageContentNode.setProperty("image", hotel.getImageUrl());
									hotelPageContentNode.setProperty("code", hotel.getHotelCod());
									hotelPageContentNode.setProperty("hotelId", hotel.getHotelId());
									j++;
									succesNumber = succesNumber + 1;

									try {
										session.save();
										replicat.replicate(session, ReplicationActionType.ACTIVATE,
												hotelPage.getPath());
										LOGGER.debug("replication of hotel : {} for language {}", hotel.getHotelId(), loc);
									} catch (RepositoryException e) {
										LOGGER.debug("replication ERROR of hotel : {} for language {}", hotel.getHotelId(),loc);
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
								LOGGER.debug("Hotel error, for language {} number of failures :{}",loc, +errorNumber);
								j++;
							}
						}

						i++;
					} while (hotels.size() > 0);

					if (session.hasPendingChanges()) {
						try {
							// save migration date
							Node rootNode = citiesRootPage.getContentResource().adaptTo(Node.class);
							rootNode.setProperty("lastModificationDateHotel", Calendar.getInstance());
							session.save();
						} catch (RepositoryException e) {
							session.refresh(false);
						}
					}

				}
			}
			resourceResolver.close();
		} catch (ApiException | LoginException |

				RepositoryException e) {
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
