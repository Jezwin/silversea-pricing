package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;

public class QuoteRequestUse extends WCMUsePojo {
    private List<Resource> countries;

    @Override
    public void activate() throws Exception {
        Map<String, String> queryMap = new HashMap<String, String>();

        // create query description as hash map
        queryMap.put(PathPredicateEvaluator.PATH, WcmConstants.PATH_TAGS_GEOLOCATION);
        queryMap.put(TypePredicateEvaluator.TYPE, TagConstants.NT_TAG);
        queryMap.put(JcrPropertyPredicateEvaluator.PROPERTY, "name");
        queryMap.put(JcrPropertyPredicateEvaluator.PROPERTY + "." + JcrPropertyPredicateEvaluator.OPERATION, JcrPropertyPredicateEvaluator.OP_EXISTS);
        queryMap.put(Predicate.ORDER_BY, "@" + JcrConstants.JCR_TITLE);
        queryMap.put(Predicate.ORDER_BY + "." + Predicate.PARAM_SORT, Predicate.SORT_ASCENDING);
        queryMap.put(PredicateConverter.GROUP_PARAMETER_PREFIX + "." + Predicate.PARAM_LIMIT, "-1");

        // Do query
        Session session = getResourceResolver().adaptTo(Session.class);
        QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
        SearchResult result = query.getResult();

        // Get result from Query
        countries = new ArrayList<Resource>();
        for (Hit hit : result.getHits()) {
            countries.add(hit.getResource());
        }
    }

    /**
     * @return the countries
     */
    public List<Resource> getCountries() {
        return countries;
    }

    /**
     * @return the isChecked
     */
    public Boolean getIsChecked() {
        String[] tags = getProperties().get(NameConstants.PN_TAGS, String[].class);
        TagManager tagManager;
        if (tags != null) {
            tagManager = getResourceResolver().adaptTo(TagManager.class);
            for (String tagId : tags) {
                if (tagManager.resolve(tagId).getName().equals(GeolocationHelper.getCountryCode(getRequest()))) {
                    return false;
                }
            }
        }
        return true;
    }
}