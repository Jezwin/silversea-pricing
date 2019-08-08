package com.silversea.aem.services.impl;

import com.silversea.aem.services.ApiConfigurationService;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, label = "Silversea - Api importer Configuration", metatype = true)
@Service(value = ApiConfigurationService.class)
public class ApiConfigurationServiceImpl implements ApiConfigurationService {

    static final private Logger LOGGER = LoggerFactory.getLogger(ApiConfigurationServiceImpl.class);

    private static final String DEFAULT_API_BASE_PATH = "http://shop.silversea.com/apit";
    private static final String DEFAULT_LOGIN = "auolivier@sqli.com";
    private static final String DEFAULT_PASSWORD = "123qweASD";

    @Property(value = DEFAULT_API_BASE_PATH, label = "API Base path", description = "API base path, Swagger format http://shop.silversea.com/apit")
    private static final String API_BASE_PATH = "apiBasePath";
    private String apiBasePath;

    @Property(value = DEFAULT_LOGIN, label = "login swagger", description = "login swagger")
    private static final String LOGIN = "login";
    private String login;

    @Property(value = DEFAULT_PASSWORD, label = "password swagger", description = "password swagger")
    private static final String PASSWORD = "password";
    private String password;

    // TODO review naming
    // TODO replace by constants
    @Property(description = "AEM content root path", value = {
            "shipUrl:/content/silversea-com/en/ships",
            "brochureUrl:/content/dam/silversea-com/brochures",
            "featuresUrl:/etc/tags/features",
            "countriesUrl:/content/silversea-com/en/country",
            "exclusiveOffersUrl:/content/silversea-com/en/exclusive-offers",
            "agenciesUrl:/content/silversea-com/en/other-resources/find-a-travel-agent",
            "citiesUrl:/content/silversea-com/en/other-resources/find-a-port",
            "cruisesUrl:/content/silversea-com/en/destinations"
    })
    private static final String API_ROOT_PATH = "apiRootPath";
    private Map<String, String> apiRootPath;

    @Property(value = "100", label = "page size", description = "Number of items per page from API")
    private static final String PAGE_SIZE = "pageSize";
    private int pageSize;

    @Property(value = "100", label = "Session refresh", description = "Number of transactions before saving session")
    private static final String SESSION_REFRESH = "sessionRefresh";
    private int sessionRefresh;

    @Property(value = "100000", label = "Timeout", description = "Timeout")
    private static final String TIME_OUT = "timeout";
    private int timeout;

    @Activate
    @Modified
    protected void activate(final ComponentContext context) {
        Dictionary<?, ?> properties = context.getProperties();

        login = PropertiesUtil.toString(properties.get(LOGIN), DEFAULT_LOGIN);
        password = PropertiesUtil.toString(properties.get(PASSWORD), DEFAULT_PASSWORD);
        apiBasePath = PropertiesUtil.toString(properties.get(API_BASE_PATH), DEFAULT_API_BASE_PATH);
        apiRootPath = parseToMap(PropertiesUtil.toStringArray(properties.get(API_ROOT_PATH), new String[]{}));
        pageSize = PropertiesUtil.toInteger(properties.get(PAGE_SIZE), 100);
        sessionRefresh = PropertiesUtil.toInteger(properties.get(SESSION_REFRESH), 100);
        timeout = PropertiesUtil.toInteger(properties.get(TIME_OUT), 100);
    }

    @Override
    public String apiBasePath() {
        return apiBasePath;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String apiRootPath(final String api) {
        return apiRootPath.get(api);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getSessionRefresh() {
        return sessionRefresh;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    /**
     * Parsing string array and generating map from it<br/>
     * Format is key:value
     *
     * @param apiPaths array of configurations
     * @return map of configurations
     */
    static private Map<String, String> parseToMap(String[] apiPaths) {
        Map<String, String> result = new HashMap<>();

        for (String apiPath : apiPaths) {
            LOGGER.trace("Parsing {}", apiPath);

            String[] keyValue = apiPath.split(":");
            result.put(keyValue[0], keyValue[1]);
        }

        return result;
    }
}
