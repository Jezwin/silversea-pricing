package com.silversea.aem.services.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.services.ApiConfigurationService;

@Component(immediate = true, label = "Silversea.com - Api importer Configuration", metatype = true)
@Service(value = ApiConfigurationService.class)
public class ApiConfigurationServiceImp implements ApiConfigurationService {

    static final private Logger LOGGER = LoggerFactory.getLogger(ApiConfigurationServiceImp.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /** URL récupérer login swagger **/
    @Property(value = "https://shop.silversea.com", label = "Api Domain", description = "Api domain of swagger")
    private static String API_DOMAIN = "apiDomain";
    private String apiDomain;

    /** URL récupérer login swagger **/
    @Property(value = "auolivier@sqli.com", label = "login swagger", description = "login swagger")
    private static String LOGIN = "login";
    private String login;

    /** URL récupérer password swagger **/
    @Property(value = "123qweASD", label = "password swagger", description = "password swagger")
    private static String PASSWORD = "password";
    private String password;

    @Property(description = "SSC API Paths", value = { "shipUrl:/api/v1/ships", "brochureUrl:/api/v1/brochures",
            "featuresUrl:/api/v1/features", "contriesUrl:/api/v1/countries", "spetialOffersUrl:/api/v1/specialOffers",
            "landProgramUrl:/api/v1/landAdventures", "shorexUrl:/api/v1/shoreExcursions",
            "hotelUrl:/api/v1/shoreExcursions", "agenciesUrl:/api/v1/agencies", "citiesUrl:/apit/v1/cities", })
    private static final String API_PATH = "apiPath";
    private Map<String, String> api_path;

    @Property(description = "SSC API Root Paths", value = { "shipUrl:/content/silversea-com/en/ships",
            "brochureUrl:/content/dam/siversea-com/brochures", "featuresUrl:/content/silversea-com/en/features",
            "contriesUrl:/content/silversea-com/en/country",
            "spetialOffersUrl:/content/silversea-com/en/exclusive-offers",
            "agenciesUrl:/content/silversea-com/en/other-resources/find-a-travel-agent",
            "citiesUrl:/content/silversea-com/en/other-resources/find-a-port",
            "cruisesUrl:/content/silversea-com/en/destinations/", })
    private static final String API_ROOT_PATH = "apiRootPath";
    private Map<String, String> api_root_path;

    /** URL récupérer le nombre déelement a retourner par l'api **/
    @Property(value = "100", label = "page size", description = "page size (number of element returned by the API)")
    private static String PAGE_SIZE = "pageSize";
    private int pageSize;

    /** URL récupérer la session refresh **/
    @Property(value = "100", label = "Session refresh", description = "Number of element , before we refresh se session save")
    private static String SESSION_REFRESH = "sessionRefresh";
    private int sessionRefresh;

    /** URL récupérer le timeout **/
    @Property(value = "3000", label = "Timeout", description = "Timeout")
    private static String TIME_OUT = "timeout";
    private int timeout;

    /**
     * Methode activate permettant de récupérer les valeurs des propriétés
     * 
     * @param compContext
     */
    @Activate
    @Modified
    protected void activate(ComponentContext compContext) {

        LOGGER.debug("Activation service configuration");

        // Récupération des propriétés
        @SuppressWarnings("unchecked")
        Dictionary<String, String> properties = compContext.getProperties();

        // recuperation du login password du swagger
        login = PropertiesUtil.toString(properties.get(LOGIN), "auolivier@sqli.com");
        password = PropertiesUtil.toString(properties.get(PASSWORD), "123qweASD");

        apiDomain = PropertiesUtil.toString(properties.get(API_DOMAIN), "https://shop.silversea.com");

        api_path = parseToMap(PropertiesUtil.toStringArray(properties.get(API_PATH), new String[] {}));

        api_root_path = parseToMap(PropertiesUtil.toStringArray(properties.get(API_ROOT_PATH), new String[] {}));

        pageSize = PropertiesUtil.toInteger(properties.get(PAGE_SIZE), 100);
        sessionRefresh = PropertiesUtil.toInteger(properties.get(SESSION_REFRESH), 100);
        timeout = PropertiesUtil.toInteger(properties.get(TIME_OUT), 100);

    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String apiUrlConfiguration(String api) {
        return api_path.get(api);
    }

    @Override
    public String apiRootPath(String api) {
        return api_root_path.get(api);
    }

    @Override
    public String getApiBaseDomain() {
        return apiDomain;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getSessionRefresh() {
        return sessionRefresh;
    }

    public int getTimeout() {
        return timeout;
    }

    private Map<String, String> parseToMap(String[] apiPaths) {
        Map<String, String> result = new HashMap<String, String>();

        for (String apiPath : apiPaths) {
            String[] keyValue = apiPath.split(":");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

}
