/**
 * 
 */
package com.silversea.aem.constants;

/**
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
	
	//HTML Suffix
	String HTML_SUFFIX = ".html";

	// Resource Type
	String RT_SUB_REDIRECT_PAGE = "subRedirectPage";

	// Tags
	String TAG_NAMESPACE_LANGUAGES = "languages:";

	// Use for QueryBuilder
	String SEARCH_KEY_PATH = "path";
	String SEARCH_KEY_TYPE = "type";
	String SEARCH_KEY_OFF_SET = "p.offset";
	String SEARCH_KEY_PAGE_LIMIT = "p.limit";
	String SEARCH_KEY_ORDER_BY = "orderby";
	String SEARCH_KEY_ORDER_BY_SORT_ORDER = "orderby.sort";
	String SEARCH_KEY_PROPERTY = "property";
	String SEARCH_KEY_PROPERTY_VALUE = "property.value";
	String DEFAULT_KEY_CQ_TEMPLATE = "cq:template";
	String DEFAULT_KEY_CQ_PAGE = "cq:page";
	String DEFAULT_KEY_CQ_COMPONENT = "cq:component";
	String DEFAULT_VALUE_ORDER_BY_SORT_DESC = "desc";
	String DEFAULT_VALUE_ORDER_BY_SORT_ASC = "asc";
}
