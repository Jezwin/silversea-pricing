package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.HotelsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.utils.StringsUtils;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.model.Hotel;
import io.swagger.client.model.Hotel77;
import io.swagger.client.model.Shorex;

import org.apache.commons.lang3.BooleanUtils;
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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Component
public class HotelsImporterImpl implements HotelsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(HotelsImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

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
    public ImportResult importAllHotels() {
        LOGGER.debug("Starting hotels import");

        int successNumber = 0;
        int errorNumber = 0;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final HotelsApi hotelsApi = new HotelsApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            List<Hotel> hotels;
            int apiPage = 1, itemsWritten = 0;

            LOGGER.debug("Importing hotels");

            do {
                hotels = hotelsApi.hotelsGet(null, apiPage, pageSize, null);

                for (Hotel hotel : hotels) {
                    LOGGER.trace("Importing hotel: {}", hotel.getHotelName());

                    try {
                        // Getting cities with the city id read from the hotel
                        Integer cityId = hotel.getCities().size() > 0 ? hotel.getCities().get(0).getCityId() : null;

                        if (cityId == null) {
                            throw new ImporterException("Hotel have no city");
                        }

                        // TODO create cache of cityId / Resource in order to speed up the process
                        Iterator<Resource> portsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com//element(*,cq:Page)[" +
                                        "jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/port\"" +
                                        " and jcr:content/cityId=\"" + cityId + "\"]", "xpath");

                        if (!portsResources.hasNext()) {
                            throw new ImporterException("Cannot find city with id " + cityId);
                        }

                        while (portsResources.hasNext()) {
                            // Getting port page
                            Page portPage = portsResources.next().adaptTo(Page.class);

                            if (portPage == null) {
                                throw new ImporterException("Error getting port page " + cityId);
                            }

                            LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                            // Creating subpage "hotel" if not present
                            Page hotelsPage;
                            if (portPage.hasChild(WcmConstants.NN_HOTELS)) {
                                hotelsPage = pageManager.getPage(portPage.getPath() + "/hotels");
                            } else {
                                hotelsPage = pageManager.create(portPage.getPath(), WcmConstants.NN_HOTELS,
                                        "/apps/silversea/silversea-com/templates/page", "Hotels", false);

                                LOGGER.trace("{} page is not existing, creating it", hotelsPage.getPath());
                            }

                            // Creating hotel page
                            final Page hotelPage = pageManager.create(hotelsPage.getPath(),
                                    JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
                                            StringsUtils.getFormatWithoutSpecialCharacters(hotel.getHotelName())),
                                    WcmConstants.PAGE_TEMPLATE_HOTEL,
                                    StringsUtils.getFormatWithoutSpecialCharacters(hotel.getHotelName()), false);

                            LOGGER.trace("Creating hotel {} in city {}", hotel.getHotelName(), portPage.getPath());

                            // If hotel is created, set the properties
                            if (hotelPage == null) {
                                throw new ImporterException(
                                        "Cannot create hotel page for hotel " + hotel.getHotelName());
                            }

                            Node hotelPageContentNode = hotelPage.getContentResource().adaptTo(Node.class);

                            if (hotelPageContentNode == null) {
                                throw new ImporterException("Cannot set properties for hotel " + hotel.getHotelName());
                            }

                            hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE, hotel.getHotelName());
                            hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, hotel.getDescription());
                            hotelPageContentNode.setProperty("image", hotel.getImageUrl());
                            hotelPageContentNode.setProperty("code", hotel.getHotelCod());
                            hotelPageContentNode.setProperty("hotelId", hotel.getHotelId());

                            // Set livecopy mixin
                            if (!LanguageHelper.getLanguage(portPage).equals("en")) {
                                hotelPageContentNode.addMixin("cq:LiveRelationship");
                            }

                            LOGGER.trace("Hotel {} successfully created", hotelPage.getPath());

                            successNumber++;
                            itemsWritten++;
                        }

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} hotels imported, saving session", +itemsWritten);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (WCMException | RepositoryException | ImporterException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    }
                }

                apiPage++;
            } while (hotels.size() > 0);

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateHotels");
            
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} hotels imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
            
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read hotels from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Cannot save modification", e);
        }

        LOGGER.debug("Ending hotels import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }
    
	@Override
	public ImportResult disactiveAllItemDeltaByAPI() {
		LOGGER.debug("Starting hotels disactive delta API");

		int successNumber = 0, errorNumber = 0;

		Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Session session = resourceResolver.adaptTo(Session.class);

			HotelsApi hotelsApi = new HotelsApi(ImportersUtils.getApiClient(apiConfig));
			
			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}

			// excursions mapping
			Map<Integer, Map<String, Page>> hotelsMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
	                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
	                            "[sling:resourceType=\"silversea/silversea-com/components/pages/hotel\"]", "hotelId");

			int itemsWritten = 0, page = 1, perPage = 100;
			Hotel itemToCheck = null;
			List<Hotel> hotelsAPI = hotelsApi.hotelsGet(null, page, perPage, null);
			List<Hotel> hotelsListAPI = new ArrayList<>();
			
			LOGGER.debug("Check all hotel in jcr: {}", hotelsMapping.size());
			
			while(hotelsAPI.size() != 0){
				hotelsListAPI.addAll(hotelsAPI);
				page++;
				hotelsAPI = hotelsApi.hotelsGet(null, page, perPage, null);
			}
			
			for (Map.Entry<Integer, Map<String, Page>> hotel : hotelsMapping.entrySet()) {
				itemToCheck = null;
				Integer hotelID = hotel.getKey();
				LOGGER.debug("Check hotelID: {}", hotelID);

				for (Hotel hAPI : hotelsListAPI) {

					if (hotelID.equals(hAPI.getHotelId())) {
						itemToCheck = hAPI;
					}
				}
				if (itemToCheck == null) {

					for (Map.Entry<String, Page> excursionsPages : hotelsMapping
							.get(hotelID).entrySet()) {
						Page excursionPage = excursionsPages.getValue();

						LOGGER.debug("Updating hotel {}", hotelID);

						if (excursionPage == null) {
							throw new ImporterException(
									"Cannot set hotel page " + hotelID);
						}

						Node excursionContentNode = excursionPage.getContentResource().adaptTo(Node.class);

						if (excursionContentNode == null) {
							throw new ImporterException(
									"Cannot set properties for hotel " + hotelID);
						}

						excursionContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

						LOGGER.trace("Hotel {} is marked to be deactivated", hotelID);
					}
					successNumber++;
					itemsWritten++;

				}
				try {
					if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
						try {
							session.save();

							LOGGER.debug("{} hotels imported, saving session", +itemsWritten);
						} catch (RepositoryException e) {
							session.refresh(true);
						}
					}
				} catch (RepositoryException e) {
					errorNumber++;

					LOGGER.warn("Import error {}", e.getMessage());
				}
			}


			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.debug("{} hotels imported, saving session", +itemsWritten);
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}
		} catch (LoginException | ImporterException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (ApiException e) {
			LOGGER.error("Cannot read excursions from API", e);
		} catch (RepositoryException e) {
			LOGGER.error("Error writing data", e);
		}

		LOGGER.debug("Ending hotels disactive update, success: {}, error: {}", +successNumber, +errorNumber);

		return new ImportResult(successNumber, errorNumber);
	}


    /**
     * TODO it seems a lot of elements are marked as modified on API, compare API data against CRX data before update
     */
    @Override
    public ImportResult updateHotels() {
        LOGGER.debug("Starting hotels update");

        int successNumber = 0;
        int errorNumber = 0;
        int apiPage = 1;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final HotelsApi hotelsApi = new HotelsApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage, "lastModificationDateHotels");

            LOGGER.debug("Last import date for hotels {}", lastModificationDate);

            // city mapping
            final Map<Integer, Map<String, Page>> portsMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "cityId");

            // hotel mapping
            final Map<Integer, Map<String, Page>> hotelsMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/hotel\"]", "hotelId");

            int itemsWritten = 0;
            List<Hotel77> hotels;

            do {
                final ApiResponse<List<Hotel77>> apiResponse = hotelsApi.hotelsGetChangesWithHttpInfo(lastModificationDate,
                        apiPage , pageSize, null);
                hotels = apiResponse.getData();

                // TODO replace by header
                LOGGER.trace("Total hotels : {}, page : {}, hotels for this page : {}", hotels.size(), apiPage, hotels.size());

                for (Hotel77 hotel : hotels) {
                    LOGGER.debug("Updating hotel: {}", hotel.getHotelName());

                    try {
                        if (hotelsMapping.containsKey(hotel.getHotelId())) {
                            // if hotels are found, update it
                            for (Map.Entry<String, Page> hotelsPages : hotelsMapping.get(hotel.getHotelId()).entrySet()) {
                                final Page hotelPage = hotelsPages.getValue();

                                LOGGER.trace("Updating hotel {}", hotel.getHotelName());

                                if (hotelPage == null) {
                                    throw new ImporterException("Cannot set hotel page " + hotel.getHotelName());
                                }

                                // depending of the city status, mark the page to be activated or deactivated
                                if (BooleanUtils.isTrue(hotel.getIsDeleted())) {
                                    final Node hotelContentNode = hotelPage.getContentResource().adaptTo(Node.class);

                                    if (hotelContentNode == null) {
                                        throw new ImporterException("Cannot set properties for hotel " + hotel.getHotelName());
                                    }

                                    hotelContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

                                    LOGGER.trace("Hotel {} is marked to be deactivated", hotel.getHotelName());
                                } else {
                                    final Node hotelContentNode = updateHotelContentNode(hotel, hotelPage);
                                    hotelContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                    LOGGER.trace("Hotel {} is marked to be activated", hotel.getHotelName());
                                }
                            }
                        } else {
                            // else create port page for each language
                            final Integer cityId = hotel.getCities().size() > 0 ? hotel.getCities().get(0).getCityId() : null;

                            if (cityId == null) {
                                throw new ImporterException("Hotel have no city");
                            }

                            if (!portsMapping.containsKey(cityId)) {
                                throw new ImporterException("Cannot find city with id " + cityId);
                            }

                            for (Map.Entry<String, Page> portsPage : portsMapping.get(cityId).entrySet()) {
                                // Getting port page
                                final Page portPage = portsPage.getValue();

                                if (portPage == null) {
                                    throw new ImporterException("Error getting port page " + cityId);
                                }

                                LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                                // Creating subpage "hotel" if not present
                                Page hotelsPage;
                                if (portPage.hasChild(WcmConstants.NN_HOTELS)) {
                                    hotelsPage = pageManager.getPage(portPage.getPath() + "/hotels");
                                } else {
                                    hotelsPage = pageManager.create(portPage.getPath(), WcmConstants.NN_HOTELS,
                                            "/apps/silversea/silversea-com/templates/page", "Hotels", false);

                                    LOGGER.trace("{} page is not existing, creating it", hotelsPage.getPath());
                                }

                                // Creating hotel page
                                final Page hotelPage = pageManager.create(hotelsPage.getPath(),
                                        JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
                                                StringsUtils.getFormatWithoutSpecialCharacters(hotel.getHotelName())),
                                        WcmConstants.PAGE_TEMPLATE_HOTEL,
                                        StringsUtils.getFormatWithoutSpecialCharacters(hotel.getHotelName()), false);

                                LOGGER.trace("Creating hotel {} in city {}", hotel.getHotelName(), portPage.getPath());

                                // If hotel is created, set the properties
                                if (hotelPage == null) {
                                    throw new ImporterException(
                                            "Cannot create hotel page for hotel " + hotel.getHotelName());
                                }

                                final Node hotelContentNode = updateHotelContentNode(hotel, hotelPage);
                                hotelContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                LOGGER.trace("Hotel {} successfully created", hotelPage.getPath());
                            }
                        }

                        successNumber++;
                        itemsWritten++;

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} hotels imported, saving session", +itemsWritten);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (RepositoryException | ImporterException | WCMException e) {
                        errorNumber++;

                        LOGGER.warn("Import error {}", e.getMessage());
                    }
                }

                apiPage++;
            } while (hotels.size() > 0);

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("citiesUrl"), "lastModificationDateHotels", true);
            
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} hotels imported, saving session");
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read hotels from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error writing data", e);
        }

        LOGGER.debug("Ending hotels update, success: {}, error: {}, api calls: {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public void importOneHotel(String hotelId) {
        // TODO implement
    }

    /**
     * Update hotel properties from API
     *
     * @param hotel hotel object from API
     * @param hotelPage page of the hotel
     * @return the content node of the hotel page, updated
     * @throws ImporterException if the hotel page cannot be updated
     */
    private Node updateHotelContentNode(Hotel77 hotel, Page hotelPage) throws ImporterException {
        final Node hotelContentNode = hotelPage.getContentResource().adaptTo(Node.class);

        if (hotelContentNode == null) {
            throw new ImporterException("Cannot set properties for hotel " + hotel.getHotelName());
        }

        try {
            hotelContentNode.setProperty(JcrConstants.JCR_TITLE, hotel.getHotelName());
            hotelContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, hotel.getDescription());
            hotelContentNode.setProperty("image", hotel.getImageUrl());
            hotelContentNode.setProperty("code", hotel.getHotelCod());
            hotelContentNode.setProperty("hotelId", hotel.getHotelId());

            // Set livecopy mixin
            if (!LanguageHelper.getLanguage(hotelPage).equals("en")) {
                hotelContentNode.addMixin("cq:LiveRelationship");
            }
        } catch (RepositoryException e) {
            throw new ImporterException("Cannot set properties for hotel " + hotel.getHotelName(), e);
        }

        return hotelContentNode;
    }
}
