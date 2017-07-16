package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.HotelsImporter;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.model.Hotel;
import io.swagger.client.model.Hotel77;
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

        Map<String, Object> authenticationPrams = new HashMap<>();
        authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final HotelsApi hotelsApi = new HotelsApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            List<Hotel> hotels;
            int i = 1, j = 0;

            LOGGER.debug("Importing hotels");

            do {
                hotels = hotelsApi.hotelsGet(null, i, pageSize, null);

                for (Hotel hotel : hotels) {
                    LOGGER.trace("Importing hotel: {}", hotel.getHotelName());

                    try {
                        // Getting cities with the city id read from the hotel
                        Integer cityId = hotel.getCities().size() > 0 ? hotel.getCities().get(0).getCityId() : null;

                        if (cityId == null) {
                            throw new ImporterException("Hotel have no city");
                        }

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
                            if (portPage.hasChild("hotels")) {
                                hotelsPage = pageManager.getPage(portPage.getPath() + "/hotels");
                            } else {
                                hotelsPage = pageManager.create(portPage.getPath(), "hotels",
                                        "/apps/silversea/silversea-com/templates/page", "Hotels", false);

                                LOGGER.trace("{} page is not existing, creating it", hotelsPage.getPath());
                            }

                            // Creating hotel page
                            final Page hotelPage = pageManager.create(hotelsPage.getPath(),
                                    JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
                                            StringHelper.getFormatWithoutSpecialCharcters(hotel.getHotelName())),
                                    TemplateConstants.PATH_HOTEL,
                                    StringHelper.getFormatWithoutSpecialCharcters(hotel.getHotelName()), false);

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

                            LOGGER.trace("Hotel {} successfully created", hotelPage.getPath());

                            successNumber++;
                            j++;
                        }

                        if (j % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} hotels imported, saving session", +j);
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
            } while (hotels.size() > 0);

            ImporterUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateHotels");

            resourceResolver.close();
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read hotels from API", e);
        }

        LOGGER.debug("Ending hotels import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateHotels() {
        LOGGER.debug("Starting cities update");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final HotelsApi hotelsApi = new HotelsApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final String lastModificationDate = ImporterUtils.getDateFromPageProperties(rootPage, "lastModificationDateHotels");

            LOGGER.debug("Last import date for hotels {}", lastModificationDate);

            int j = 0, i = 1;
            List<Hotel77> hotels;

            do {
                final ApiResponse<List<Hotel77>> apiResponse = hotelsApi.hotelsGetChangesWithHttpInfo(lastModificationDate,
                        i , pageSize, null);
                hotels = apiResponse.getData();

                // TODO replace by header
                LOGGER.trace("Total hotels : {}, page : {}, hotels for this page : {}", hotels.size(), i, hotels.size());

                for (Hotel77 hotel : hotels) {
                    LOGGER.debug("Updating hotel: {}", hotel.getHotelName());

                    try {
                        // Getting all the hotel pages with the current hotelId
                        Iterator<Resource> hotelsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com//element(*,cq:Page)[" +
                                        "jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/hotel\"" +
                                        " and jcr:content/hotelId=\"" + hotel.getHotelId() + "\"]", "xpath");

                        if (hotelsResources.hasNext()) {
                            // if hotels are found, update it
                            while (hotelsResources.hasNext()) {
                                final Resource hotelResource = hotelsResources.next();
                                final Page hotelPage = hotelResource.adaptTo(Page.class);

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

                                    hotelContentNode.setProperty("toDeactivate", true);

                                    LOGGER.trace("Hotel {} is marked to be deactivated", hotel.getHotelName());
                                } else {
                                    final Node hotelContentNode = updateHotelContentNode(hotel, hotelPage);
                                    hotelContentNode.setProperty("toActivate", true);

                                    LOGGER.trace("Hotel {} is marked to be activated", hotel.getHotelName());
                                }
                            }
                        } else {
                            // else create port page for each language
                            Integer cityId = hotel.getCities().size() > 0 ? hotel.getCities().get(0).getCityId() : null;

                            if (cityId == null) {
                                throw new ImporterException("Hotel have no city");
                            }

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
                                if (portPage.hasChild("hotels")) {
                                    hotelsPage = pageManager.getPage(portPage.getPath() + "/hotels");
                                } else {
                                    hotelsPage = pageManager.create(portPage.getPath(), "hotels",
                                            "/apps/silversea/silversea-com/templates/page", "Hotels", false);

                                    LOGGER.trace("{} page is not existing, creating it", hotelsPage.getPath());
                                }

                                // Creating hotel page
                                final Page hotelPage = pageManager.create(hotelsPage.getPath(),
                                        JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
                                                StringHelper.getFormatWithoutSpecialCharcters(hotel.getHotelName())),
                                        TemplateConstants.PATH_HOTEL,
                                        StringHelper.getFormatWithoutSpecialCharcters(hotel.getHotelName()), false);

                                LOGGER.trace("Creating hotel {} in city {}", hotel.getHotelName(), portPage.getPath());

                                // If hotel is created, set the properties
                                if (hotelPage == null) {
                                    throw new ImporterException(
                                            "Cannot create hotel page for hotel " + hotel.getHotelName());
                                }

                                final Node hotelContentNode = updateHotelContentNode(hotel, hotelPage);
                                hotelContentNode.setProperty("toActivate", true);

                                LOGGER.trace("Hotel {} successfully created", hotelPage.getPath());
                            }
                        }

                        successNumber++;
                        j++;

                        if (j % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} hotels imported, saving session", +j);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (RepositoryException | ImporterException | WCMException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    }
                }

                i++;
            } while (hotels.size() > 0);

            ImporterUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateHotels");

            resourceResolver.close();
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read hotels from API", e);
        }

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public void importOneHotel(String hotelId) {
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
        } catch (RepositoryException e) {
            throw new ImporterException("Cannot set properties for hotel " + hotel.getHotelName(), e);
        }

        return hotelContentNode;
    }
}
