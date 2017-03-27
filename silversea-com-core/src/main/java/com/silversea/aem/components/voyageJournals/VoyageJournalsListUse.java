package com.silversea.aem.components.voyageJournals;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;

import javax.jcr.Session;
import java.util.*;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class VoyageJournalsListUse extends WCMUsePojo {

    private List<Page> voyageJournalList;

    Map<String, String> mapQuery = new HashMap<String, String>();


    @Override
    public void activate() throws Exception {

        voyageJournalList = new ArrayList<>();
        mapQuery.put("path", getCurrentPage().getPath());
        mapQuery.put("type", "cq:PageContent");
        mapQuery.put("property", "sling:resourceType");
        mapQuery.put("property.value", "silversea/silversea-com/components/pages/voyagejournal");
        //map.put("p.offset", "0"); // same as query.setStart(0) below
        //map.put("p.limit", "20"); // same as query.setHitsPerPage(20) below

        Session session = getResourceResolver().adaptTo(Session.class);
        QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(mapQuery), session);
        //query.setStart(0);
        //query.setHitsPerPage(20);

        SearchResult result = query.getResult();

        Iterator<Resource> iterator = result.getResources();
        while (iterator.hasNext()) {
            Page page = iterator.next().getParent().adaptTo(Page.class);
            voyageJournalList.add(page);
        }
    }

    public List<Page> getVoyageJournalList() {
        return voyageJournalList;
    }
}
