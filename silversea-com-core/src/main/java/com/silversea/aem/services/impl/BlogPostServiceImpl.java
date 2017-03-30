package com.silversea.aem.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.BlogPostTeaserModel;
import com.silversea.aem.services.BlogPostService;

@Component(immediate = true)
@Service(value = BlogPostService.class)
public class BlogPostServiceImpl implements BlogPostService {

    static final private Logger LOGGER = LoggerFactory.getLogger(BlogPostServiceImpl.class);

    private static final String DEFAULT_LIMIT_SIZE = "15";
    private static final String DEFAULT_OFF_SET = "0";
    private static final String DEFAULT_PUBLICATION_DATE = "@publicationDate";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private QueryBuilder builder;

    @Activate
    public void activate(final ComponentContext context) {

    }

    @Override
    public List<BlogPostTeaserModel> getBlogPostTeaserModelList(String path, String propertyKey, String propertyValue,
            String sortBy) {
        List<BlogPostTeaserModel> blogPostTeaserModelList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        try {
            map.put(WcmConstants.SEARCH_KEY_PATH, path);
            map.put(WcmConstants.SEARCH_KEY_TYPE, WcmConstants.DEFAULT_KEY_CQ_PAGE);
            map.put(WcmConstants.SEARCH_KEY_PROPERTY, propertyKey);
            map.put(WcmConstants.SEARCH_KEY_PROPERTY_VALUE, propertyValue);
            map.put(WcmConstants.SEARCH_KEY_OFF_SET, DEFAULT_OFF_SET);
            map.put(WcmConstants.SEARCH_KEY_PAGE_LIMIT, DEFAULT_LIMIT_SIZE);
            map.put(WcmConstants.SEARCH_KEY_ORDER_BY, DEFAULT_PUBLICATION_DATE);
            map.put(WcmConstants.SEARCH_KEY_ORDER_BY_SORT_ORDER, sortBy);
            Query query = builder.createQuery(PredicateGroup.create(map), getSession());

            SearchResult searchResult = query.getResult();
            Iterator<Resource> resourceIterator = searchResult.getResources();
            while (resourceIterator.hasNext()) {
                Resource res = resourceIterator.next();
                Page page = res.getParent().adaptTo(Page.class);
                if (page != null) {
                    BlogPostTeaserModel blogPostTeaserModel = page.adaptTo(BlogPostTeaserModel.class);
                    blogPostTeaserModelList.add(blogPostTeaserModel);
                }
            }
            getResourceResolver().close();
        } catch (LoginException e) {
            String errorMessage = "Some issues are happened ()";
            LOGGER.error(errorMessage, e);
        }
        return blogPostTeaserModelList;
    }

    private ResourceResolver getResourceResolver() throws LoginException {
        return resourceResolverFactory.getAdministrativeResourceResolver(null);
    }

    private Session getSession() throws LoginException {
        return getResourceResolver().adaptTo(Session.class);
    }

}
