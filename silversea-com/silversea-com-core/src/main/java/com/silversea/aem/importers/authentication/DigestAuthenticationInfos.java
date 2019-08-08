package com.silversea.aem.importers.authentication;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author aurelienolivier
 */
public class DigestAuthenticationInfos {

    static final private Logger LOGGER = LoggerFactory.getLogger(DigestAuthenticationInfos.class);

    private String realm;

    private String qop;

    private String nonce;

    private String username;

    private String password;

    private String uri;

    private int nc = 0;

    private String cnonce;

    /**
     * Creating authentication infos from initial http call
     * to the API
     *
     * example :
     * WWW-Authenticate: Digest realm="testrealm@host.com",
     *      qop="auth",
     *      nonce="dcd98b7102dd2f0e8b11d0f600bfb0c093",
     *      opaque="5ccc069c403ebaf9f0171e9517f40e41"
     *
     * @param headerValue
     */
    public DigestAuthenticationInfos(final String headerValue) throws IllegalArgumentException {
        final String digestInfos = headerValue.substring("Digest ".length()).trim();

        LOGGER.debug("digestInfos : {}", digestInfos);

        final String[] digestInfosArray = digestInfos.split(", ");

        for (String digestInfo : digestInfosArray) {
            if (digestInfo.startsWith("realm=")) {
                realm = digestInfo.substring("realm=".length()).replace("\"","");
            }

            if (digestInfo.startsWith("qop=")) {
                qop = digestInfo.substring("qop=".length()).replace("\"","");
            }

            if (digestInfo.startsWith("nonce=")) {
                nonce = digestInfo.substring("nonce=".length()).replace("\"","");
            }
        }

        if (realm == null || qop == null || nonce == null) {
            throw new IllegalArgumentException("Not a valid WWW-authenticate header");
        }
    }

    /**
     * Get Authorization header value containing digest infos
     *
     * Example :
     * Authorization: Digest username="auolivier@sqli.com",
     *      realm="shop.silversea.com",
     *      nonce="NjM2MjA2MzkxNDI0MjUuNDo5YTE1YmU4M2U0ZmEwYmY0MGNjNzhjM2YxYTJjMjI0Ng==",
     *      uri="/api/v1/cities",
     *      response="9b931e1b215726de616b805a3aac7dfa",
     *      qop=auth,
     *      nc=00000001,
     *      cnonce="36c544059b723fc0"
     *
     * @return valid Digest Authorization header
     */
    public String getHeaderValue() {
        StringBuilder headerValue = new StringBuilder();

        nc++;
        cnonce = RandomStringUtils.randomAlphabetic(16);

        // Using following algorithm to generate response
        // TODO deal with other cases if necessary : https://en.wikipedia.org/wiki/Digest_access_authentication
        // HA1=MD5(username:realm:password)
        // HA2=MD5(method:digestURI)
        // response=MD5(HA1:nonce:nonceCount:cnonce:qop:HA2)

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");

            byte[] HA1 = md.digest((username + ":" + realm + ":" + password).getBytes("UTF-8"));
            byte[] HA2 = md.digest(("GET:" + uri).getBytes("UTF-8"));

            LOGGER.debug("Generated HA1: {} and HA2: {}", Hex.encodeHexString(HA1), Hex.encodeHexString(HA2));

            StringBuilder sb = new StringBuilder();
            sb.append(Hex.encodeHexString(HA1)).append(':')
                    .append(nonce).append(':')
                    .append(String.format("%08d", nc)).append(':')
                    .append(cnonce).append(':')
                    .append(qop).append(':')
                    .append(Hex.encodeHexString(HA2));
            byte[] responseBytes = md.digest(sb.toString().getBytes("UTF-8"));

            headerValue.append("Digest ")
                    .append("username=\"").append(username).append("\", ")
                    .append("realm=\"").append(realm).append("\", ")
                    .append("nonce=\"").append(nonce).append("\", ")
                    .append("uri=\"").append(uri).append("\", ")
                    .append("response=\"").append(Hex.encodeHexString(responseBytes)).append("\", ")
                    .append("qop=").append(qop).append(", ")
                    .append("nc=").append(String.format("%08d", nc)).append(", ")
                    .append("cnonce=\"").append(cnonce).append("\"");

            LOGGER.debug("Generated Authorization header : {}", headerValue);

            return headerValue.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.error("Cannot generate Authorization header", e);
        }

        return null;
    }

    /**
     * Set the username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * set the password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Helper method to set both username and password
     * @param username
     * @param password
     */
    public void setCredentials(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Set the uri
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
}
