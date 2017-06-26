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
import com.silversea.aem.importers.services.HotelImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.model.Hotel;
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
import java.util.*;

@Service
@Component(label = "Silversea.com - Hotels importer")
public class HotelImporterImpl extends BaseImporter implements HotelImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(HotelImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private ApiCallService apiCallService;

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

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            // Session initialization
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            List<Hotel> hotels;
            int i = 1, j = 0;

            LOGGER.debug("Importing hotels");

            do {
                hotels = apiCallService.getHotels(i, pageSize);

                for (Hotel hotel : hotels) {
                    LOGGER.trace("Importing hotel: {}", hotel.getHotelName());

                    try {
                        // Getting cities with the city id read from the hotel
                        Integer cityId = hotel.getCities().size() > 0 ? hotel.getCities().get(0).getCityId() : null;

                        if (cityId == null) {
                            throw new ImporterException("Hotel have no city");
                        }

                        Iterator<Resource> portsResources = resourceResolver.findResources(
                                "/jcr:root/content/silversea-com"
                                        + "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

                        if (!portsResources.hasNext()) {
                            throw new ImporterException("Cannot find city with id " + cityId);
                        }

                        while (portsResources.hasNext()) {
                            // Getting port page
                            Page portPage = portsResources.next().adaptTo(Page.class);

                            if (portPage == null) {
                                throw new ImporterException("Error getting port page " + portPage.getPath());
                            }

                            LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                            // Creating subpage "hotel" if not present
                            Page hotelsPage;
                            if (portPage.hasChild("hotels")) {
                                hotelsPage = pageManager.getPage(portPage.getPath() + "/hotels");
                            } else {
                                hotelsPage = pageManager.create(portPage.getPath(), "hotels",
                                        "/apps/silversea/silversea-com/templates/page", "Hotels",
                                        false);

                                LOGGER.trace("{} page is not existing, creating it", hotelsPage.getPath());
                            }

                            // Creating hotel page
                            final Page hotelPage = pageManager.create(hotelsPage.getPath(),
                                    JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
                                            StringHelper.getFormatWithoutSpecialCharcters(
                                                    hotel.getHotelName())),
                                    TemplateConstants.PATH_HOTEL,
                                    StringHelper.getFormatWithoutSpecialCharcters(hotel.getHotelName()),
                                    false);

                            LOGGER.trace("Creating hotel {} in city {}", hotel.getHotelName(), portPage.getPath());

                            // If hotel is created, set the properties
                            if (hotelPage == null) {
                                throw new ImporterException("Cannot create hotel page for hotel " + hotel.getHotelName());
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

            setLastModificationDate(pageManager, session,
                    apiConfig.apiRootPath("citiesUrl"), "lastModificationDateHotels");

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
    public void updateHotels() {
    }

    @Override
    public void importOneHotel(String hotelId) {
    }
}
