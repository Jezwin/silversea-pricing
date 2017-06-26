package com.silversea.aem.importers;

/**
 * @author aurelienolivier
 */
public interface ImportersConstants {

    String SILVERSEA_ROOT = "/content/silversea-com";

    String BASEPATH_PORTS = "/content/silversea-com/en/other-resources/find-a-port";

    String BASEPATH_TRAVEL_AGENCIES = "/content/silversea-com/en/other-resources/find-a-travel-agent";

    String BASEPATH_SPECIAL_OFFERS = "/content/silversea-com/en/exclusive-offers";

    String BASEPATH_SHIP = "/content/silversea-com/en/ships";

    String BASEPATH_FEATURES = "/content/silversea-com/en/features";

    String BASEPATH_BROCHURES = "/content/dam/siversea-com/brochures";

    String BASEPATH_CRUISES = "/content/silversea-com/en/destinations/";

    // Just Add to test
    String BASEPATH_COUNTRY = "/content/silversea-com/en/country";

    String API_DOMAIN = "https://shop.silversea.com";

    //Sub service name
    String SUB_SERVICE_IMPORT_DATA = "import-data";

    // Templates
    String CRUISE_TEMPLATE = "/apps/silversea/silversea-com/templates/cruise";
    String CRUISE_SEGEMENT_TEMPLATE = "/apps/silversea/silversea-com/templates/combosegment";
    String COMBO_CRUISE_TEMPLATE = "/apps/silversea/silversea-com/templates/combocruise";

    //Resources type
    String COMBO_CRUISE_RESOURCE_TYPE = "silversea/silversea-com/components/pages/combocruise";

    //Sling resource type property
    String SLIN_RESOURCE_TYPE = "jcr:content/sling:resourceType";

    // Tags path
    String GEOTAGGING_TAG_PREFIX = "geotagging:";

    // Constants
    String PRICE_WAITLIST = "Waitlist:";

    String CRUISES_DAM_PATH = "/content/dam/silversea-com/api-provided/cruises/";
    String QUERY_JCR_ROOT_PATH = "/jcr:root";
    String QUERY_CONTENT_PATH = "/jcr:root/content/silversea-com/en";
    String QUERY_TAGS_PATH = "/jcr:root/etc/tags";

    String ITINERARIES_NODE = "itineraries";
    String SUITES_NODE = "suites";
    String LOWEST_PRICES_NODE = "lowest-prices";
    String CRUISES_DESTINATIONS_URL_KEY = "cruisesUrl";

    //API
    String VOYAGE_URL_KEY = "voyageUrl";
    String VOYAGE_SPECIAL_OFFERS_URL_KEY = "voyageSpecialOffersUrl";
    String VOYAGE_LAND_ADVENTURE_URL_KEY = "landAdventuresUrl";
    String VOYAGE_ITINERARY_HOTELS_URL_KEY = "itineraryHotelsUrl";
    String VOYAGE_ITINERARY_EXCURSIONS_URL_KEY = "itineraryExcursionsUrl";
    String VOYAGE_PRICES_URL_KEY = "pricesUrl";
    String SPECIAL_VOYAGE_URL_KEY = "specialVoyagesUrl";
}
