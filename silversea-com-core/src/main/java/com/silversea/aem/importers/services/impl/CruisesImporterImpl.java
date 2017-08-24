package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Voyage;
import io.swagger.client.model.Voyage77;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.mime.MimeTypeService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Service
@Component
public class CruisesImporterImpl implements CruisesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private MimeTypeService mimeTypeService;

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
    public ImportResult importAllItems() {
        LOGGER.debug("Starting cruises import");

        int successNumber = 0;
        int errorNumber = 0;
        int apiPage = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final VoyagesApi voyagesApi = new VoyagesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing cruises deletion
            LOGGER.debug("Cleaning already imported cruises");

            // removing assets
            /*ImporterUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/dam/silversea-com/api-provided/cruises"
                    + "//element(*,sling:OrderedFolder)");*/

            ImportersUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]");

            // Initializing elements necessary to import cruise
            // destinations
            final Map<String, List<String>> destinationsMapping = getDestinationsMapping(resourceResolver);

            // ships
            final Map<Integer, Map<String, String>> shipsMapping = ImportersUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/ship\"]", "shipId");

            // features
            final Map<Integer, String> featuresMapping = getFeaturesMap(resourceResolver);

            // Importing cruises
            List<Voyage> cruises;
            int itemsWritten = 0;

            do {
                cruises = voyagesApi.voyagesGet(null, null, null, null, null, apiPage, pageSize, null, null);

                for (Voyage cruise : cruises) {
                    LOGGER.trace("importing cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

                    try {
                        final List<String> destinationPaths = destinationsMapping.get(String.valueOf(cruise.getDestinationId()));

                        if (destinationPaths == null) {
                            throw new ImporterException("Cannot find destination with id " + String.valueOf(cruise.getDestinationId()));
                        }

                        for (String destinationPath : destinationPaths) {
                            final Resource destinationResource = resourceResolver.getResource(destinationPath);

                            if (destinationResource == null) {
                                throw new ImporterException("Destination " + destinationPath + " cannot be found");
                            }

                            final Page destinationPage = destinationResource.adaptTo(Page.class);

                            final String language = LanguageHelper.getLanguage(destinationPage);

                            LOGGER.trace("Destination language : {}", language);

                            final String cruiseTitle = getTranslatedCruiseTitle(language, cruise.getVoyageName());
                            final Node cruiseContentNode = createCruisePage(pageManager, destinationPath, cruise.getVoyageName(), cruise.getVoyageCod());

                            // Adding tags
                            List<String> tagIds = new ArrayList<>();

                            // setting cruise type
                            if (cruise.getIsExpedition() != null && cruise.getIsExpedition()) {
                                tagIds.add(WcmConstants.TAG_CRUISE_TYPE_EXPEDITION);
                            } else {
                                tagIds.add(WcmConstants.TAG_CRUISE_TYPE_CRUISE);
                            }

                            // setting features
                            for (int featureId : cruise.getFeatures()) {
                                if (featuresMapping.containsKey(featureId)) {
                                    tagIds.add(featuresMapping.get(featureId));
                                }
                            }

                            cruiseContentNode.setProperty("cq:tags", tagIds.toArray(new String[tagIds.size()]));

                            // setting ship reference
                            if (shipsMapping.containsKey(cruise.getShipId())) {
                                if (shipsMapping.get(cruise.getShipId()).containsKey(language)) {
                                    LOGGER.trace("Associating ship {} to cruise", shipsMapping.get(cruise.getShipId()).get(language));

                                    cruiseContentNode.setProperty("shipReference", shipsMapping.get(cruise.getShipId()).get(language));
                                }
                            }

                            // setting other properties
                            cruiseContentNode.setProperty("apiTitle", cruiseTitle);
                            cruiseContentNode.setProperty("importedDescription", cruise.getVoyageDescription());
                            cruiseContentNode.setProperty("startDate", cruise.getDepartDate().toGregorianCalendar());
                            cruiseContentNode.setProperty("endDate", cruise.getArriveDate().toGregorianCalendar());
                            cruiseContentNode.setProperty("voyageHighlights", cruise.getVoyageHighlights());
                            cruiseContentNode.setProperty("duration", cruise.getDays());
                            cruiseContentNode.setProperty("cruiseCode", cruise.getVoyageCod());
                            cruiseContentNode.setProperty("cruiseId", cruise.getVoyageId());

                            // Set livecopy mixin
                            if (!language.equals("en")) {
                                cruiseContentNode.addMixin("cq:LiveRelationship");
                                cruiseContentNode.addMixin("cq:PropertyLiveSyncCancelled");

                                cruiseContentNode.setProperty("cq:propertyInheritanceCancelled", new String[]{"jcr:title", "apiTitle", "sling:alias"});
                            }

                            associateMapAsset(assetManager, session, cruiseContentNode, destinationPage.getName(), cruise.getMapUrl());

                            successNumber++;
                            itemsWritten++;

                            batchSave(session, itemsWritten);
                        }
                    } catch (RepositoryException | WCMException | ImporterException e) {
                        LOGGER.error("Cannot write cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod(), e);

                        errorNumber++;
                    }
                }

                apiPage++;
            } while (cruises.size() > 0);

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
                    "lastModificationDateCruises", false);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} cruises imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import cruises", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cruises from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.info("Ending cruises import, success: {}, error: {}, api calls: {}", +successNumber, +errorNumber,
                apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        LOGGER.debug("Starting cruises update");

        int successNumber = 0;
        int errorNumber = 0;
        int apiPage = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final VoyagesApi voyagesApi = new VoyagesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Initializing elements necessary to import exclusive offers
            // cruises mapping
            final Map<Integer, Map<String, Page>> cruisesMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
                    "cruiseId");

            // Initializing elements necessary to import cruise
            // destinations
            final Map<String, List<String>> destinationsMapping = getDestinationsMapping(resourceResolver);

            // ships
            final Map<Integer, Map<String, String>> shipsMapping = ImportersUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/ship\"]", "shipId");

            // features
            final Map<Integer, String> featuresMapping = getFeaturesMap(resourceResolver);

            // getting last import date
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("cruisesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage, "lastModificationDateCruises");

            LOGGER.debug("Last import date for cruises {}", lastModificationDate);

            List<Voyage77> cruises;
            int itemsWritten = 0;

            do {
                cruises = voyagesApi.voyagesGetChanges(lastModificationDate, apiPage, pageSize, null, null);

                for (final Voyage77 cruise : cruises) {
                    try {
                        if (cruisesMapping.containsKey(cruise.getVoyageId()) && cruise.getIsDeleted() != null && cruise.getIsDeleted()) {
                            // deactivate cruise
                            LOGGER.debug("deactivating cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

                            for (Map.Entry<String, Page> cruisePages : cruisesMapping.get(cruise.getVoyageId()).entrySet()) {
                                final Node cruiseContentNode = cruisePages.getValue().getContentResource().adaptTo(Node.class);

                                if (cruiseContentNode == null) {
                                    throw new ImporterException("Cannot get content cruise node");
                                }

                                cruiseContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

                                successNumber++;
                                itemsWritten++;

                                batchSave(session, itemsWritten);
                            }
                        } else if (cruise.getIsDeleted() == null || !cruise.getIsDeleted()) {
                            if (cruisesMapping.containsKey(cruise.getVoyageId())) {
                                // update cruise
                                LOGGER.debug("Updating cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

                                for (Map.Entry<String, Page> cruisePages : cruisesMapping.get(cruise.getVoyageId()).entrySet()) {
                                    final Node cruiseContentNode = cruisePages.getValue().getContentResource().adaptTo(Node.class);

                                    if (cruiseContentNode == null) {
                                        throw new ImporterException("Cannot get content cruise node");
                                    }

                                    updateCruiseData(assetManager, session, shipsMapping, featuresMapping, cruise, LanguageHelper.getLanguage(cruisePages.getValue()), cruiseContentNode);

                                    successNumber++;
                                    itemsWritten++;

                                    batchSave(session, itemsWritten);
                                }
                            } else {
                                // create cruise
                                LOGGER.debug("Creating cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod());

                                final List<String> destinationPaths = destinationsMapping.get(String.valueOf(cruise.getDestinationId()));

                                if (destinationPaths == null) {
                                    throw new ImporterException("Cannot find destination with id " + String.valueOf(cruise.getDestinationId()));
                                }

                                for (String destinationPath : destinationPaths) {
                                    final Resource destinationResource = resourceResolver.getResource(destinationPath);

                                    if (destinationResource == null) {
                                        throw new ImporterException("Destination " + destinationPath + " cannot be found");
                                    }

                                    final Node cruiseContentNode = createCruisePage(pageManager, destinationPath, cruise.getVoyageName(), cruise.getVoyageCod());

                                    // update cruise data
                                    final Page destinationPage = destinationResource.adaptTo(Page.class);
                                    updateCruiseData(assetManager, session, shipsMapping, featuresMapping, cruise, LanguageHelper.getLanguage(destinationPage), cruiseContentNode);

                                    successNumber++;
                                    itemsWritten++;

                                    batchSave(session, itemsWritten);
                                }
                            }
                        }
                    } catch (RepositoryException | WCMException | ImporterException e) {
                        LOGGER.warn("Cannot write cruise {} ({}) - {}", cruise.getVoyageName(), cruise.getVoyageCod(), e.getMessage());

                        errorNumber++;
                    }
                }

                apiPage++;
            } while (cruises.size() > 0);

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
                    "lastModificationDateCruises", false);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} cruises imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import cruises", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cruises from API", e);
        }

        LOGGER.info("Ending cruises update, success: {}, error: {}, api calls: {}", +successNumber, +errorNumber,
                apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public JSONObject getJsonMapping() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        JSONObject jsonObject = new JSONObject();

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);

            Iterator<Resource> cruises = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]", "xpath");

            while (cruises.hasNext()) {
                final Resource cruise = cruises.next();
                final Page cruisePage = cruise.adaptTo(Page.class);

                final Resource childContent = cruise.getChild(JcrConstants.JCR_CONTENT);

                if (cruisePage != null && childContent != null) {
                    final ValueMap childContentProperties = childContent.getValueMap();
                    final String cruiseCode = childContentProperties.get("cruiseCode", String.class);
                    final String lang = cruisePage.getAbsoluteParent(2).getName();
                    final String path = cruisePage.getPath();

                    if (cruiseCode != null && lang != null) {
                        try {
                            if (jsonObject.has(cruiseCode)) {
                                final JSONObject cruiseObject = jsonObject.getJSONObject(cruiseCode);
                                cruiseObject.put(lang, path);
                                jsonObject.put(cruiseCode, cruiseObject);
                            } else {
                                JSONObject shipObject = new JSONObject();
                                shipObject.put(lang, path);
                                jsonObject.put(cruiseCode, shipObject);
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Cannot add cruise {} with path {} to cruises array", cruiseCode, cruise.getPath(), e);
                        }
                    }
                }
            }

        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        }

        return jsonObject;
    }

    private static Map<String, List<String>> getDestinationsMapping(ResourceResolver resourceResolver) {
        final Iterator<Resource> destinations = resourceResolver.findResources("/jcr:root/content/silversea-com"
                + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/destination\"]", "xpath");

        final Map<String, List<String>> destinationsMapping = new HashMap<>();
        while (destinations.hasNext()) {
            final Resource destination = destinations.next();

            final String destinationId = destination.getValueMap().get("destinationId", String.class);

            if (destinationId != null) {
                if (destinationsMapping.containsKey(destinationId)) {
                    destinationsMapping.get(destinationId).add(destination.getParent().getPath());
                } else {
                    final List<String> destinationPaths = new ArrayList<>();
                    destinationPaths.add(destination.getParent().getPath());
                    destinationsMapping.put(destinationId, destinationPaths);
                }

                LOGGER.trace("Adding destination {} ({}) to cache", destination.getPath(), destinationId);
            }
        }
        return destinationsMapping;
    }

    private static Map<Integer, String> getFeaturesMap(ResourceResolver resourceResolver) {
        final Iterator<Resource> features = resourceResolver.findResources("/jcr:root/etc/tags/features//element(*,cq:Tag)[featureId]", "xpath");

        final Map<Integer, String> featuresMapping = new HashMap<>();
        while (features.hasNext()) {
            final Resource feature = features.next();

            final Integer featureId = feature.getValueMap().get("featureId", Integer.class);

            if (featureId != null) {
                final Tag tag = feature.adaptTo(Tag.class);
                if (tag != null) {
                    featuresMapping.put(featureId, tag.getTagID());

                    LOGGER.trace("Adding feature {} ({}) to cache", feature.getPath(), featureId);
                }
            }
        }
        return featuresMapping;
    }

    private String getTranslatedCruiseTitle(String language, String voyageName) {
        String cruiseTitle = voyageName;

        switch (language) {
            case "fr":
                cruiseTitle = voyageName.replace(" to ", " à ");
                break;
            case "es":
            case "pt-br":
                cruiseTitle = voyageName.replace(" to ", " a ");
                break;
            case "de":
                cruiseTitle = voyageName.replace(" to ", " nach ");
                break;
        }

        return cruiseTitle;
    }

    private Node createCruisePage(PageManager pageManager, String destinationPath, String voyageName, String voyageCode) throws WCMException, RepositoryException {
        // old : [Voyage Code]-[Departure port]-to-[Arrival port]
        // new : [Departure port]-to(i18n)-[Arrival port]-[Voyage Code]
        final String pageName = JcrUtil.createValidName(StringUtils
                .stripAccents(voyageName + " - " + voyageCode), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING)
                .replaceAll("-+", "-");

        // creating cruise page - uniqueness is derived from cruise code
        final Page cruisePage = pageManager.create(destinationPath,
                pageName, WcmConstants.PAGE_TEMPLATE_CRUISE, voyageCode + " - " + voyageName, false);

        final Resource cruiseContentResource = cruisePage.getContentResource();
        final Node cruiseContentNode = cruiseContentResource.adaptTo(Node.class);

        // setting alias
        // TODO replace by check on language
        if (destinationPath.contains("/fr/") || destinationPath.contains("/es/") || destinationPath.contains("/pt-br/")) {
            cruiseContentNode.setProperty("sling:alias", pageName.replace("-to-", "-a-"));
        } else if (destinationPath.contains("/de/")) {
            cruiseContentNode.setProperty("sling:alias", pageName.replace("-to-", "-nach-"));
        }
        return cruiseContentNode;
    }

    private void associateMapAsset(final AssetManager assetManager, final Session session, final Node cruiseContentNode,
                                   final String destinationPageName, final String mapUrl) throws RepositoryException {
        // download and associate map
        if (StringUtils.isNotEmpty(mapUrl)) {
            try {
                final String filename = StringUtils.substringAfterLast(mapUrl, "/");

                final String assetPath = "/content/dam/silversea-com/api-provided/cruises/"
                        + destinationPageName + "/" + cruiseContentNode.getParent().getName() + "/" + filename;

                if (session.itemExists(assetPath)) {
                    cruiseContentNode.setProperty("itinerary", assetPath);
                } else {
                    final InputStream inputStream = new URL(mapUrl).openStream();
                    final Asset asset = assetManager.createAsset(assetPath, inputStream,
                            mimeTypeService.getMimeType(mapUrl), false);

                    cruiseContentNode.setProperty("itinerary", asset.getPath());
                }

                LOGGER.trace("Creating itinerary asset {}", assetPath);
            } catch (IOException e) {
                LOGGER.warn("Cannot import itinerary image {}", mapUrl);
            }
        }
    }

    private void updateCruiseData(AssetManager assetManager, Session session, Map<Integer, Map<String, String>> shipsMapping,
                                  Map<Integer, String> featuresMapping, Voyage77 cruise,
                                  String language, Node cruiseContentNode) throws RepositoryException {
        // Adding tags
        List<String> tagIds = new ArrayList<>();

        // setting cruise type
        if (cruise.getIsExpedition() != null && cruise.getIsExpedition()) {
            tagIds.add(WcmConstants.TAG_CRUISE_TYPE_EXPEDITION);
        } else {
            tagIds.add(WcmConstants.TAG_CRUISE_TYPE_CRUISE);
        }

        // setting features
        for (int featureId : cruise.getFeatures()) {
            if (featuresMapping.containsKey(featureId)) {
                tagIds.add(featuresMapping.get(featureId));
            }
        }

        cruiseContentNode.setProperty("cq:tags", tagIds.toArray(new String[tagIds.size()]));

        // setting ship reference
        if (shipsMapping.containsKey(cruise.getShipId())) {
            if (shipsMapping.get(cruise.getShipId()).containsKey(language)) {
                LOGGER.trace("Associating ship {} to cruise", shipsMapping.get(cruise.getShipId()).get(language));

                cruiseContentNode.setProperty("shipReference", shipsMapping.get(cruise.getShipId()).get(language));
            }
        }

        // setting other properties
        final String cruiseTitle = getTranslatedCruiseTitle(language, cruise.getVoyageName());
        cruiseContentNode.setProperty("apiTitle", cruiseTitle);
        cruiseContentNode.setProperty("importedDescription", cruise.getVoyageDescription());
        cruiseContentNode.setProperty("startDate", cruise.getDepartDate().toGregorianCalendar());
        cruiseContentNode.setProperty("endDate", cruise.getArriveDate().toGregorianCalendar());
        cruiseContentNode.setProperty("voyageHighlights", cruise.getVoyageHighlights());
        cruiseContentNode.setProperty("duration", cruise.getDays());
        cruiseContentNode.setProperty("cruiseCode", cruise.getVoyageCod());
        cruiseContentNode.setProperty("cruiseId", cruise.getVoyageId());

        // Set livecopy mixin
        if (!language.equals("en")) {
            cruiseContentNode.addMixin("cq:LiveRelationship");
            cruiseContentNode.addMixin("cq:PropertyLiveSyncCancelled");

            cruiseContentNode.setProperty("cq:propertyInheritanceCancelled", new String[]{"jcr:title", "apiTitle", "sling:alias"});
        }

        associateMapAsset(assetManager, session, cruiseContentNode, cruiseContentNode.getParent().getParent().getName(), cruise.getMapUrl());

        cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
    }

    private void batchSave(Session session, int itemsWritten) throws RepositoryException {
        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
            try {
                session.save();

                LOGGER.info("{} cruises imported, saving session", +itemsWritten);
            } catch (RepositoryException e) {
                session.refresh(true);
            }
        }
    }
}
