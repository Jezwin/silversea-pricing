package com.silversea.aem.components.editorial;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.constants.WcmConstants;

public class SearchCruiseUse extends WCMUsePojo {
    private Map<String, String> dates;

    @Override
    public void activate() throws Exception {
        String destinationPath = getProperties().get(WcmConstants.PN_DESTINATION_REFERENCE, String.class);
        Map<String, String> map = new HashMap<String, String>();

        // create query description as hash map
        map.put("path", destinationPath);
        map.put("type", NameConstants.NT_PAGE);
        map.put("1_property", JcrConstants.JCR_CONTENT + "/" + NameConstants.NN_TEMPLATE);
        map.put("1_property.value", TemplateConstants.PATH_VOYAGE);

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

            startDate = page.getProperties().get(WcmConstants.START_DATE, Calendar.class);
            formatDateValue = formaterValue.format(startDate.getTime()).toString();
            formatDateKey = formaterKey.format(startDate.getTime()).toString();

            dates.put(formatDateKey, formatDateValue);
        }
    }

    public Map<String, String> getDates() {
        return new TreeMap<String, String>(dates);
    }
}