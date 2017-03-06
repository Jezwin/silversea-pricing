package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.BlogPostTeaserModel;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class BlogPostTeaserUse extends WCMUsePojo {

    private BlogPostTeaserModel blogTeaser;

    @Override
    public void activate() throws Exception {
        blogTeaser = getCurrentPage().adaptTo(BlogPostTeaserModel.class);
    }

    public BlogPostTeaserModel getBlogTeaser() {
        return blogTeaser;
    }
}