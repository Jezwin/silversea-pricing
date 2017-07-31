package com.silversea.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;

import javax.jcr.Session;
import java.util.*;

/**
 * Created by asiba on 28/03/2017.
 *
 * TODO review various NPE
 * TODO review java coding style conventions
 * TODO constants
 */
public class PressReleasesListUse extends WCMUsePojo {

    private List<Page> pressReleaseList = new ArrayList<>();

    private String currentPage;

    private long totalMatches;

    private int numberOfPages;

    @Override
    public void activate() throws Exception {
        final InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());

        // TODO use int instead of string, and merge limit and ilimit
        final String limit = properties.getInherited("paginationLimit", WcmConstants.PAGINATION_LIMIT);
        final Integer ilimit = Integer.parseInt(limit);

        if (getRequest().getRequestParameter("page") != null) {
            currentPage = getRequest().getRequestParameter("page").toString();
        } else {
            currentPage = "1";
        }

        int pageNum = Integer.parseInt(currentPage) - 1;
        if (pageNum <= 0) {
            pageNum = 0;
        }
        pageNum = pageNum * ilimit;

        /*
        * Construct Query
        * */
        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put(WcmConstants.SEARCH_KEY_PATH, getCurrentPage().getPath());
        mapQuery.put(WcmConstants.SEARCH_KEY_TYPE, "cq:PageContent");
        mapQuery.put(WcmConstants.SEARCH_KEY_PROPERTY, "sling:resourceType");
        mapQuery.put(WcmConstants.SEARCH_KEY_PROPERTY_VALUE, "silversea/silversea-com/components/pages/pressrelease");
        mapQuery.put(WcmConstants.SEARCH_KEY_ORDER_BY, "@jcr:content/publicationDate");
        mapQuery.put(WcmConstants.SEARCH_KEY_ORDER_BY_SORT_ORDER, "desc");
        mapQuery.put(WcmConstants.SEARCH_KEY_OFF_SET, String.valueOf(pageNum));
        mapQuery.put(WcmConstants.SEARCH_KEY_PAGE_LIMIT, limit);

        /*
        * Build Query
        * */
        Session session = getResourceResolver().adaptTo(Session.class);
        QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(mapQuery), session);
        query.setStart(pageNum);
        query.setHitsPerPage(ilimit);
        SearchResult result = query.getResult();

        /*
        * Get result from Query
        * */
        totalMatches = result.getTotalMatches();
        numberOfPages = (int) Math.ceil((float) totalMatches / ilimit);
        for (Hit hit : result.getHits()) {
            Page page = hit.getResource().getParent().adaptTo(Page.class);
            pressReleaseList.add(page);
        }
    }

    public List<Page> getPressReleaseList() {
        return pressReleaseList;
    }

    public Integer getCurrent() {
        return Integer.parseInt(currentPage);
    }

    public int getNext() {
        if (Integer.parseInt(currentPage) < numberOfPages) {
            return Integer.parseInt(currentPage) + 1;
        }

        return -1;
    }

    public int getPrev() {
        if (Integer.parseInt(currentPage) > 1) {
            return Integer.parseInt(currentPage) - 1;
        }

        return -1;
    }

    public List<Integer> getNumberOfPages() {
        List<Integer> num = new ArrayList<>();

        for (int i = 0; i < numberOfPages; i++) {
             num.add(i+1);
        }
        return num;
    }

    public long getTotalMatches() {
        return totalMatches;
    }
}
