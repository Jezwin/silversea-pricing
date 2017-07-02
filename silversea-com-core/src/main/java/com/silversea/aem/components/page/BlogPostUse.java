package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.BlogPostModel;

/**
 * Created by mbennabi on 20/02/2017.
 */
@Deprecated
public class BlogPostUse extends WCMUsePojo {

    private BlogPostModel blog;

    @Override
    public void activate() throws Exception {
        blog = getCurrentPage().adaptTo(BlogPostModel.class);
    }

    public BlogPostModel getBlog() {
        return blog;
    }
}