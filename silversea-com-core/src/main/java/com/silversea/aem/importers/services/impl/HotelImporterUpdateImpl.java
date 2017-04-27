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
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.HotelUpdateImporter;

import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.model.Hotel77;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Service
@Component(label = "Silversea.com - HotelsMAJ importer")
public class HotelImporterUpdateImpl extends BaseImporter implements HotelUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(HotelImporterUpdateImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicat;

    @Override
    public void importUpdateHotel() throws IOException, ReplicationException {
        final String authorizationHeader = getAuthorizationHeader("/api/v1/hotels");

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            // get authentification to the Hotels API
            HotelsApi hotelsApi = new HotelsApi();
            hotelsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

            // get parent content resource
            Resource resParent = resourceResolver.getResource(ImportersConstants.BASEPATH_PORTS);
            Date date = resParent.getChild("jcr:content").getValueMap().get("lastModificationDate", Date.class);

            // get last importing date
            String dateFormat = "yyyyMMdd";
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String currentDate;
            if(date !=null){
             currentDate = formatter.format(date.getTime()).toString();
           

            int i = 1;

            List<Hotel77> hotels;

            do {
                // gets all hotels changes
                hotels = hotelsApi.hotelsGetChanges(currentDate, i, 100, null);

                int j = 0;

                for (Hotel77 hotel : hotels) {

                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/hotelId=\"" + hotel.getHotelId() + "\"]", "xpath");

                    Page hotelPage = null;

                    if (resources.hasNext()) {
                        hotelPage = resources.next().adaptTo(Page.class);
                        
                        //  désactivation de la page si le boolean est a true
                        if (BooleanUtils.isTrue(hotel.getIsDeleted())) {
                            replicat.replicate(session, ReplicationActionType.DEACTIVATE, hotelPage.getPath());
                        }
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
                                        TemplateConstants.PATH_HOTEL, hotel.getHotelName(), false);

                                LOGGER.debug("Creating excursion {}", hotel.getHotelName());
                            } else {
                                LOGGER.debug("No city found with id {}", cityId);
                            }
                        } else {
                            LOGGER.debug("Excursion have no city attached, not imported");
                        }
                    }

                    if (hotelPage != null && BooleanUtils.isFalse(hotel.getIsDeleted())) {
                        // Test if hotel are deleted

                        Node hotelPageContentNode = hotelPage.getContentResource().adaptTo(Node.class);
                        hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE, hotel.getHotelName());
                        hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, hotel.getDescription());
                        hotelPageContentNode.setProperty("image", hotel.getImageUrl());
                        hotelPageContentNode.setProperty("code", hotel.getHotelCod());
                        hotelPageContentNode.setProperty("hotelId", hotel.getHotelId());
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
                    Node rootNode = resParent.getChild(JcrConstants.JCR_CONTENT).adaptTo(Node.class);
                    rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            resourceResolver.close();
            
            } } catch (ApiException | WCMException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing hotels", e);
        }
    }
}
