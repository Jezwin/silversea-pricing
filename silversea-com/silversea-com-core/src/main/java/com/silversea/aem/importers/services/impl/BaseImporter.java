package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.s7dam.set.MediaSet;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.jackrabbit.vault.util.SHA1;
import com.silversea.aem.importers.utils.ImportersUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.authentication.DigestAuthenticationInfos;

import static com.day.cq.dam.commons.util.S7SetHelper.createS7MixedMediaSet;

/**
 * Created by aurelienolivier on 13/02/2017.
 */
public class BaseImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(BaseImporter.class);

    private String login;
    private String password;
    private String apiDomain;

    protected String getAuthorizationHeader(final String path) throws IOException {
        // Get server data used to generate digest authentication
        HttpClient client = new HttpClient();
        GetMethod get;
        if (apiDomain != null) {
            get = new GetMethod(apiDomain + path);
        } else {
            get = new GetMethod(ImportersConstants.API_DOMAIN + path);
        }

        client.executeMethod(get);
        Header wwwAuthenticateHeader = get.getResponseHeader("WWW-Authenticate");

        if (wwwAuthenticateHeader != null) {
            DigestAuthenticationInfos digestAuthenticationInfos =
                    new DigestAuthenticationInfos(wwwAuthenticateHeader.getValue());
            if (login != null && password != null) {
                digestAuthenticationInfos.setCredentials(login, password);
            } else {
                digestAuthenticationInfos.setCredentials("auolivier@sqli.com", "123qweASD");
            }
            digestAuthenticationInfos.setUri(path);

            final String authorizationHeader = digestAuthenticationInfos.getHeaderValue();

            LOGGER.debug("Authorization header : {}", authorizationHeader);

            return authorizationHeader;
        }

        return null;
    }

    protected void getAuthentication(String log, String pass) {
        login = log;
        password = pass;
    }

    protected void getApiDomain(String domain) {
        apiDomain = domain;
    }

    /**
     * Set the last modification date on the defined <code>rootPath</code>
     *
     * @param pageManager  the page manager
     * @param session      the session
     * @param rootPath     path of the page where to set the last modification date property
     * @param propertyName the property name to write
     */
    @Deprecated
    protected void setLastModificationDate(PageManager pageManager, Session session,
                                           final String rootPath, final String propertyName) {
        // Setting modification date for each language
        final Page rootPage = pageManager.getPage(rootPath);
        final List<String> locales = ImportersUtils.getSiteLocales(pageManager);

        // Iterating over locales to import cities
        for (String locale : locales) {
            final Page citiesRootPage = ImportersUtils.getPagePathByLocale(pageManager, rootPage, locale);

            // Setting last modification date
            // after import
            try {
                Node rootNode = citiesRootPage.getContentResource().adaptTo(Node.class);

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
    }

    public static MediaSet createMediaSet(ResourceResolver resolver, Resource pathFolderResource,
                                          String setName, String... pathImages) throws PersistenceException,
            RepositoryException {
        MediaSet s7MixedMediaSet;
        if(pathFolderResource.getChild(setName) == null) {
            s7MixedMediaSet = createS7MixedMediaSet(pathFolderResource, setName, new HashMap<>());
            }else {
            s7MixedMediaSet = pathFolderResource.getChild(setName).adaptTo(MediaSet.class);
        }
            Asset asset;
            for (String pathImage : pathImages) {
                asset = pathImage != null && resolver.getResource(pathImage) != null ?
                        resolver.getResource(pathImage).adaptTo(Asset.class) : null;
                if (asset != null) {
                    if(!s7MixedMediaSet.contains(asset)) {
                        s7MixedMediaSet.add(asset);
                    }
                    final Resource setMetadata = s7MixedMediaSet.getChild("jcr:content/metadata");
                    if (setMetadata != null) {
                        final Node setMetadataNode = setMetadata.adaptTo(Node.class);

                        if (setMetadataNode != null) {
                            setMetadataNode.setProperty("dc:title", setName);
                        }
                    }

                }
            }
            return s7MixedMediaSet;


    }


}
