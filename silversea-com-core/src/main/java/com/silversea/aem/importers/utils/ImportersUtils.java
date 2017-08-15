package com.silversea.aem.importers.utils;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ImportersUtils {

    static final private Logger LOGGER = LoggerFactory.getLogger(ImporterUtils.class);

    /**
     * @param pageManager
     *
     * @return
     */
    public static List<String> getSiteLocales(final PageManager pageManager) {
        final Page rootPage = pageManager.getPage(ImportersConstants.SILVERSEA_ROOT);
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
     *
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
     * @param pageManager the page manager
     * @param session the session
     * @param rootPath path of the page where to set the last modification date property
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
     * @param session the session
     * @param rootPath path of the page where to set the last modification date property
     * @param propertyName the property name to write
     * @param autoSave save the session if true
     *
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
     *
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
     *
     * @return
     *
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
     *
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
     * @param query xpath query searching for cq:PageContent items
     * @param propertyId property name of the element id
     *
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

    /**
     * Build a Map with : <ul> <li>id of the element</li> <li>lang</li> <li>page</li> </ul>
     *
     * @param resourceResolver the resource resolver
     * @param query xpath query searching for cq:PageContent items
     * @param propertyId property name of the element id
     *
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
}