package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.BlogPostTeaserModel;
import com.silversea.aem.services.BlogPostService;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class BlogPostTeaserUse extends WCMUsePojo {

    private static final String PROPERTY_BLOG_POST_REFERENCE_PARAM = "blogPostReference";

    private static final String DEFAULT_VALUE_TEMPLATE_PATH = "/apps/silversea/silversea-com/templates/blogpost";

    private BlogPostTeaserModel blogTeaser;

    private BlogPostService blogPostService;

    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();
        try {
            blogPostService = getSlingScriptHelper().getService(BlogPostService.class);
            // Map the blogPostReference in Dialog
            String blogPostReferenceParam = properties.get(PROPERTY_BLOG_POST_REFERENCE_PARAM, String.class);
            Resource res = getResourceResolver().getResource(blogPostReferenceParam);
            if (null != res) {
                if (null != blogTeaser) {
                    Page targetPage = res.adaptTo(Page.class);
                    if (null != targetPage) {
                        blogTeaser = targetPage.adaptTo(BlogPostTeaserModel.class);
                    }
                } else {
                    blogTeaser = blogPostService
                            .getBlogPostTeaserModelList(blogPostReferenceParam, WcmConstants.DEFAULT_KEY_CQ_TEMPLATE,
                                    DEFAULT_VALUE_TEMPLATE_PATH, WcmConstants.DEFAULT_VALUE_ORDER_BY_SORT_DESC)
                            .get(0);
                }
            }
        } finally

        {
            properties = null;
        }

    }

    public BlogPostTeaserModel getBlogTeaser() {
        return blogTeaser;
    }

}