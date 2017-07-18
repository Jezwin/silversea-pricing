package com.silversea.aem.constants;

public interface ServiceConstants {

    String VOYAGE_API_KEY = "voyagesApi";
    String HOTELS_API_KEY = "hotelsApi";
    String SHOREX_API_KEY = "shorexesApi";
    String PRICES_API_KEY = "pricesApi";
    String LAND_API_KEY = "landsApi";
    String SPECIAL_OFFERS_API_KEY = "voyageSpecialOffersApi";
    String SPECIAL_VOYAGES_API_KEY = "specialVoyagesApi";
    String CITIES_API_KEY = "citiesApi";
    String TRAVEL_AGENCIES_API_KEY = "travelAgenciesApi";
    String EXCLUSIVE_OFFERS_API_KEY = "exclusiveOffersApi";
    String BROCHURES_API_KEY = "brochuresApi";
    String FEATURES_API_KEY = "featuresApi";
    String SHIPS_API_KEY = "shipsApi";
    String COUNTRY_API_KEY = "contryApi";
    
    String SEARCH_CONTENT_ROOT= "/content/silversea-com/";
    String SEARCH_CRUISE_ROOT_PATH = "/destinations/";
    String SEARCH_PORT_ROOT_PATH = "/other-resources/find-a-port";
    String SEARCH_SHIP_ROOT_PATH ="/ships";
    
    String CRUISE_RESOURCE_TYPE= "silversea/silversea-com/components/pages/cruise";
    String DESTINATION_RESOURCE_TYPE = "silversea/silversea-com/components/pages/destination";
    String PORT_RESOURCE_TYPE = "silversea/silversea-com/components/pages/port";
    String SHIP_RESOURCE_TYPE= "silversea/silversea-com/components/pages/ship";
    
    String SLIN_RESOURCE_TYPE = "jcr:content/sling:resourceType";
    String SEARCH_PATH_PROPERTY ="path";
    String SEARCH_TYPE_PROPERTY = "type";
    //Computed data for search
    String SEARCH_CMP_DESTINATION = "jcr:content/cmp-destinationId";
    String SEARCH_CMP_SHIP = "jcr:content/cmp-ship";
    String SEARCH_CMP_DURATION = "jcr:content/cmp-duration";
    String SEARCH_CMP_CITIES = "jcr:content/cmp-cities";
    String SEARCH_START_DATE ="jcr:content/startDate";
    String SEARCH_CMP_DATE ="jcr:content/cmp-date";
    String SEARCH_CRUISE_DATE_FORMAT = "dd MMM YYYY";
    String DATE_FORMAT_MMM_YYYY= "MMM yyyy";
    String DATE_FORMAT_MMMM_YYYY= "MMMM yyyy";
    String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    String CRUISE_TYPE_TAGS_PREFIX="/etc/tags/cruise-type/";
    String FEATURE_TAGS_PREFIX="/etc/tags/features/";
    String DURATION_TAGS_PREFIX="/etc/tags/cruises-durations/";
    
    String SEARCH_DATE_UPPER_BOUND = "upperBound";
    String SEARCH_DATE_PREDICATE = "daterange";
    String SEARCH_TAGS_KEY = "tags";
    String SEARCH_TAG_ID_KEY = "tagid";
     
    
}
