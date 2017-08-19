package com.silversea.aem.components.editorial;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.jcr.Session;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.silversea.aem.constants.WcmConstants;
import org.apache.commons.lang3.StringUtils;

public class SearchCruiseUse extends WCMUsePojo {
    private Map<String, String> dates;
    private List<Page> destinations;

    @Override
    public void activate() throws Exception {
        String destinationPath = getProperties().get(WcmConstants.PN_DESTINATION_REFERENCE, String.class);
        Map<String, String> map = new HashMap<String, String>();

        // set destination list
        setDestinationList(destinationPath);

        // create query description as hash map
        map.put("path", destinationPath);
        map.put("type", NameConstants.NT_PAGE);
        map.put("1_property", JcrConstants.JCR_CONTENT + "/" + NameConstants.NN_TEMPLATE);
        map.put("1_property.value", "/apps/silversea/silversea-com/templates/cruise");

        // Do query
        Session session = getResourceResolver().adaptTo(Session.class);
        QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
        SearchResult result = query.getResult();

        // Get result from Query
        Page page;
        Locale locale = getCurrentPage().getLanguage(false);
        SimpleDateFormat formaterValue = new SimpleDateFormat("MMMMM yyyy", locale);
        SimpleDateFormat formaterKey = new SimpleDateFormat("yyyy-MM", locale);
        String formatDateKey, formatDateValue;
        Calendar startDate;
        dates = new HashMap<String, String>();

        for (Hit hit : result.getHits()) {
            page = hit.getResource().adaptTo(Page.class);

            startDate = page.getProperties().get(WcmConstants.PN_START_DATE, Calendar.class);
            formatDateValue = formaterValue.format(startDate.getTime()).toString();
            formatDateKey = formaterKey.format(startDate.getTime()).toString();

            dates.put(formatDateKey, formatDateValue);
        }
    }

    public void setDestinationList(String destinationPath) {
        PageManager pageManager = getResourceResolver().adaptTo(PageManager.class);
        Page destination = pageManager.getPage(destinationPath);
        Iterator<Page> iteratorDestination = destination.listChildren();
        List<Page> destinationList = Lists.newArrayList(iteratorDestination);
        destinations = destinationList.stream()
                .filter(item -> StringUtils.equals(item.getProperties().get("sling:resourceType", String.class), WcmConstants.RT_DESTINATION)  )
                .collect(Collectors.toList());

    }

    public Map<String, String> getDates() {
        return new TreeMap<String, String>(dates);
    }

    public List<Page> getDestinations() {
        return destinations;
    }
}