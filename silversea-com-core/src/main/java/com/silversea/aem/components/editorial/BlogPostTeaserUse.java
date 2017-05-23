package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.BlogPostTeaserModel;

public class BlogPostTeaserUse extends WCMUsePojo {

    private BlogPostTeaserModel blogTeaser;

    @Override
    public void activate() throws Exception {
        String blogPostReference = getProperties().get("blogPostReference", String.class);

        if (blogPostReference != null) {
            Page blogPostPage = getPageManager().getPage(blogPostReference);

            if (blogPostPage != null) {
                blogTeaser = blogPostPage.adaptTo(BlogPostTeaserModel.class);
            }
        }
    }

    public BlogPostTeaserModel getBlogTeaser() {
        return blogTeaser;
    }

}