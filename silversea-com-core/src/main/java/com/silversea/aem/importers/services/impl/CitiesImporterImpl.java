package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.s7dam.set.ImageSet;
import com.day.cq.dam.commons.util.S7SetHelper;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.model.City;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Service
@Component(label = "Silversea.com - Cities importer")
public class CitiesImporterImpl implements CitiesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CitiesImporterImpl.class);

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
    public ImportResult importAllCities() {
        LOGGER.debug("Starting cities import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Getting paths to import data
            LOGGER.trace("Getting root page : {}", apiConfig.apiRootPath("citiesUrl"));
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final List<String> locales = ImporterUtils.getSiteLocales(pageManager);

            // Iterating over locales to import cities
            for (String locale : locales) {
                if (locale != null) {

                    final Page citiesRootPage = ImporterUtils.getPagePathByLocale(pageManager, rootPage, locale);

                    LOGGER.debug("Cleaning already imported cities");

                    Iterator<Page> children = citiesRootPage.listChildren();
                    while (children.hasNext()) {
                        final Page child = children.next();

                        try {
                            LOGGER.trace("trying to remove {}", child.getPath());

                            session.removeItem(child.getPath());
                            session.save();
                        } catch (RepositoryException e) {
                            LOGGER.error("Cannot clean already existing cities");
                        }
                    }

                    LOGGER.debug("Importing cities for locale \"{}\"", locale);

                    int j = 0;

                    if (citiesRootPage != null) {
                        List<City> cities;
                        int i = 1;

                        do {
                            cities = apiCallService.getCities(i, pageSize);

                            for (City city : cities) {
                                LOGGER.trace("Importing city: {}", city.getCityName());

                                try {
                                    String portFirstLetter;

                                    if (city.getCityName() == null) {
                                        throw new ImporterException("City name is null");
                                    }

                                    // Port parent page initialization
                                    portFirstLetter = String.valueOf(city.getCityName().charAt(0));

                                    final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

                                    Page portFirstLetterPage = pageManager
                                            .getPage(citiesRootPage.getPath() + "/" + portFirstLetterName);

                                    if (portFirstLetterPage == null) {
                                        portFirstLetterPage = pageManager.create(citiesRootPage.getPath(),
                                                portFirstLetterName, TemplateConstants.PATH_PAGE_PORT, portFirstLetter,
                                                false);

                                        LOGGER.trace("{} page is not existing, creating it", portFirstLetterName);
                                    }

                                    // Creating port page
                                    final Page portPage = pageManager.create(portFirstLetterPage.getPath(),
                                            JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
                                                    StringHelper.getFormatWithoutSpecialCharcters(city.getCityName())),
                                            TemplateConstants.PATH_PORT,
                                            StringHelper.getFormatWithoutSpecialCharcters(city.getCityName()), false);

                                    LOGGER.trace("Creating port {}", city.getCityName());

                                    // If port is created, set the properties
                                    if (portPage == null) {
                                        throw new ImporterException(
                                                "Cannot create port page for city " + city.getCityName());
                                    }

                                    Node portPageContentNode = portPage.getContentResource().adaptTo(Node.class);

                                    if (portPageContentNode == null) {
                                        throw new ImporterException(
                                                "Cannot set properties for city " + city.getCityName());
                                    }

                                    portPageContentNode.setProperty(JcrConstants.JCR_TITLE, city.getCityName());
                                    portPageContentNode.setProperty("apiTitle", city.getCityName());
                                    portPageContentNode.setProperty("apiDescription", city.getShortDescription());
                                    portPageContentNode.setProperty("apiLongDescription", city.getDescription());
                                    portPageContentNode.setProperty("cityCode", city.getCityCod());
                                    portPageContentNode.setProperty("cityId", city.getCityId());
                                    portPageContentNode.setProperty("latitude", city.getLatitude());
                                    portPageContentNode.setProperty("longitude", city.getLongitude());
                                    portPageContentNode.setProperty("countryId", city.getCountryId());
                                    portPageContentNode.setProperty("countryIso2", city.getCountryIso2());
                                    portPageContentNode.setProperty("countryIso3", city.getCountryIso3());

                                    LOGGER.trace("Port {} successfully created", portPage.getPath());

                                    successNumber++;
                                    j++;

                                    if (j % sessionRefresh == 0 && session.hasPendingChanges()) {
                                        try {
                                            session.save();

                                            LOGGER.debug("{} cities imported, saving session", +j);
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
                        } while (cities.size() > 0);

                        ImporterUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                                "lastModificationDate");
                    }
                }
            }

            resourceResolver.close();
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cities from API", e);
        }

        LOGGER.debug("Ending cities import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public void updateCities() {
        // TODO
    }

    @Override
    public void importOneCity(final String cityId) {
        // TODO
    }

    @Override
    public JSONObject getCitiesMapping() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        JSONObject jsonObject = new JSONObject();

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);

            Iterator<Resource> cities = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "xpath");

            while (cities.hasNext()) {
                Resource city = cities.next();

                Resource childContent = city.getChild(JcrConstants.JCR_CONTENT);

                if (childContent != null) {
                    ValueMap childContentProperties = childContent.getValueMap();
                    String cityId = childContentProperties.get("cityId", String.class);

                    if (cityId != null) {
                        try {
                            if (jsonObject.has(cityId)) {
                                final JSONArray jsonArray = jsonObject.getJSONArray(cityId);
                                jsonArray.put(city.getPath());

                                jsonObject.put(cityId, jsonArray);
                            } else {
                                jsonObject.put(cityId, Collections.singletonList(city.getPath()));
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Cannot add city {} with path {} to cities array", cityId, city.getPath(), e);
                        }
                    }
                }
            }

        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        }

        return jsonObject;
    }

    @Override
    public void updateCitiesAfterMigration() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final Session session = resourceResolver.adaptTo(Session.class);

            if (session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Building assets mapping list
            Iterator<Resource> assets = resourceResolver.findResources("/jcr:root/content/dam/silversea-com"
                    + "//element(*,dam:Asset)[jcr:content/metadata/initialPath]", "xpath");

            Map<String, String> assetsMapping = new HashMap<>();
            while (assets.hasNext()) {
                final Resource assetResource = assets.next();
                final ValueMap assetProperties = assetResource.getChild("jcr:content/metadata").getValueMap();

                if (assetProperties.get("initialPath", String.class) != null) {
                    assetsMapping.put(assetProperties.get("initialPath", String.class), assetResource.getPath());

                    LOGGER.trace("Adding {}/{} to assets mapping",
                            assetProperties.get("initialPath", String.class), assetResource.getPath());
                }
            }

            // Iterating over the cities to update properties
            final Iterator<Resource> cities = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "xpath");

            int i = 0;
            while (cities.hasNext()) {
                final Resource city = cities.next();
                final Resource childContent = city.getChild(JcrConstants.JCR_CONTENT);

                LOGGER.trace("Starting update of {}", city.getPath());

                try {
                    if (childContent == null) {
                        throw new ImporterException("Cannot get jcr:content child for " + city.getPath());
                    }

                    final ValueMap childContentProperties = childContent.getValueMap();
                    final Node childContentNode = childContent.adaptTo(Node.class);

                    String thumbnail = childContentProperties.get("thumbnail", String.class);
                    String[] assetSelectionReference = childContentProperties.get("assetSelectionReferenceImported", String[].class);

                    LOGGER.trace("\nthumbnail {}\nassetSelectionRef : {}", thumbnail, Arrays.toString(assetSelectionReference));

                    if (childContentNode == null) {
                        throw new ImporterException("Cannot get node for " + city.getPath());
                    }

                    // Replacing thumbnail by image node with real path
                    if (thumbnail != null && assetsMapping.containsKey(thumbnail)) {
                        try {
                            final Node imageNode = childContentNode.addNode("image", JcrConstants.NT_UNSTRUCTURED);
                            imageNode.setProperty("fileReference", assetsMapping.get(thumbnail));

                            LOGGER.trace("Updating thumbnail of city {} by {}", city.getPath(), assetsMapping.get(thumbnail));
                        } catch (RepositoryException e) {
                            LOGGER.error("Cannot change thumbnail property on city {}", city.getPath());
                        }
                    }

                    i++;

                    // Creating image set for assetReference
                    if (assetSelectionReference != null && assetSelectionReference.length > 0) {
                        try {
                            // Image set will be created in the same folder than the first asset
                            final String firstAssetInitialPath = assetSelectionReference[0];
                            final String firstAssetPath = assetsMapping.get(firstAssetInitialPath);

                            if (firstAssetPath == null) {
                                throw new ImporterException("Cannot find asset in mapping : " + firstAssetInitialPath);
                            }

                            final String folderPath = ResourceUtil.getParent(firstAssetPath);

                            if (folderPath == null) {
                                throw new ImporterException("Cannot find parent folder in DAM");
                            }

                            final Resource folder = resourceResolver.getResource(folderPath);

                            if (folder == null) {
                                throw new ImporterException("Cannot get parent folder in DAM");
                            }

                            // if the image set already exists, associate it,
                            // create it and associate it if not
                            final Resource imageSetResource = folder.getChild(city.getName());

                            if (imageSetResource != null) {
                                childContentNode.setProperty("assetSelectionReference", imageSetResource.getPath());

                                LOGGER.trace("ImageSet {} already exists, associating it", imageSetResource.getPath());
                            } else {
                                // Saving session due to conflicts in S7 helpers
                                if (session.hasPendingChanges()) {
                                    session.save();
                                }

                                // Creating image set
                                Map<String, Object> setProperties = new HashMap<>();
                                setProperties.put("dc:title", childContentProperties.get(JcrConstants.JCR_TITLE, city.getName()));

                                final ImageSet s7ImageSet = S7SetHelper.createS7ImageSet(folder, city.getName(), setProperties);

                                for (final String assetInitialPath : assetSelectionReference) {
                                    final String assetPath = assetsMapping.get(assetInitialPath);

                                    if (assetPath != null) {
                                        final Resource assetResource = resourceResolver.getResource(assetPath);

                                        if (assetResource != null) {
                                            final Asset asset = assetResource.adaptTo(Asset.class);

                                            if (asset != null) {
                                                s7ImageSet.add(asset);
                                            }
                                        }
                                    }
                                }

                                childContentNode.setProperty("assetSelectionReference", s7ImageSet.getPath());

                                LOGGER.trace("ImageSet {} created and associated", s7ImageSet.getPath());
                            }
                        } catch (ImporterException e) {
                            LOGGER.error("Cannot update city {} with image set", city.getPath(), e);
                        } catch (PersistenceException e) {
                            LOGGER.error("Cannot create image set for {}", city.getPath(), e);
                        }
                    }

                    i++;

                    // Saving session
                    if (i % sessionRefresh == 0 && session.hasPendingChanges()) {
                        try {
                            session.save();

                            LOGGER.debug("{} cities updated, saving session", +i);
                        } catch (RepositoryException e) {
                            try {
                                session.refresh(true);
                            } catch (RepositoryException e1) {
                                LOGGER.error("Cannot refresh session", e);
                            }
                        }
                    }
                } catch (ImporterException e) {
                    LOGGER.error("Cannot update city {}", city.getPath(), e);
                }

                LOGGER.trace("Update of {} finished", city.getPath());
            }

            // final session save
            try {
                session.save();

                LOGGER.debug("{} cities updated, saving session", +i);
            } catch (RepositoryException e) {
                session.refresh(false);
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error during cities update", e);
        }
    }
}
