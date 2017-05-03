package com.silversea.aem.services.impl;

import java.io.IOException;
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

import com.silversea.aem.importers.services.impl.BaseImporter;
import com.silversea.aem.services.ApiConfigurationService;

@Component(immediate = true, label = "Silversea.com - Api importer Configuration", metatype = true)
@Service(value = ApiConfigurationService.class)
public class ApiConfigurationServiceImp extends BaseImporter implements ApiConfigurationService {

    static final private Logger LOGGER = LoggerFactory.getLogger(ApiConfigurationServiceImp.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

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
            "landProgramUrl:/api/v1/landAdventures", "shorexUrl:/api/v1/shoreExcursions","hotelUrl:/api/v1/shoreExcursions", "agenciesUrl:/api/v1/agencies", "citiesUrl:/api/v1/cities",})
    private static final String API_PATH = "apiPath";
    private Map<String, String> api_path;

//    /** URL récupérer l url de l API.SHIP **/
//    @Property(value = "/api/v1/ships", label = "Ship API Url", description = "path to the ship api")
//    private static String SHIP_URL = "shipUrl";
//    private String shipUrl;
//
//    /** URL récupérer l url de la brochure API. **/
//    @Property(value = "/api/v1/brochures", label = "Brochures Api Path", description = "path to the Brochure api")
//    private static String BROCHURE_API_URL = "brochureUrl";
//    private String brochureUrl;
//
//    /** URL récupérer l url de contires API. **/
//    @Property(value = "/api/v1/countries", label = "Contries Api Path", description = "path to the Contries api")
//    private static String COUNTRY_PATH = "contriesUrl";
//    private String contriesUrl;
//
//    /** URL récupérer l url de la brochure API. **/
//    @Property(value = "/api/v1/specialOffers", label = "Special Offers Api Path", description = "path to the Specials Offers api")
//    private static String SPECIAL_OFFERS_PATH = "spetialOffersUrl";
//    private String spetialOffersUrl;
//
//    /** URL récupérer l url de land program API. **/
//    @Property(value = "/api/v1/landAdventures", label = "Land Program Api Path", description = "path to the Land program api")
//    private static String LAND_PROGRAM_PATH = "landProgramUrl";
//    private String landProgramUrl;
//
//    /** URL récupérer l url de shorx API. **/
//    @Property(value = "/api/v1/shoreExcursions", label = "Shorex Api Path", description = "path to the shorex api")
//    private static String SHOREX_PATH = "shorexUrl";
//    private String shorexUrl;
//
//    /** URL récupérer l url de shorx API. **/
//    @Property(value = "/api/v1/agencies", label = "agencies Api Path", description = "path to the agencies api")
//    private static String AGENCIES_PATH = "agenciesUrl";
//    private String agenciesUrl;
//
//    /** URL récupérer l url de shorx API. **/
//    @Property(value = "/api/v1/cities", label = "cities Api Path", description = "path to the cities api")
//    private static String CITIES_PATH = "citiesUrl";
//    private String citiesUrl;

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

        // Récupération de la propriété ship url
//        shipUrl = PropertiesUtil.toString(properties.get(SHIP_URL), "/api/v1/brochures");
//        brochureUrl = PropertiesUtil.toString(properties.get(SHIP_URL), "/api/v1/ships");
//        contriesUrl = PropertiesUtil.toString(properties.get(SHIP_URL), "/api/v1/countries");
//        spetialOffersUrl = PropertiesUtil.toString(properties.get(SHIP_URL), "/api/v1/specialOffers");
//        landProgramUrl = PropertiesUtil.toString(properties.get(SHIP_URL), "/api/v1/landAdventures");
//        shorexUrl = PropertiesUtil.toString(properties.get(SHIP_URL), "/api/v1/shoreExcursions");
//        agenciesUrl = PropertiesUtil.toString(properties.get(SHIP_URL), "/api/v1/agencies");

        api_path = parseToMap(PropertiesUtil.toStringArray(properties.get(API_PATH), new String[] {}));

    }

    @Override
    public String apiUrlConfiguration(String api) {
        return api_path.get(api);
    }

    public void passwordSwagger() {

    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getApi_path() {
        return api_path;
    }

//    public String getShipUrl() {
//        return shipUrl;
//    }
//
//    public String getBrochureUrl() {
//        return brochureUrl;
//    }
//
//    public String getContriesUrl() {
//        return contriesUrl;
//    }
//
//    public String getSpetialOffersUrl() {
//        return spetialOffersUrl;
//    }
//
//    public String getLandProgramUrl() {
//        return landProgramUrl;
//    }
//
//    public String getShorexUrl() {
//        return shorexUrl;
//    }
//
//    public String getAgenciesUrl() {
//        return agenciesUrl;
//    }
//
//    public String getCitiesUrl() {
//        return citiesUrl;
//    }

    private Map<String, String> parseToMap(String[] apiPaths) {
        Map<String, String> result = new HashMap<String, String>();

        for (String apiPath : apiPaths) {
            String[] keyValue = apiPath.split(":");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }
}
