package com.silversea.aem.constants;

/**
 * TODO clean and check if constants already exists
 * Before creating constant, check first this following page
 * https://docs.adobe.com/docs/en/aem/6-2/develop/ref/javadoc/constant-values.html
 *
 * @author mjedli
 */
public interface WcmConstants {

    // Node names
    String NN_DEFAULT = "default";
    String NN_PHONE = "phone";

    String NN_LAND_PROGRAMS = "land-programs";
    String NN_HOTELS = "hotels";
    String NN_EXCURSIONS = "excursions";

    // Properties names
    String PN_NOT_IN_SITEMAP = "notInSitemap";
    String PN_FILE_REFERENCE = "fileReference";
    String PN_REFERENCE_PAGE_MAIN_NAVIGATION_BOTTOM = "referencePageMainNavigationBottom";
    String PN_WIDTH = "width";
    String PN_BROCHURE_CODE = "brochureCode";
    String PN_BROCHURE_ONLINE_URL = "onlineBrochureUrl";
    String PN_BROCHURE_IS_DIGITAL_ONLY = "brochureDigitalOnly";
    String PN_BROCHURE_IS_PREORDER = "preOrder";
    String PN_DESTINATION_REFERENCE = "destinationsReference";
    String PN_START_DATE = "startDate";
    final String PN_DESTINATION_ID = "destinationId";
    final String PN_SHIP_ID = "shipId";
    final String PN_EXCLUSIVE_OFFER_ID = "exclusiveOfferId";
    
    // Properties values
    String PV_AVAILABILITY_WAITLIST = "Waitlist";

    // HTML Suffix
    String HTML_SUFFIX = ".html";
    
    //URL Selectors
    final String SELECTOR_SINGLE_DESTINATION= "sd";
    final String SELECTOR_SINGLE_SHIP= "ss";
    final String SELECTOR_EXCLUSIVE_OFFER = "eo";
    final String SELECTOR_FYC_RESULT = "s";

    // Templates
    String PAGE_TEMPLATE_PAGE = "/apps/silversea/silversea-com/templates/page";
    String PAGE_TEMPLATE_PORT = "/apps/silversea/silversea-com/templates/port";
    String PAGE_TEMPLATE_PORTS_LIST = "/apps/silversea/silversea-com/templates/portslist";
    String PAGE_TEMPLATE_HOTEL = "/apps/silversea/silversea-com/templates/hotel";
    String PAGE_TEMPLATE_LAND_PROGRAM = "/apps/silversea/silversea-com/templates/landprogram";
    String PAGE_TEMPLATE_EXCURSION = "/apps/silversea/silversea-com/templates/excursion";
    String PAGE_TEMPLATE_EXCLUSIVE_OFFER = "/apps/silversea/silversea-com/templates/exclusiveoffer";
    String PAGE_TEMPLATE_CRUISE = "/apps/silversea/silversea-com/templates/cruise";
    String PAGE_TEMPLATE_TRAVEL_AGENCY = "/apps/silversea/silversea-com/templates/travelagency";
    String PAGE_TEMPLATE_COMBO_CRUISE_SEGMENT = "/apps/silversea/silversea-com/templates/combosegment";
    final String PAGE_TEMPLATE_DESTINATION = "/apps/silversea/silversea-com/templates/destination";
    final String PAGE_TEMPLATE_SHIP = "/apps/silversea/silversea-com/templates/ship";
    
    // Resource Type
    String RT_SUB_REDIRECT_PAGE = "subRedirectPage"; // TODO not a resource type

    String RT_IMAGE_CPT = "silversea/silversea-com/components/editorial/image";
    String RT_DESTINATION = "silversea/silversea-com/components/pages/destination";
    String RT_HOTEL = "silversea/silversea-com/components/pages/hotel";
    String RT_LAND_PROGRAMS = "silversea/silversea-com/components/pages/landprogram";
    String RT_EXCURSIONS = "silversea/silversea-com/components/pages/excursion";
    String RT_TRAVEL_AGENT = "silversea/silversea-com/components/pages/travelagency";
    String RT_EXCLUSIVE_OFFER = "silversea/silversea-com/components/pages/exclusiveoffer";
    String RT_PAGE = "silversea/silversea-com/components/pages/page";
    String RT_VOYAGE = "silversea/silversea-com/components/pages/cruise";
    String RT_SUITE = "silversea/silversea-com/components/pages/suite";
    String RT_SUITE_VARIATION = "silversea/silversea-com/components/pages/suitevariation";
    String RT_SHIP = "silversea/silversea-com/components/pages/ship";
    String RT_DINING = "silversea/silversea-com/components/pages/dining";
    String RT_PUBLIC_AREA = "silversea/silversea-com/components/pages/publicarea";
    String RT_VOYAGE_JOURNAL = "silversea/silversea-com/components/pages/voyagejournal";
    String RT_PRESS_RELEASE = "silversea/silversea-com/components/pages/pressrelease";
    String RT_FEATURE = "silversea/silversea-com/components/pages/feature";
    String RT_PORT = "silversea/silversea-com/components/pages/port";
    String RT_BLOG_POST = "silversea/silversea-com/components/pages/blogpost";
    String RT_KEY_PEOPLE = "silversea/silversea-com/components/pages/keypeople";
    String RT_VOYAGE_JOURNAL_DAY = "silversea/silversea-com/components/pages/voyagejournalsday";
    String RT_PUBLIC_AREA_VARIATION = "silversea/silversea-com/components/pages/publicareavariation";
    String RT_DINING_VARIATION = "silversea/silversea-com/components/pages/diningvariation";
    String RT_COMBO_CRUISE = "silversea/silversea-com/components/pages/combocruise";
    String RT_CRUISE = "silversea/silversea-com/components/pages/cruise";
    String RT_LANDING_PAGE = "silversea/silversea-ssc/components/pages/landingpage";
    
    
    String RT_EXCLUSIVE_OFFER_VARIATION = "silversea/silversea-com/components/pages/exclusiveoffervariation";
    String RT_SITEMAP = "silversea/silversea-com/components/pages/sitemappage";
    String RT_SITEMAP_INDEX = "silversea/silversea-com/components/pages/sitemapindex";
    String RT_PORT_PAGE_LIST = "silversea/silversea-com/components/pages/portslist";
    String RT_LIGHTBOX = "silversea/silversea-com/components/pages/lightbox";
    String RT_PRESS_RELEASE_LIST = "silversea/silversea-com/components/pages/pressreleaseslist";
    String RT_BLOG_POST_LIST = "silversea/silversea-com/components/pages/blogposts";
    String RT_REDIRECT = "foundation/components/redirect";
    String RT_COMBO_SEGMENT = "silversea/silversea-com/components/pages/combosegment";
    String RT_VOYAGEJOURNAL_LIST = "silversea/silversea-com/components/pages/voyagejournals";

    // Tags
    // TODO naming convention
    String GEOLOCATION_TAGS_PREFIX = "geotagging:";

    String TAG_NAMESPACE_LANGUAGES = "languages:";
    String TAG_NAMESPACE_BROCHURE_GROUPS = "brochure-groups:";
    String TAG_NAMESPACE_FEATURES = "features:";

    String TAG_NAMESPACE_CRUISE_TYPES = "cruise-types:";
    String TAG_CRUISE_TYPE_CRUISE = TAG_NAMESPACE_CRUISE_TYPES + "silversea-cruise";
    String TAG_CRUISE_TYPE_EXPEDITION = TAG_NAMESPACE_CRUISE_TYPES + "silversea-expedition";

    // Default Value
    String PAGINATION_LIMIT = "10";

    // Use for QueryBuilder
    String SEARCH_KEY_PATH = "path";
    String SEARCH_KEY_TYPE = "type";
    String SEARCH_KEY_OFF_SET = "p.offset";
    String SEARCH_KEY_PAGE_LIMIT = "p.limit";
    String SEARCH_KEY_ORDER_BY = "orderby";
    String SEARCH_KEY_ORDER_BY_SORT_ORDER = "orderby.sort";
    String SEARCH_KEY_PROPERTY = "property";
    String SEARCH_KEY_PROPERTY_VALUE = "property.value";

    // Responsive
    Integer DEFAULT_WIDTH_DESKTOP = 930;
    Integer DEFAULT_WIDTH_MOBILE = 737;

    // Content Path
    String PATH_SILVERSEA_COM = "/content/silversea-com";
    String PATH_TAGS_GEOLOCATION = "/etc/tags/geotagging";
    String PATH_DAM_SILVERSEA = "/content/dam/silversea-com";

    String FOLDER_BROCHURES = "brochures"; // TODO naming convention

    // Geolocation default values
    // TODO change to US
    String DEFAULT_GEOLOCATION_GEO_MARKET_CODE = "ft";
    String DEFAULT_GEOLOCATION_COUNTRY = "US";
    String DEFAULT_GEOLOCATION_COUNTRY_ISO3 = "USA";
    String DEFAULT_CURRENCY = "USD";

    String FEATURE_CODE_VENETIAN_SOCIETY = "vs_saving";
}