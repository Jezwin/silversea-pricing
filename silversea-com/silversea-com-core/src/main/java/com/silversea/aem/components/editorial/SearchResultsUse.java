package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.ResultPage;
import com.day.cq.search.result.SearchResult;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class used for the search results component.
 * 
 * @author lymendoza
 *
 */
public class SearchResultsUse extends WCMUsePojo {

    private SearchResult searchResult;
    private List<Hit> hitsListFiletered;
    private String searchText;
    private String pageRequested;
    private long numberOfPages;
    private int pageRequestedInt;
    private Boolean noResult;

    private int hitsPerPage = 10;

    @Override
    public void activate() throws Exception {
        // get parameters from request
        searchText = getRequest().getParameter("q");
        pageRequested = getRequest().getParameter("page");
        if (StringUtils.isEmpty(pageRequested)) {
            pageRequested = "1";
        }
        pageRequestedInt = Integer.parseInt(pageRequested);

        // get site language
        String lang = LanguageHelper.getLanguage(getRequest());
        if (lang == null) {
            lang = LanguageHelper.getLanguage(getCurrentPage());
        }

        if (!StringUtils.isEmpty(searchText)) {
            // create query description as hash map (simplest way, same as form
            // post)
            Map<String, String> map = new HashMap<String, String>();

            // create query description as hash map (simplest way, same as form
            // post)
            map.put("path", "/content/silversea-com/" + lang);
            map.put("type", "cq:Page");
            map.put("1_fulltext", searchText);

            map.put("2_property", "jcr:content/sling:resourceType");
            map.put("3_property", "jcr:content/sling:resourceType");
            map.put("4_property", "jcr:content/sling:resourceType");
            map.put("5_property", "jcr:content/sling:resourceType");
            map.put("6_property", "jcr:content/sling:resourceType");
            map.put("7_property", "jcr:content/sling:resourceType");
            map.put("8_property", "jcr:content/sling:resourceType");
            map.put("10_property", "jcr:content/sling:resourceType");
            map.put("11_property", "jcr:content/sling:resourceType");
            map.put("12_property", "jcr:content/sling:resourceType");
            map.put("13_property", "jcr:content/sling:resourceType");
            map.put("14_property", "jcr:content/sling:resourceType");
            map.put("15_property", "jcr:content/sling:resourceType");
            map.put("16_property", "jcr:content/sling:resourceType");
            map.put("17_property", "jcr:content/sling:resourceType");
            map.put("18_property", "jcr:content/sling:resourceType");
            map.put("19_property", "jcr:content/sling:resourceType");
            map.put("20_property", "jcr:content/sling:resourceType");
            map.put("21_property", "jcr:content/sling:resourceType");
            map.put("22_property", "jcr:content/notIndexed");
            
           // map.put("group.23_property", "jcr:content/isVisible");
           // map.put("group.24_property", "jcr:content/isVisible");
           // map.put("group.p.or", "true");

            
            map.put("2_property.operation", "unequals");
            map.put("3_property.operation", "unequals");
            map.put("4_property.operation", "unequals");
            map.put("5_property.operation", "unequals");
            map.put("6_property.operation", "unequals");
            map.put("7_property.operation", "unequals");
            map.put("8_property.operation", "unequals");
            map.put("10_property.operation", "unequals");
            map.put("11_property.operation", "unequals");
            map.put("12_property.operation", "unequals");
            map.put("13_property.operation", "unequals");
            map.put("14_property.operation", "unequals");
            map.put("15_property.operation", "unequals");
            map.put("16_property.operation", "unequals");
            map.put("17_property.operation", "unequals");
            map.put("18_property.operation", "unequals");
            map.put("19_property.operation", "unequals");
            map.put("20_property.operation", "unequals");
            map.put("21_property.operation", "unequals");
            map.put("22_property.operation", "not");
            
           // map.put("group.23_property.operation", "not");
           // map.put("group.24_property.operation", "unequals");


            map.put("2_property.value", WcmConstants.RT_PUBLIC_AREA);
            map.put("3_property.value", WcmConstants.RT_EXCLUSIVE_OFFER_VARIATION);
            map.put("4_property.value", WcmConstants.RT_KEY_PEOPLE);
            map.put("5_property.value", WcmConstants.RT_LAND_PROGRAMS);
            map.put("6_property.value", WcmConstants.RT_EXCURSIONS);
            map.put("7_property.value", WcmConstants.RT_HOTEL);
            map.put("8_property.value", WcmConstants.RT_TRAVEL_AGENT);
            map.put("10_property.value", WcmConstants.RT_LIGHTBOX);
            map.put("11_property.value", WcmConstants.RT_SITEMAP);
            map.put("12_property.value", WcmConstants.RT_SITEMAP_INDEX);
            map.put("13_property.value", WcmConstants.RT_PORT_PAGE_LIST);
            map.put("14_property.value", WcmConstants.RT_VOYAGEJOURNAL_LIST);
            map.put("15_property.value", WcmConstants.RT_VOYAGE_JOURNAL);
            map.put("16_property.value", WcmConstants.RT_PRESS_RELEASE_LIST);
            map.put("17_property.value", WcmConstants.RT_BLOG_POST_LIST);
            map.put("18_property.value", WcmConstants.RT_REDIRECT);
            map.put("19_property.value", WcmConstants.RT_COMBO_SEGMENT);
            map.put("20_property.value", WcmConstants.RT_LANDING_PAGE);
            map.put("21_property.value", WcmConstants.RT_EXCLUSIVE_OFFER);
            map.put("22_property.value", "true");
            
            //map.put("group.23_property.value", "false");
            //map.put("group.24_property.value", "false");
            
            Session session = getResourceResolver().adaptTo(Session.class);
            QueryBuilder builder = getResourceResolver().adaptTo(QueryBuilder.class);
            Query query = builder.createQuery(PredicateGroup.create(map), session);

            // get number of results to show per page
            hitsPerPage = getProperties().get("hitsPerPage", 10);

            // calculate start index
            int startIndex = (pageRequestedInt - 1) * hitsPerPage;

            query.setStart(startIndex);
            query.setHitsPerPage(hitsPerPage);

            searchResult = query.getResult();
            
            //hitsListFiletered = searchResult.getHits();
            hitsListFiletered = new ArrayList<Hit>();
            for (int i = 0; i < searchResult.getHits().size(); i++) {
            	Boolean isVisible = searchResult.getHits().get(i).getProperties().get("isVisible", Boolean.class);
				if(isVisible != null){
					if(!isVisible){
						
					}else{
						hitsListFiletered.add(searchResult.getHits().get(i));
					}
				}else{
					hitsListFiletered.add(searchResult.getHits().get(i));
				}
			}
            
            if(hitsListFiletered.size() == 0){
            	noResult = true;
            }else{
            	noResult = false;
            }

            numberOfPages = searchResult.getTotalMatches() / hitsPerPage;
        }
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }
    
    public List<Hit> getHitsListFiletered(){
    	return hitsListFiletered;
    }

    public String getSearchText() {
        return searchText;
    }

    public long getNumberOfPages() {
        return numberOfPages;
    }

    public ArrayList<String> getPageTrio() {
        ArrayList<String> pages = new ArrayList<String>();
        int requestedPage = Integer.parseInt(pageRequested);
        if (requestedPage != 1) {
            pages.add("" + (requestedPage - 1));
        }
        pages.add("" + requestedPage);
        if (requestedPage < getNumberOfPages()) {
            pages.add("" + (requestedPage + 1));
        }
        return pages;
    }

    public String getPageRequested() {
        return pageRequested;
    }

    public int getPageRequestedInt() {
        return pageRequestedInt;
    }

    public int getNextPage() {
        return pageRequestedInt + 1;
    }

    public int getPreviousPage() {
        return pageRequestedInt - 1;
    }

    public int getHitsPerPage() {
        return hitsPerPage;
    }

    public boolean getShowNextLink() {
        return pageRequestedInt != numberOfPages;
    }

    public boolean getShowPreviousLink() {
        return pageRequestedInt != 1;
    }

    public boolean getShowPrev() {
        return pageRequestedInt > 3;
    }
    
    public Boolean getNoResult(){
    	return noResult;
    }

    public boolean getShowNext() {
        return (pageRequestedInt + 3) <= numberOfPages;
    }

    public boolean getShowFirstPage() {
        return pageRequestedInt >= 3;
    }

    public boolean getShowLastPage() {
        return (numberOfPages - pageRequestedInt) > 1;
    }

}