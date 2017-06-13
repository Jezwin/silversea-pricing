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

import com.silversea.aem.services.ApiConfigurationService;

@Component(immediate = true, label = "Silversea.com - Api importer Configuration", metatype = true)
@Service(value = ApiConfigurationService.class)
public class ApiConfigurationServiceImp implements ApiConfigurationService {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;


    private static final String DEFAULT_API_DOMAIN = "https://shop.silversea.com";
    private static final String DEFAULT_LOGIN ="auolivier@sqli.com";
    private static final String DEFAULT_PASSWORD = "123qweASD";


    @Property(value = DEFAULT_API_DOMAIN, label = "Api Domain", description = "Api domain of swagger")
    private static final String API_DOMAIN = "apiDomain";
    private String apiDomain;


    @Property(value = DEFAULT_LOGIN, label = "login swagger", description = "login swagger")
    private static final String LOGIN = "login";
    private String login;


    @Property(value = DEFAULT_PASSWORD, label = "password swagger", description = "password swagger")
    private static final String PASSWORD = "password";
    private String password;

    @Property(description = "SSC API Paths", value = { "shipUrl:/api/v1/ships", "brochureUrl:/api/v1/brochures",
            "featuresUrl:/api/v1/features", "contriesUrl:/api/v1/countries", "spetialOffersUrl:/api/v1/specialOffers",
            "landProgramUrl:/api/v1/landAdventures", "shorexUrl:/api/v1/shoreExcursions",
            "voyageUrl:/api/v1/voyages","landAdventuresUrl:/api/v1/landAdventures/Itinerary","voyageSpecialOffersUrl:/api/v1/voyageSpecialOffers",
            "specialVoyagesUrl:/api/v1/specialVoyages",
            "itineraryHotelsUrl:/api/v1/hotels/Itinerary","itineraryExcursionsUrl:/api/v1/shoreExcursions/Itinerary","pricesUrl:/api/v1/prices",
            "hotelUrl:/api/v1/shoreExcursions", "agenciesUrl:/api/v1/agencies", "citiesUrl:/api/v1/cities", })
    private static final String API_PATH = "apiPath";
    private Map<String, String> apiPath;

    @Property(description = "SSC API Root Paths", value = { "shipUrl:/content/silversea-com/en/ships",
            "brochureUrl:/content/dam/siversea-com/brochures", "featuresUrl:/content/silversea-com/en/features",
            "contriesUrl:/content/silversea-com/en/country",
            "spetialOffersUrl:/content/silversea-com/en/exclusive-offers",
            "agenciesUrl:/content/silversea-com/en/other-resources/find-a-travel-agent",
            "citiesUrl:/content/silversea-com/en/other-resources/find-a-port",
            "cruisesUrl:/content/silversea-com/en/destinations/", })
    private static final String API_ROOT_PATH = "apiRootPath";
    private Map<String, String> apiRootPath;

    @Property(value = "100", label = "page size", description = "page size (number of element returned by the API)")
    private static final String PAGE_SIZE = "pageSize";
    private int pageSize;

    @Property(value = "100", label = "Session refresh", description = "Number of element , before we refresh se session save")
    private static final String SESSION_REFRESH = "sessionRefresh";
    private int sessionRefresh;

    @Property(value = "3000", label = "Timeout", description = "Timeout")
    private static final String TIME_OUT = "timeout";
    private int timeout;


    @Activate
    @Modified
    protected void activate(final ComponentContext context) {

        Dictionary<?, ?> properties = context.getProperties();

        login = PropertiesUtil.toString(properties.get(LOGIN), DEFAULT_LOGIN);
        password = PropertiesUtil.toString(properties.get(PASSWORD), DEFAULT_PASSWORD);
        apiDomain = PropertiesUtil.toString(properties.get(API_DOMAIN), DEFAULT_API_DOMAIN);
        apiPath = parseToMap(PropertiesUtil.toStringArray(properties.get(API_PATH), new String[] {}));
        apiRootPath = parseToMap(PropertiesUtil.toStringArray(properties.get(API_ROOT_PATH), new String[] {}));
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
        return apiPath.get(api);
    }

    @Override
    public String apiRootPath(String api) {
        return apiRootPath.get(api);
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
