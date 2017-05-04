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
import com.silversea.aem.importers.services.CitiesUpdateImporter;

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

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicat;

    @Override
    public void importUpdateCities() throws IOException, ReplicationException {
        // final String authorizationHeader =
        // getAuthorizationHeader("/api/v1/cities/changesFrom/");

        try {
            /**
             * authentification pour le swagger
             */
//          getAuthentification(apiConfig.getLogin(), apiConfig.getPassword());
            
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            // get parent content resource
            Resource resParent = resourceResolver.getResource(ImportersConstants.BASEPATH_PORTS);
            Date date = resParent.getChild("jcr:content").getValueMap().get("lastModificationDate", Date.class);

            // get last importing date
            String dateFormat = "yyyyMMdd";
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String currentDate;

            if (date != null) {
                currentDate = formatter.format(date.getTime()).toString();

//                final String authorizationHeader = getAuthorizationHeader("/api/v1/cities/changesFrom/" + currentDate);
                final String authorizationHeader = getAuthorizationHeader("/api/v1/cities");

                CitiesApi citiesApi = new CitiesApi();
                citiesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

                Page citiesRootPage = pageManager.getPage(ImportersConstants.BASEPATH_PORTS);

                List<City77> cities;
                int i = 1;

                do {
                    cities = citiesApi.citiesGetChanges(currentDate, i, 100, null, null, null);

                    int j = 0;

                    for (City77 city : cities) {
                        LOGGER.debug("Importing city: {}", city.getCityName());

                        final String portFirstLetter = String.valueOf(city.getCityName().charAt(0));
                        final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

                        Page portFirstLetterPage;

                        if (citiesRootPage.hasChild(portFirstLetterName)) {
                            portFirstLetterPage = pageManager
                                    .getPage(ImportersConstants.BASEPATH_PORTS + "/" + portFirstLetterName);

                            LOGGER.debug("Page {} already exists", portFirstLetterName);
                        } else {
                            portFirstLetterPage = pageManager.create(citiesRootPage.getPath(), portFirstLetterName,
                                    TemplateConstants.PATH_PAGE, portFirstLetter, false);

                            LOGGER.debug("Creating page {}", portFirstLetterName);
                        }

                        Iterator<Resource> resources = resourceResolver.findResources(
                                "//element(*,cq:Page)[jcr:content/cityId=\"" + city.getCityId() + "\"]", "xpath");

                        Page portPage;

                        if (resources.hasNext()) {
                            portPage = resources.next().adaptTo(Page.class);
                            // TODO Descativation for deleted page
                            if (BooleanUtils.isTrue(city.getIsDeleted())) {
                                replicat.replicate(session, ReplicationActionType.DEACTIVATE, portPage.getPath());
                            }

                            LOGGER.debug("Port page {} with ID {} already exists", city.getCityName(),
                                    city.getCityCod());
                        } else {
                            portPage = pageManager.create(portFirstLetterPage.getPath(),
                                    JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
                                            city.getCityName()),
                                   TemplateConstants.PATH_PORT, city.getCityName(), false);

                            LOGGER.debug("Creating port {}", city.getCityName());
                        }

                        Node portPageContentNode = portPage.getContentResource().adaptTo(Node.class);

                        if (portPageContentNode.hasProperty(JcrConstants.JCR_TITLE) && portPageContentNode
                                .getProperty("jcr:title").getString().equals(city.getCityName())) {
                            portPageContentNode.setProperty(JcrConstants.JCR_TITLE, city.getCityName());
                        }

                        portPageContentNode.setProperty("apiTitle", city.getCityName());
                        portPageContentNode.setProperty("apiDescription", city.getShortDescription());
                        portPageContentNode.setProperty("apiLongDescription", city.getDescription());
                        portPageContentNode.setProperty("cityCode", city.getCityCod());
                        portPageContentNode.setProperty("cityId", city.getCityId());
                        portPageContentNode.setProperty("latitude", city.getLatitude());
                        portPageContentNode.setProperty("longitude", city.getLongitude());
                        portPageContentNode.setProperty("countryId", city.getCountryId());
                        portPageContentNode.setProperty("countryIso3", city.getCountryIso3());

                        j++;

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
            }
        } catch (ApiException | WCMException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing cities", e);
        }
    }

    @Override
    public void importUpdateCity(final String cityId) {
    }
}
