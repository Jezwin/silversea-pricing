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
import com.silversea.aem.importers.services.HotelUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Hotel77;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Deprecated
@Service
@Component(label = "Silversea.com - HotelsMAJ importer")
public class HotelImporterUpdateImpl implements HotelUpdateImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(HotelImporterUpdateImpl.class);

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
			LOGGER.debug("hotels update importer login exception ", e);
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
			currentDate = ImporterUtils.getLastModificationDate(citiesRootPage, "lastModificationDateHotel");

			if (currentDate != null) {

				int i = 1;

				List<Hotel77> hotels;

				do {
					hotels = apiCallService.getHotelsUpdate(currentDate, i, pageSize);

					int j = 0;
					if (hotels != null) {
						for (Hotel77 hotel : hotels) {

							try {

								Iterator<Resource> resources = resourceResolver.findResources(
										"//element(*,cq:Page)[jcr:content/hotelId=\"" + hotel.getHotelId() + "\"]",
										"xpath");

								Page hotelPage = null;
								Integer cityId = null;
								if (resources.hasNext()) {
									hotelPage = resources.next().adaptTo(Page.class);
									if (BooleanUtils.isTrue(hotel.getIsDeleted())) {
										ImporterUtils.updateReplicationStatus(replicat, session, true,
												hotelPage.getPath());
									}
								} else {
									if (hotel.getCities() != null && !hotel.getCities().isEmpty()) {
										cityId = hotel.getCities().get(0).getCityId();
									}

									if (cityId != null) {
										Iterator<Resource> portsResources = resourceResolver.findResources(
												"//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

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
											if (!replicat.getReplicationStatus(session, hotelsPage.getPath())
													.isActivated()) {
												session.save();
												ImporterUtils.updateReplicationStatus(replicat, session, false,
														hotelsPage.getPath());
											}

											hotelPage = pageManager.create(hotelsPage.getPath(),
													JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
															StringHelper.getFormatWithoutSpecialCharcters(
																	hotel.getHotelName())),
													TemplateConstants.PATH_HOTEL,
													StringHelper.getFormatWithoutSpecialCharcters(hotel.getHotelName()),
													false);

											LOGGER.debug("create hotel  {} with ID {}", hotel.getHotelName(),
													hotel.getHotelId());

										} else {
											LOGGER.debug("No city found with id {}", cityId);
										}
									} else {
										LOGGER.debug("hotel have no city attached, not imported");
									}
								}

								if (hotelPage != null && (BooleanUtils.isFalse(hotel.getIsDeleted())
										|| hotel.getIsDeleted() == null)) {
									Node hotelPageContentNode = hotelPage.getContentResource().adaptTo(Node.class);
									hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE, hotel.getHotelName());
									hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
											hotel.getDescription());
									hotelPageContentNode.setProperty("image", hotel.getImageUrl());
									hotelPageContentNode.setProperty("code", hotel.getHotelCod());
									hotelPageContentNode.setProperty("hotelId", hotel.getHotelId());
									succesNumber = succesNumber + 1;
									j++;
									LOGGER.debug("update hotel  {} with ID {}", hotel.getHotelName(),
											hotel.getHotelId());

									session.save();
									ImporterUtils.updateReplicationStatus(replicat, session, false,
											hotelPage.getPath());

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
								LOGGER.debug("hotel error, number of faulures :", errorNumber);
								j++;
							}

						}
						i++;

					}

				} while (hotels.size() > 0 && hotels != null);

				if (session.hasPendingChanges()) {
					try {
						Node rootNode = resParent.getChild(JcrConstants.JCR_CONTENT).adaptTo(Node.class);
						rootNode.setProperty("lastModificationDateHotel", Calendar.getInstance());
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
			LOGGER.error("Exception importing hotels", e);
		}
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public int getSuccesNumber() {
		return succesNumber;
	}
}
