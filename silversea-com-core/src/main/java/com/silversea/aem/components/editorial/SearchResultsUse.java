package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.silversea.aem.helper.LanguageHelper;
/**
 * Class used for the search results component.
 * @author lymendoza
 *
 */
public class SearchResultsUse extends WCMUsePojo {

    private SearchResult searchResult;
    private String searchText;
    private String pageRequested;
    private long numberOfPages;
    private int pageRequestedInt;
    
    private int hitsPerPage = 10; 

    @Override
    public void activate() throws Exception {
        //get parameters from request
        searchText = getRequest().getParameter("q");
        pageRequested = getRequest().getParameter("page");
        if (StringUtils.isEmpty(pageRequested)) {
            pageRequested = "1";
        }
        pageRequestedInt = Integer.parseInt(pageRequested);
        
        //get site language
        String lang = LanguageHelper.getLanguage(getRequest());
        if (lang == null) {
            lang = LanguageHelper.getLanguage(getCurrentPage());
        }

        if (!StringUtils.isEmpty(searchText)) {
            // create query description as hash map (simplest way, same as form post)
            Map<String, String> map = new HashMap<String, String>();

            // create query description as hash map (simplest way, same as form post)
            map.put("path", "/content/silversea-com/" + lang);
            map.put("type", "cq:Page");
            map.put("group.1_fulltext", searchText);

            Session session = getResourceResolver().adaptTo(Session.class);
            QueryBuilder builder = getResourceResolver().adaptTo(QueryBuilder.class);
            Query query = builder.createQuery(PredicateGroup.create(map), session);

            //get number of results to show per page
            hitsPerPage = getProperties().get("hitsPerPage", 10);
            
            //calculate start index
            int startIndex = (pageRequestedInt - 1) * hitsPerPage;

            query.setStart(startIndex);
            query.setHitsPerPage(hitsPerPage);

            searchResult = query.getResult();
            numberOfPages = searchResult.getTotalMatches() / hitsPerPage;
        }
    }

    public SearchResult getSearchResult() {
        return searchResult;
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

    public boolean getShowNext() {
        return (pageRequestedInt + 3) <= numberOfPages;
    }

    public boolean getShowFirstPage() {
        return pageRequestedInt >= 3;
    }

    public boolean getShowLastPage() {
        return (numberOfPages - pageRequestedInt ) > 1;
    }
}