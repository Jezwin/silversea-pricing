package com.silversea.aem.components.voyageJournals;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.services.impl.GeolocationTagServiceImpl;
import org.apache.sling.api.resource.Resource;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.*;

public class VoyageJournalsListUse extends WCMUsePojo {

    private List<Page> voyageJournalList;

    private Map<String, String> mapQuery = new HashMap<String, String>();

    private String currentPage;

    private int pageNum;

    private long totalMatches;

    private int numberOfPages;

    private String limit;

    private Integer Ilimit;

    static final private Logger LOGGER = LoggerFactory.getLogger(VoyageJournalsListUse.class);


    @Override
    public void activate() throws Exception {

        final InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());
        limit = properties.getInherited("paginationLimit", String.class);
        Ilimit = Integer.parseInt(limit);

        voyageJournalList = new ArrayList<>();
        if (getRequest().getRequestParameter("page") != null)
            currentPage = getRequest().getRequestParameter("page").toString();
        else
            currentPage = "1";

        pageNum = Integer.parseInt(currentPage) - 1;
        if (pageNum <= 0)
            pageNum = 0;
        pageNum = pageNum * Ilimit;

        LOGGER.debug("{} current page is ", currentPage);

        mapQuery.put("path", getCurrentPage().getPath());
        mapQuery.put("type", "cq:PageContent");
        mapQuery.put("property", "sling:resourceType");
        mapQuery.put("property.value", "silversea/silversea-com/components/pages/voyagejournal");
        mapQuery.put("orderby", "@jcr:content/jcr:lastModified");
        mapQuery.put("orderby.sort", "desc");
        mapQuery.put("p.offset", String.valueOf(pageNum));
        mapQuery.put("p.limit", limit);

        LOGGER.debug("offset : {} limit : {} Ilimit : {}", String.valueOf(pageNum), limit, Ilimit);

        Session session = getResourceResolver().adaptTo(Session.class);
        QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(mapQuery), session);
        query.setStart(pageNum);
        query.setHitsPerPage(Ilimit);

        SearchResult result = query.getResult();

        totalMatches = result.getTotalMatches();
        numberOfPages = (int) Math.ceil((float) totalMatches / Ilimit);

        LOGGER.debug("totalMatches : {} number of page : {}", totalMatches, numberOfPages);

        Page page;
        for (Hit hit : result.getHits()) {
            page = hit.getResource().getParent().adaptTo(Page.class);
            voyageJournalList.add(page);
        }

        LOGGER.debug("total res : {}", voyageJournalList.size());
    }

    public List<Page> getVoyageJournalList() {
        return voyageJournalList;
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
