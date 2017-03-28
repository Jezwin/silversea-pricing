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
import com.silversea.aem.components.editorial.BlogPostTeaserListUse;
import com.silversea.aem.constants.WcmConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asiba on 28/03/2017.
 */
public class PressreleaseslistUse extends WCMUsePojo {
    private List<Page> pressReleaseList;

    private Map<String, String> mapQuery = new HashMap<String, String>();

    private String currentPage;

    private int pageNum;

    private long totalMatches;

    private int numberOfPages;

    private String limit;

    private Integer Ilimit;

    static final private Logger LOGGER = LoggerFactory.getLogger(PressreleaseslistUse.class);

    @Override
    public void activate() throws Exception {

        /*
        * Init page value
        **/
        Page page;
        pressReleaseList = new ArrayList<>();

        final InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());
        limit = properties.getInherited("paginationLimit", WcmConstants.PAGINATION_LIMIT);
        Ilimit = Integer.parseInt(limit);

        if (getRequest().getRequestParameter("page") != null)
            currentPage = getRequest().getRequestParameter("page").toString();
        else
            currentPage = "1";

        pageNum = Integer.parseInt(currentPage) - 1;
        if (pageNum <= 0)
            pageNum = 0;
        pageNum = pageNum * Ilimit;

        /*
        * Construct Query
        * */
        mapQuery.put(WcmConstants.SEARCH_KEY_PATH, getCurrentPage().getPath());
        mapQuery.put(WcmConstants.SEARCH_KEY_TYPE, "cq:PageContent");
        mapQuery.put(WcmConstants.SEARCH_KEY_PROPERTY, "sling:resourceType");
        mapQuery.put(WcmConstants.SEARCH_KEY_PROPERTY_VALUE, "silversea/silversea-com/components/pages/pressrelease");
        mapQuery.put(WcmConstants.SEARCH_KEY_ORDER_BY, "@jcr:content/jcr:lastModified");
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
        query.setHitsPerPage(Ilimit);
        SearchResult result = query.getResult();

        /*
        * Get result from Query
        * */
        totalMatches = result.getTotalMatches();
        numberOfPages = (int) Math.ceil((float) totalMatches / Ilimit);
        for (Hit hit : result.getHits()) {
            page = hit.getResource().getParent().adaptTo(Page.class);
            pressReleaseList.add(page);
        }
    }

    public List<Page> getPressReleaseList() {
        return pressReleaseList;
    }

    public int getNext() {
        if (Integer.parseInt(currentPage) < numberOfPages)
            return Integer.parseInt(currentPage) + 1;
        return -1;
    }

    public int getPrev() {
        if (Integer.parseInt(currentPage) > 1)
            return Integer.parseInt(currentPage) - 1;
        return -1;
    }

    public long getNumberOfPages() { return numberOfPages; }

    public long getTotalMatches() { return totalMatches; }
}
