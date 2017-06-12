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
import com.silversea.aem.importers.services.HotelUpdateImporter;
import com.silversea.aem.services.ApiConfigurationService;

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
            
            final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("hotelUrl"));
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            HotelsApi hotelsApi = new HotelsApi();
            hotelsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

            Page citiesRootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            Resource resParent = citiesRootPage.adaptTo(Resource.class);
            Date date = resParent.getChild("jcr:content").getValueMap().get("lastModificationDate", Date.class);

            String dateFormat = "yyyyMMdd";
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String currentDate;
            if (date != null) {
                currentDate = formatter.format(date.getTime()).toString();

                int i = 1;

                List<Hotel77> hotels;

                do {
                    hotels = hotelsApi.hotelsGetChanges(currentDate, i, pageSize, null);

                    int j = 0;

                    for (Hotel77 hotel : hotels) {
                        try {

                            Iterator<Resource> resources = resourceResolver.findResources(
                                    "//element(*,cq:Page)[jcr:content/hotelId=\"" + hotel.getHotelId() + "\"]",
                                    "xpath");

                            Page hotelPage = null;

                            if (resources.hasNext()) {
                                hotelPage = resources.next().adaptTo(Page.class);
                                if (BooleanUtils.isTrue(hotel.getIsDeleted())) {
                                    replicat.replicate(session, ReplicationActionType.DEACTIVATE, hotelPage.getPath());
                                }
                            } else {
                                Integer cityId = hotel.getCities().size() > 0 ? hotel.getCities().get(0).getCityId()
                                        : null;

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
                                        if(!replicat.getReplicationStatus(session, hotelsPage.getPath()).isActivated()){
                                            replicat.replicate(session,ReplicationActionType.ACTIVATE, hotelsPage.getPath());
                                        }

                                        hotelPage = pageManager.create(hotelsPage.getPath(),
                                                JcrUtil.createValidChildName(hotelsPage.adaptTo(Node.class),
                                                        StringHelper
                                                                .getFormatWithoutSpecialCharcters(hotel.getHotelName())),
                                                TemplateConstants.PATH_HOTEL,
                                                StringHelper.getFormatWithoutSpecialCharcters(hotel.getHotelName()), false);

                                        LOGGER.debug("Creating hotel {}", hotel.getHotelName());
                                    } else {
                                        LOGGER.debug("No city found with id {}", cityId);
                                    }
                                } else {
                                    LOGGER.debug("hotel have no city attached, not imported");
                                }
                            }

                            if (hotelPage != null && (BooleanUtils.isFalse(hotel.getIsDeleted()) || hotel.getIsDeleted() == null)) {
                                Node hotelPageContentNode = hotelPage.getContentResource().adaptTo(Node.class);
                                hotelPageContentNode.setProperty(JcrConstants.JCR_TITLE, hotel.getHotelName());
                                hotelPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION, hotel.getDescription());
                                hotelPageContentNode.setProperty("image", hotel.getImageUrl());
                                hotelPageContentNode.setProperty("code", hotel.getHotelCod());
                                hotelPageContentNode.setProperty("hotelId", hotel.getHotelId());
                                succesNumber = succesNumber + 1;
                                j++;
                                try {
                                    session.save();
                                    replicat.replicate(session, ReplicationActionType.ACTIVATE,
                                            hotelPage.getPath());
                                } catch (RepositoryException e) {
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
                            LOGGER.debug("hotel error, number of faulures :", errorNumber);
                            j++;
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

            }else{
                throw new UpdateImporterExceptions();
            }
        } catch (ApiException | LoginException | RepositoryException e) {
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
