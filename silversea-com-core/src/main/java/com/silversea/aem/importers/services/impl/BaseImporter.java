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
             digestAuthenticationInfos.setCredentials("auolivier@sqli.com","123qweASD");
//            digestAuthenticationInfos.setCredentials(apiConf.getLogin(), apiConf.getPassword());
            digestAuthenticationInfos.setUri(path);

            final String authorizationHeader = digestAuthenticationInfos.getHeaderValue();

            LOGGER.error("Authorization header : {}", authorizationHeader);

            return authorizationHeader;
        }

        return null;
    }
}
