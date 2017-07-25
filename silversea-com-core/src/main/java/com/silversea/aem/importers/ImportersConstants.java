package com.silversea.aem.importers;

/**
 * TODO naming conventions
 */
public interface ImportersConstants {

    String SILVERSEA_ROOT = "/content/silversea-com";

    String API_DOMAIN = "https://shop.silversea.com";

    //Sub service name
    String SUB_SERVICE_IMPORT_DATA = "import-data";

    // Templates
    // TODO merge with com.silversea.aem.constants.TemplateConstants
    String CRUISE_TEMPLATE = "/apps/silversea/silversea-com/templates/cruise";
    String CRUISE_SEGEMENT_TEMPLATE = "/apps/silversea/silversea-com/templates/combosegment";

    //Resources type
    String COMBO_CRUISE_RESOURCE_TYPE = "silversea/silversea-com/components/pages/combocruise";

    //Sling resource type property
    String PN_SLING_RESOURCE_TYPE = "jcr:content/sling:resourceType";

    // Tags path
    String GEOTAGGING_TAG_PREFIX = "geotagging:";

    // Constants
    String PRICE_WAITLIST = "Waitlist:";

    String CRUISES_DAM_PATH = "/content/dam/silversea-com/api-provided/cruises/";
    String QUERY_JCR_ROOT_PATH = "/jcr:root";
    String QUERY_CONTENT_PATH = "/jcr:root/content/silversea-com/en";
    String QUERY_TAGS_PATH = "/jcr:root/etc/tags";
    String DURATIONS_TAGS_PATH= "/etc/tags/cruises-durations";
    String DATE_FORMAT_MMM_YYYY= "MMM yyyy";
    String TAG_DURATIONS_SEPARATOR = "-";

    String ITINERARIES_NODE = "itineraries";
    String SUITES_NODE = "suites";
    String LOWEST_PRICES_NODE = "lowest-prices";
    String CRUISES_DESTINATIONS_URL_KEY = "cruisesUrl";

    String LANGUAGE_EN = "en";

    // Property names
    String PN_TO_ACTIVATE = "toActivate";
    String PN_TO_DEACTIVATE = "toDeactivate";
}
