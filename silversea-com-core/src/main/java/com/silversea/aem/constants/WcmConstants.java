package com.silversea.aem.constants;

/**
 * TODO clean and check if constants already exists
 * https://docs.adobe.com/docs/en/aem/6-2/develop/ref/javadoc/constant-values.html
 *
 * @author mjedli
 *
 */
public interface WcmConstants {
    /*
     * Before creating constant, check first this following page
     * https://docs.adobe.com/docs/en/aem/6-2/develop/ref/javadoc/constant-
     * values.html
     */

    // Properties
    String PN_NOT_IN_SITEMAP = "notInSitemap";
    String PN_FILE_REFERENCE = "fileReference";
    String PN_REFERENCE_PAGE_MAIN_NAVIGATION_BOTTOM = "referencePageMainNavigationBottom";
    String PN_WIDTH = "width";
    String PN_BROCHURE_CODE = "brochureCode";
    String PN_BROCHURE_ONLINE_URL = "onlineBrochureUrl";
    String PN_BROCHURE_IS_DIGITAL_ONLY = "brochureDigitalOnly";
    String PN_DESTINATION_REFERENCE = "destinationsReference";
    String PN_START_DATE = "startDate";
    String PN_COUNTRY_ID = "country_id";
    String PN_COUNTRY_NAME = "country_name";

    // HTML Suffix
    String HTML_SUFFIX = ".html";

    // Resource Type
    String RT_SUB_REDIRECT_PAGE = "subRedirectPage";
    String RT_IMAGE_CPT = "silversea/silversea-com/components/editorial/image";
    String RT_TAG = "cq/tagging/components/tag";
    String RT_DESTINATION = "silversea/silversea-com/components/pages/destination";
    String RT_HOTEL = "silversea/silversea-com/components/pages/hotel";
    String RT_LAND_PROGRAMS = "silversea/silversea-com/components/pages/landprogram";
    String RT_EXCURSIONS = "silversea/silversea-com/components/pages/excursion";
    String RT_TRAVEL_AGENT = "silversea/silversea-com/components/pages/travelagency";

    // Node name
    String NN_DEFAULT = "default";
    String NN_PHONE = "phone";

    // Tags
    String TAG_NAMESPACE_LANGUAGES = "languages:";
    String TAG_NAMESPACE_BROCHURE_GROUPS = "brochure-groups:";

    // Default Value
    String PAGINATION_LIMIT = "10";

    // Use for QueryBuilder
    String SEARCH_KEY_PATH = "path";
    String SEARCH_NODE_NAME = "nodename";
    String SEARCH_KEY_TYPE = "type";
    String SEARCH_KEY_OFF_SET = "p.offset";
    String SEARCH_KEY_PAGE_LIMIT = "p.limit";
    String SEARCH_KEY_ORDER_BY = "orderby";
    String SEARCH_KEY_ORDER_BY_SORT_ORDER = "orderby.sort";
    String SEARCH_KEY_PROPERTY = "property";
    String SEARCH_KEY_PROPERTY_VALUE = "property.value";
    String DEFAULT_KEY_CQ_TEMPLATE = "cq:template";
    String DEFAULT_KEY_CQ_PAGE = "cq:page";
    String DEFAULT_KEY_CQ_TAG = "cq:tag";
    String DEFAULT_KEY_CQ_COMPONENT = "cq:component";
    String DEFAULT_VALUE_ORDER_BY_SORT_DESC = "desc";
    String DEFAULT_VALUE_ORDER_BY_SORT_ASC = "asc";

    // Responsive
    Integer DEFAULT_WIDTH_DESKTOP = 930;
    Integer DEFAULT_WIDTH_MOBILE = 737;

    // Prefix of geolocation tags
    String GEOLOCATION_TAGS_PREFIX = "geotagging:";
    
    // Content Path
    String PATH_TAGS_GEOLOCATION = "/etc/tags/geotagging";
    String PATH_DAM_SILVERSEA = "/content/dam/silversea-com";
    String FOLDER_BROCHURES = "brochures";
}