package com.silversea.aem.importers.services.impl;

import java.io.IOException;
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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.HotelImporter;

import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.model.Hotel;
import io.swagger.client.model.Shorex;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Service
@Component(label = "Silversea.com - Hotels importer")
public class HotelImporterImpl extends BaseImporter implements HotelImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(HotelImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importHotel() throws IOException {
        final String authorizationHeader = getAuthorizationHeader("/api/v1/hotels");

        // get authentification to the Hotels API
        HotelsApi hotelsApi = new HotelsApi();
        hotelsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);
            Page citiesRootPage = pageManager.getPage(ImportersConstants.BASEPATH_PORTS);

            int i = 1;

            List<Hotel> hotels;

            do {
                // gets all hotels
                hotels = hotelsApi.hotelsGet(null, i, 100, null);

                int j = 0;

                for (Hotel hotel : hotels) {

                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/id=\"" + hotel.getHotelId() + "\"]", "xpath");

                    Page hotelPage = null;

                    if (resources.hasNext()) {
                        hotelPage = resources.next().adaptTo(Page.class);
                    } else {
                        Integer cityId = hotel.getCities().size() > 0 ? hotel.getCities().get(0).getCityId() : null;

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
                                            "/apps/silversea/silversea-com/templates/page", "Hotels", false);
                                }

                                hotelPage = pageManager.create(hotelsPage.getPath(),
                                        JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
                                                hotel.getHotelName()),
                                        "/apps/silversea/silversea-com/templates/hotel", hotel.getHotelName(),
                                        false);

                                LOGGER.debug("Creating excursion {}", hotel.getHotelName());
                            } else {
                                LOGGER.debug("No city found with id {}", cityId);
                            }
                        } else {
                            LOGGER.debug("Excursion have no city attached, not imported");
                        }
                    }

                    if (hotelPage != null) {
                        Node hotelPageContentNode = hotelPage.getContentResource().adaptTo(Node.class);
                        hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE, hotel.getHotelName());
                        hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, hotel.getDescription());
                        hotelPageContentNode.setProperty("image", hotel.getImageUrl());
                        hotelPageContentNode.setProperty("code", hotel.getHotelCod());
                        hotelPageContentNode.setProperty("id", hotel.getHotelId());
                        j++;
                    }

                    if (j % 100 == 0) {
                        if (session.hasPendingChanges()) {
                            try {
                                session.save();
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    }
                }

                i++;
            } while (hotels.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    // save migration date
                    Node rootNode = citiesRootPage.getContentResource().adaptTo(Node.class); 
                    rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            resourceResolver.close();
        } catch (ApiException | WCMException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing shorexes", e);
        }
    }
}
