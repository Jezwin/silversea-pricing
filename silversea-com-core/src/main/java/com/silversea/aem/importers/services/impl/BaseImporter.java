package com.silversea.aem.importers.services.impl;

import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.authentication.DigestAuthenticationInfos;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by aurelienolivier on 13/02/2017.
 */
public class BaseImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(BaseImporter.class);

    protected String getAuthorizationHeader(final String path) throws IOException {
        // Get server data used to generate digest authentication
        HttpClient client = new HttpClient();

        GetMethod get = new GetMethod(ImportersConstants.API_DOMAIN + path);
        client.executeMethod(get);

        Header wwwAuthenticateHeader = get.getResponseHeader("WWW-Authenticate");

        if (wwwAuthenticateHeader != null) {
            DigestAuthenticationInfos digestAuthenticationInfos = new DigestAuthenticationInfos(wwwAuthenticateHeader.getValue());
            digestAuthenticationInfos.setCredentials("auolivier@sqli.com", "123qweASD");
            digestAuthenticationInfos.setUri(path);

            final String authorizationHeader = digestAuthenticationInfos.getHeaderValue();

            LOGGER.error("Authorization header : {}", authorizationHeader);

            return authorizationHeader;
        }

        return null;
    }
}
