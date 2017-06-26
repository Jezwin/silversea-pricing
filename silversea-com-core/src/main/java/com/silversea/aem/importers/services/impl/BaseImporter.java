package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImporterUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.authentication.DigestAuthenticationInfos;
import com.silversea.aem.services.ApiConfigurationService;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Created by aurelienolivier on 13/02/2017.
 */

@Deprecated
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
            DigestAuthenticationInfos digestAuthenticationInfos = new DigestAuthenticationInfos(
                    wwwAuthenticateHeader.getValue());
            // TODO remove de if else
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
     * @param pageManager the page manager
     * @param session the session
     * @param rootPath path of the page where to set the last modification date property
     * @param propertyName the property name to write
     */
    @Deprecated
    protected void setLastModificationDate(PageManager pageManager, Session session,
                                           final String rootPath, final String propertyName) {
        // Setting modification date for each language
        final Page rootPage = pageManager.getPage(rootPath);
        final List<String> locales = ImporterUtils.getSiteLocales(pageManager);

        // Iterating over locales to import cities
        for (String locale : locales) {
            final Page citiesRootPage = ImporterUtils.getPagePathByLocale(pageManager, rootPage, locale);

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
}
