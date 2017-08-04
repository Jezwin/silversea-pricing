package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesImporter;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Voyage;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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

/**
 * TODO add last import date
 */
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
        return importSampleSet(-1);
    }

    @Override
    public ImportResult importSampleSet(final int size) {
        LOGGER.debug("Starting cruises import ({})", size == -1 ? "all" : size);

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

            final VoyagesApi voyagesApi = new VoyagesApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing cruises deletion
            LOGGER.debug("Cleaning already imported cruises");

            ImporterUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]");

            // Initializing elements necessary to import cruise
            // destinations
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

            // ships
            final Iterator<Resource> ships = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/ship\"]", "xpath");

            final Map<String, Map<String, String>> shipsMapping = new HashMap<>();
            while (ships.hasNext()) {
                final Resource ship = ships.next();

                final Page shipPage = ship.getParent().adaptTo(Page.class);
                final String language = LanguageHelper.getLanguage(shipPage);

                final String shipId = ship.getValueMap().get("shipId", String.class);

                if (shipId != null) {
                    if (shipsMapping.containsKey(shipId)) {
                        shipsMapping.get(shipId).put(language, shipPage.getPath());
                    } else {
                        final HashMap<String, String> shipPaths = new HashMap<>();
                        shipPaths.put(language, shipPage.getPath());
                        shipsMapping.put(shipId, shipPaths);
                    }

                    LOGGER.trace("Adding ship {} ({}) with lang {} to cache", ship.getPath(), shipId, language);
                }
            }

            // features
            final Iterator<Resource> features = resourceResolver.findResources("/jcr:root/etc/tags/features//element(*,cq:Tag)[featureId]", "xpath");

            final Map<Integer, String> featuresMapping = new HashMap<>();
            while (features.hasNext()) {
                final Resource feature = features.next();

                final Integer featureId = feature.getValueMap().get("featureId", Integer.class);

                if (featureId != null) {
                    featuresMapping.put(featureId, feature.getPath());

                    LOGGER.trace("Adding feature {} ({}) to cache", feature.getPath(), featureId);
                }
            }

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

                            // TODO dynamically read languages
                            String cruiseTitle = cruise.getVoyageName();
                            switch (language) {
                                case "fr":
                                    cruiseTitle = cruise.getVoyageName().replace(" to ", " à ");
                                    break;
                                case "es":
                                case "pt-br":
                                    cruiseTitle = cruise.getVoyageName().replace(" to ", " a ");
                                    break;
                                case "de":
                                    cruiseTitle = cruise.getVoyageName().replace(" to ", " nach ");
                                    break;
                            }

                            // old : [Voyage Code]-[Departure port]-to-[Arrival port]
                            // new : [Departure port]-to(i18n)-[Arrival port]-[Voyage Code]
                            final String pageName = JcrUtil.createValidName(StringUtils
                                    .stripAccents(cruise.getVoyageName() + " - " + cruise.getVoyageCod()), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING)
                                    .replaceAll("-+", "-");

                            // creating cruise page - uniqueness is derived from cruise code
                            final Page cruisePage = pageManager.create(destinationPath,
                                    pageName, WcmConstants.PAGE_TEMPLATE_CRUISE, cruiseTitle, false);

                            final Resource cruiseContentResource = cruisePage.getContentResource();
                            final Node cruiseContentNode = cruiseContentResource.adaptTo(Node.class);

                            // setting alias
                            // TODO replace by check on language
                            if (destinationPath.contains("/fr/") || destinationPath.contains("/es/") || destinationPath.contains("/pt-br/")) {
                                cruiseContentNode.setProperty("sling:alias", pageName.replace("-to-", "-a-"));
                            } else if (destinationPath.contains("/de/")) {
                                cruiseContentNode.setProperty("sling:alias", pageName.replace("-to-", "-nach-"));
                            }

                            // Adding tags
                            List<String> tagIds = new ArrayList<>();

                            // setting cruise type
                            if (cruise.getIsExpedition() != null && cruise.getIsExpedition()) {
                                tagIds.add("cruise-type:silversea-expedition");
                            } else {
                                tagIds.add("cruise-type:silversea-cruise");
                            }

                            // setting features
                            for (int featureId : cruise.getFeatures()) {
                                if (featuresMapping.containsKey(featureId)) {
                                    tagIds.add(featuresMapping.get(featureId));
                                }
                            }

                            cruiseContentNode.setProperty("cq:tags", tagIds.toArray(new String[tagIds.size()]));

                            // setting ship reference
                            final String shipId = String.valueOf(cruise.getShipId());
                            LOGGER.trace("Cruise ship id {}", shipId);

                            if (shipsMapping.containsKey(shipId)) {
                                LOGGER.trace("Found ship {} in cache", shipId);

                                if (shipsMapping.get(shipId).containsKey(language)) {
                                    LOGGER.trace("Associating ship {} to cruise", shipsMapping.get(shipId).get(language));

                                    cruiseContentNode.setProperty("shipReference", shipsMapping.get(shipId).get(language));
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

                                cruiseContentNode.setProperty("cq:propertyInheritanceCancelled", new String[]{"jcr:title", "apiTitle"});
                            }

                            // download and associate map
                            final String mapUrl = cruise.getMapUrl();

                            if (mapUrl != null) {
                                try {
                                    final InputStream inputStream = new URL(mapUrl).openStream();
                                    final String filename = StringUtils.substringAfterLast(mapUrl, "/");

                                    final Asset asset = assetManager.createAsset("/content/dam/silversea-com/api-provided/cruises/"
                                                    + pageName + "/" + filename,
                                            inputStream, mimeTypeService.getMimeType(mapUrl), false);

                                    cruiseContentNode.setProperty("itinerary", asset.getPath());

                                    LOGGER.trace("Creating itinerary asset {}", asset.getPath());
                                } catch (IOException e) {
                                    LOGGER.error("Cannot import itinerary image {}", mapUrl);
                                }
                            }

                            successNumber++;
                            itemsWritten++;

                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} cruises imported, saving session", +itemsWritten);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }

                            if (size != -1 && itemsWritten >= size) {
                                break;
                            }
                        }
                    } catch (RepositoryException | WCMException | ImporterException e) {
                        LOGGER.error("Cannot write cruise {} ({})", cruise.getVoyageName(), cruise.getVoyageCod(), e);

                        errorNumber++;
                    }

                    if (size != -1 && itemsWritten >= size) {
                        break;
                    }
                }

                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (cruises.size() > 0);

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

        LOGGER.debug("Ending cruises import, success: {}, error: {}, api calls: {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        // TODO implement
        return null;
    }
}
