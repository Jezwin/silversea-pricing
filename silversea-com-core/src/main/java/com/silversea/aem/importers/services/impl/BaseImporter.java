package com.silversea.aem.importers.services.impl;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.authentication.DigestAuthenticationInfos;
import com.silversea.aem.services.ApiConfigurationService;

/**
 * Created by aurelienolivier on 13/02/2017.
 */

public class BaseImporter {

    private String login;
    private String password;
    private String apiDomain;

    // public BaseImporter(String login, String password) {
    // super();
    // this.login = login;
    // this.password = password;
    // }

    @Reference
    private ApiConfigurationService apiConf;

    static final private Logger LOGGER = LoggerFactory.getLogger(BaseImporter.class);

    protected String getAuthorizationHeader(final String path) throws IOException {
        // Get server data used to generate digest authentication
        HttpClient client = new HttpClient();

        GetMethod get = new GetMethod(ImportersConstants.API_DOMAIN + path);
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

    protected void getAuthentification(String log, String pass) {
        login = log;
        password = pass;
    }

    protected void getApiDomain(String domain) {
        apiDomain = domain;
    }

}
