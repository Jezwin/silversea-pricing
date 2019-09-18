package com.silversea.aem.importers.utils;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.swagger.client.StringUtil;
import org.apache.jackrabbit.vault.util.SHA1;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ImportersUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImportersUtils.class);

    /**
     * @param pageManager
     * @return
     */
    public static List<String> getSiteLocales(final PageManager pageManager) {
        final Page rootPage = pageManager.getPage(WcmConstants.PATH_SILVERSEA_COM);
        List<String> locales = new ArrayList<>();

        if (rootPage != null) {
            final Iterator<Page> localePages = rootPage.listChildren();

            while (localePages.hasNext()) {
                final Page localePage = localePages.next();

                LOGGER.trace("Adding {} in locales list", localePage.getName());

                locales.add(localePage.getName());
            }
        }

        return locales;
    }

    /**
     * @param pageManager
     * @param masterPage
     * @param locale
     * @return
     */
    public static Page getPagePathByLocale(final PageManager pageManager, final Page masterPage, final String locale) {
        if (pageManager != null && masterPage != null && locale != null) {

            final String localePagePath = StringUtils.replace(masterPage.getPath(), "/en/", "/" + locale + "/");

            if (localePagePath != null) {
                return pageManager.getPage(localePagePath);
            }
        }

        return null;
    }

    /**
     * Use {@link #setLastModificationDate(Session, String, String, boolean)} instead
     * <p>
     * Set the last modification date on the defined <code>rootPath</code>
     *
     * @param pageManager  the page manager
     * @param session      the session
     * @param rootPath     path of the page where to set the last modification date property
     * @param propertyName the property name to write
     */
    @Deprecated
    public static void setLastModificationDate(PageManager pageManager, Session session,
                                               final String rootPath, final String propertyName) {
        // Setting modification date for each language
        final Page rootPage = pageManager.getPage(rootPath);

        // Setting last modification date
        try {
            Node rootNode = rootPage.getContentResource().adaptTo(Node.class);

            if (rootNode != null) {
                rootNode.setProperty(propertyName, Calendar.getInstance());

                session.save();
            } else {
                LOGGER.error("Cannot set {} on {}", propertyName, rootPath);
            }
        } catch (RepositoryException e) {
            LOGGER.error("Cannot set last modification date", e);

            try {
                session.refresh(false);
            } catch (RepositoryException e1) {
                LOGGER.debug("Cannot refresh session", e1);
            }
        }
    }

    /**
     * Set the last modification date on the defined <code>rootPath</code>
     *
     * @param session      the session
     * @param rootPath     path of the page where to set the last modification date property
     * @param propertyName the property name to write
     * @param autoSave     save the session if true
     * @throws RepositoryException
     */
    public static void setLastModificationDate(final Session session, final String rootPath, final String
            propertyName, final boolean autoSave) throws RepositoryException {
        Node rootNode = session.getNode(rootPath + "/" + JcrConstants.JCR_CONTENT);
        rootNode.setProperty(propertyName, Calendar.getInstance());

        if (autoSave && session.hasPendingChanges()) {
            session.save();
        }
    }

    /**
     * @param apiConfigurationService the api configuration
     * @return a configured API client
     */
    public static ApiClient getApiClient(ApiConfigurationService apiConfigurationService) {
        ApiClient apiClient = new ApiClient();

        final DigestAuthenticator authenticator = new DigestAuthenticator(
                new Credentials(apiConfigurationService.getLogin(), apiConfigurationService.getPassword()));
        final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();

        apiClient.setDebugging(LOGGER.isDebugEnabled());

        apiClient.setBasePath(apiConfigurationService.apiBasePath());

        // TODO connect and read timeout are not supposed to be the same !!
        apiClient.setConnectTimeout(apiConfigurationService.getTimeout());
        apiClient.getHttpClient().setReadTimeout(apiConfigurationService.getTimeout(), TimeUnit.MILLISECONDS);

        apiClient.getHttpClient().interceptors().add(new AuthenticationCacheInterceptor(authCache));
        apiClient.getHttpClient().setAuthenticator(new CachingAuthenticatorDecorator(authenticator, authCache));

        return apiClient;
    }

    /**
     * TODO javadoc
     *
     * @param resourceResolver
     * @param sessionRefresh
     * @param query
     * @return
     * @throws RepositoryException
     */
    public static int deleteResources(final ResourceResolver resourceResolver, final int sessionRefresh,
                                      final String query) throws RepositoryException {
        final Session session = resourceResolver.adaptTo(Session.class);

        if (session == null) {
            throw new RepositoryException("Cannot adapt resource resolver into session");
        }

        // Existing excursions deletion
        LOGGER.debug("Cleaning already imported items");

        final Iterator<Resource> existingResources = resourceResolver.findResources(query, "xpath");

        int i = 0;
        while (existingResources.hasNext()) {
            final Resource resource = existingResources.next();
            final Node node = resource.adaptTo(Node.class);

            if (node != null) {
                try {
                    node.remove();

                    i++;
                } catch (RepositoryException e) {
                    LOGGER.error("Cannot remove existing item {}", resource.getPath(), e);
                }
            }

            if (i % sessionRefresh == 0 && session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} items cleaned, saving session", +i);
                } catch (RepositoryException e) {
                    session.refresh(true);
                }
            }
        }

        if (session.hasPendingChanges()) {
            try {
                session.save();

                LOGGER.debug("{} items cleaned, saving session", +i);
            } catch (RepositoryException e) {
                session.refresh(false);
            }
        }

        return i;
    }

    /**
     * TODO javadoc TODO generify
     *
     * @param resourceResolver
     * @return
     */
    public static List<ItineraryModel> getItineraries(ResourceResolver resourceResolver) {
        final Iterator<Resource> itinerariesForMapping = resourceResolver.findResources(
                "/jcr:root/content/silversea-com//element(*,nt:unstructured)" +
                        "[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary\"]",
                "xpath");

        final List<ItineraryModel> itinerariesMapping = new ArrayList<>();
        while (itinerariesForMapping.hasNext()) {
            final Resource itinerary = itinerariesForMapping.next();
            final ItineraryModel itineraryModel = itinerary.adaptTo(ItineraryModel.class);

            if (itineraryModel != null) {
                itinerariesMapping.add(itineraryModel);

                LOGGER.trace("Adding itinerary {} (cruise id : {}, port id : {}) to cache", itinerary.getPath(),
                        itineraryModel.getCruiseId(), itineraryModel.getPortId());
            }
        }
        return itinerariesMapping;
    }

    /**
     * Build a Map with : <ul> <li>id of the element</li> <li>lang</li> <li>path of the element (page)</li> </ul>
     *
     * @param resourceResolver the resource resolver
     * @param query            xpath query searching for cq:PageContent items
     * @param propertyId       property name of the element id
     * @return items mapping
     */
    public static Map<Integer, Map<String, String>> getItemsMapping(final ResourceResolver resourceResolver,
                                                                    final String query, final String propertyId) {
        final Iterator<Resource> itemsForMapping = resourceResolver.findResources(query, "xpath");

        final Map<Integer, Map<String, String>> itemsMapping = new HashMap<>();

        while (itemsForMapping.hasNext()) {
            final Resource item = itemsForMapping.next();

            final Page itemPage = item.getParent().adaptTo(Page.class);
            final String language = LanguageHelper.getLanguage(itemPage);

            final Integer itemId = item.getValueMap().get(propertyId, Integer.class);

            if (itemId != null) {
                if (itemsMapping.containsKey(itemId)) {
                    itemsMapping.get(itemId).put(language, itemPage.getPath());
                } else {
                    final HashMap<String, String> itemsPaths = new HashMap<>();
                    itemsPaths.put(language, itemPage.getPath());
                    itemsMapping.put(itemId, itemsPaths);
                }

                LOGGER.trace("Adding item {} ({}) with lang {} to cache", item.getPath(), itemId, language);
            }
        }

        return itemsMapping;
    }

    public static Map<String, Map<String, String>> getItemsMapping(final ResourceResolver resourceResolver,
                                                                    final String query, final String propertyId, final String propertyId2) {
        final Iterator<Resource> itemsForMapping = resourceResolver.findResources(query, "xpath");

        final Map<String, Map<String, String>> itemsMapping = new HashMap<>();

        while (itemsForMapping.hasNext()) {
            final Resource item = itemsForMapping.next();

            final Page itemPage = item.getParent().adaptTo(Page.class);
            final String language = LanguageHelper.getLanguage(itemPage);

            final Integer itemId = item.getValueMap().get(propertyId , Integer.class);
            final Integer itemId2 = item.getValueMap().get(propertyId2, Integer.class);

            if (itemId != null && itemId2 != null) {
                if (itemsMapping.containsKey(itemId + "-" + itemId2)) {
                    itemsMapping.get(itemId + "-" + itemId2).put(language, itemPage.getPath());
                } else {
                    final HashMap<String, String> itemsPaths = new HashMap<>();
                    itemsPaths.put(language, itemPage.getPath());
                    itemsMapping.put(itemId + "-" + itemId2, itemsPaths);
                }

                LOGGER.trace("Adding item {} ({}) with lang {} to cache", item.getPath(), itemId, language);
            }
        }

        return itemsMapping;
    }
    /**
     * Build a Map with : <ul> <li>id of the element</li> <li>lang</li> <li>page</li> </ul>
     *
     * @param resourceResolver the resource resolver
     * @param query            xpath query searching for cq:PageContent items
     * @param propertyId       property name of the element id
     * @return items mapping
     */
    public static Map<Integer, Map<String, Page>> getItemsPageMapping(final ResourceResolver resourceResolver,
                                                                      final String query, final String propertyId) {
        final Iterator<Resource> itemsForMapping = resourceResolver.findResources(query, "xpath");

        final Map<Integer, Map<String, Page>> itemsMapping = new HashMap<>();

        while (itemsForMapping.hasNext()) {
            final Resource item = itemsForMapping.next();

            final Page itemPage = item.getParent().adaptTo(Page.class);
            final String language = LanguageHelper.getLanguage(itemPage);

            final Integer itemId = item.getValueMap().get(propertyId, Integer.class);

            if (itemId != null) {
                if (itemsMapping.containsKey(itemId)) {
                    itemsMapping.get(itemId).put(language, itemPage);
                } else {
                    final HashMap<String, Page> itemsPaths = new HashMap<>();
                    itemsPaths.put(language, itemPage);
                    itemsMapping.put(itemId, itemsPaths);
                }

                LOGGER.trace("Adding item {} ({}) with lang {} to cache", item.getPath(), itemId, language);
            }
        }

        return itemsMapping;
    }

    /**
     * @param page         the page from where to get the date
     * @param propertyName name of the date property to retrieve
     * @return simplified date from property defined by <code>propertyName</code>
     */
    public static String getDateFromPageProperties(final Page page, final String propertyName) {
        if (page != null) {
            Resource pageContentResource = page.getContentResource();

            if (pageContentResource != null) {
                Date date = pageContentResource.getValueMap().get(propertyName, Date.class);

                if (date != null) {
                    return new SimpleDateFormat("yyyyMMdd").format(date);
                }
            }
        }

        return null;
    }

    /**
     * Create a new asset if not already existent.
     *
     * @param resourceResolver The resource resolver.
     * @param mimeTypeService  The mime type service.
     * @param assetUrl         The URL of the asset to be saved.
     * @param destinationPath  The destination for the new asset. The filename will be retrieved from the assetUrl and
     *                         appended.
     * @return The created or already existing asset.
     */
    public static String upsertAsset(Session session, ResourceResolver resourceResolver,
                                     MimeTypeService mimeTypeService, String assetUrl,
                                     String destinationPath) {
        if(StringUtils.isEmpty(assetUrl)){
            return "";
        }
        assetUrl = assetUrl.replace("http://","https://");
        final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
        String fileName = assetUrl.substring(assetUrl.lastIndexOf("/") + 1);
        if(destinationPath.lastIndexOf("/") +1 == destinationPath.length()){
            destinationPath = destinationPath.substring(0,destinationPath.length() -1);
        }

        String finalDestination = destinationPath + "/" + fileName;
        try {
            String assetUrlFileNameEncoded = encodeFileNameAssetUrl(assetUrl,fileName);
            if (!session.itemExists(finalDestination) ||
                    isResourceToBeUpdated(resourceResolver, finalDestination, new URL(assetUrlFileNameEncoded))) {
                LOGGER.info("Creating itinerary asset {}", finalDestination);
                try (InputStream mapStream = new URL(assetUrlFileNameEncoded).openStream()) {
                    final Asset asset = assetManager.createAsset(finalDestination, mapStream,
                            mimeTypeService.getMimeType(assetUrl), false);
                    LOGGER.info("Creating {} SAVED.", finalDestination);
                    // setting to activate flag on asset
                    final Node assetNode = asset.adaptTo(Node.class);
                    if (assetNode != null && assetNode.hasNode(JcrConstants.JCR_CONTENT)) {
                        final Node assetContentNode = assetNode.getNode(JcrConstants.JCR_CONTENT);
                        if (assetContentNode != null) {
                            assetContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
                        }
                    }
                }
                return finalDestination;

            }
        } catch (RepositoryException | IOException e  ) {
            LOGGER.error("Error during creation of {}", assetUrl);
            LOGGER.error("Error during creation of {}", e.getMessage());
        }
        return finalDestination;

    }


    public static boolean isResourceToBeUpdated(ResourceResolver resourceResolver, String existingResource,
                                                URL remoteUrl) {
        Resource existingAsset = resourceResolver.getResource(existingResource);
        if(!existingAsset.getChild("jcr:content").adaptTo(ValueMap.class).get("dam:assetState", String.class).equals("processed")){
            return false;
        }
        String existingSha1 =
                existingAsset.getChild("jcr:content/metadata").adaptTo(ValueMap.class).get("dam:sha1", String.class);
        try (InputStream is = remoteUrl.openStream()) {
            if (existingSha1 != null) {
                SHA1 newSha1 = SHA1.digest(is);
                String newChecksum = newSha1.toString();
                LOGGER.debug("COMPARISON SHA1 : " +newChecksum + " OLD " + existingSha1);
                return !existingSha1.equals(newChecksum);
            }
        } catch (IOException e) {
            LOGGER.error("Error comparing {} with {}", existingResource, remoteUrl);
        }
        return true;

    }

    /***
     * Encode the filename of the assetUrl
     * (avoid exceptions during URL.openStream())
     *
     * @param assetUrl
     * @param filename
     * @return
     * @throws UnsupportedEncodingException (subclass of IOException)
     */
    private static String encodeFileNameAssetUrl(String assetUrl, String filename) throws UnsupportedEncodingException {

        String assetUrlFileNameEncoded = "";

        if(StringUtils.isNotEmpty(assetUrl) && StringUtils.isNotEmpty(filename)) {
            //asseturl without filename
            assetUrlFileNameEncoded = assetUrl.substring(0, assetUrl.lastIndexOf("/") + 1);
            //Encode filename
            String fileNameEncoded = URLEncoder.encode(filename, "UTF-8");

            assetUrlFileNameEncoded = assetUrlFileNameEncoded + fileNameEncoded;
        }

        return assetUrlFileNameEncoded;
    }
}
