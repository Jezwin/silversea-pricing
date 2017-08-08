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

public class SearchResultsUse extends WCMUsePojo {

    private SearchResult searchResult;
    private String searchText;
    private String pageRequested;
    
    private static int hitsPerPage = 10; 

    @Override
    public void activate() throws Exception {
        //get parameters from request
        searchText = getRequest().getParameter("q");
        pageRequested = getRequest().getParameter("page");
        if (StringUtils.isEmpty(pageRequested)) {
            pageRequested = "1";
        }
        int startPage = Integer.parseInt(pageRequested);

        if (!StringUtils.isEmpty(searchText)) {
            // create query description as hash map (simplest way, same as form post)
            Map<String, String> map = new HashMap<String, String>();

            // create query description as hash map (simplest way, same as form post)
            map.put("path", "/content");
            map.put("type", "cq:Page");
            map.put("group.1_fulltext", searchText);

            Session session = getResourceResolver().adaptTo(Session.class);
            QueryBuilder builder = getResourceResolver().adaptTo(QueryBuilder.class);
            Query query = builder.createQuery(PredicateGroup.create(map), session);

            //calculate start index
            int startIndex = (startPage - 1) * hitsPerPage;

            query.setStart(startIndex);
            query.setHitsPerPage(hitsPerPage);

            searchResult = query.getResult();
        }
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }

    public String getSearchText() {
        return searchText;
    }

    public long getOffset() {
        return searchResult.getStartIndex();
    }

    public long getNumberOfPages() {
        return searchResult.getTotalMatches() / hitsPerPage;
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

}