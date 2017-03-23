package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.BlogPostTeaserModel;
import com.silversea.aem.services.BlogPostService;
import com.silversea.aem.services.impl.BlogPostServiceImpl;

public class BlogPostTeaserListUse extends WCMUsePojo {

    static final private Logger LOGGER = LoggerFactory.getLogger(BlogPostTeaserListUse.class);
    private static final String PROPERTY_BLOG_POST_REFERENCE = "blogPostReference";
    private static final String PROPERTY_HIGH_LIGHT_FIRST = "highLightFirst";
    private static final String DEFAULT_VALUE_TEMPLATE_PATH = "/apps/silversea/silversea-com/templates/blogpost";

    private String blogPostReference;
    private Boolean highLightFirst;

    private List<BlogPostTeaserModel> blogPostTeaserModelList;

    private BlogPostService blogPostService;
    
    private BlogPostTeaserModel firstBlogPostTeaser;

    @Override
    public void activate() throws Exception {

        blogPostTeaserModelList = new ArrayList<>();
        ValueMap properties = getProperties();
        try {
            blogPostService = getSlingScriptHelper().getService(BlogPostServiceImpl.class);
            // Map the blogPostReference in Dialog
            blogPostReference = properties.get(PROPERTY_BLOG_POST_REFERENCE,getCurrentPage().getPath());
            highLightFirst = properties.get(PROPERTY_HIGH_LIGHT_FIRST, Boolean.class);
            blogPostTeaserModelList = blogPostService.getBlogPostTeaserModelList(blogPostReference,
                    WcmConstants.DEFAULT_KEY_CQ_TEMPLATE, DEFAULT_VALUE_TEMPLATE_PATH,
                    WcmConstants.DEFAULT_VALUE_ORDER_BY_SORT_DESC);
            if(highLightFirst){
                firstBlogPostTeaser = blogPostTeaserModelList.get(0);
                blogPostTeaserModelList.remove(0);
            }

        } finally {
            properties = null;
        }
    }

    public String getBlogPostReference() {
        return blogPostReference;
    }

    public Boolean getHighLightFirst() {
        return highLightFirst;
    }

    public List<BlogPostTeaserModel> getBlogPostTeaserModelList() {
        return blogPostTeaserModelList;
    }

    public BlogPostTeaserModel getFirstBlogPostTeaser() {
        return firstBlogPostTeaser;
    }

    public BlogPostTeaserModel setFirstBlogPostTeaser(BlogPostTeaserModel firstBlogPostTeaser) {
        return this.firstBlogPostTeaser = firstBlogPostTeaser;
    }
}
