package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.BlogPostModel;

public class BlogPostTeaserUse extends WCMUsePojo {

    private BlogPostModel blogPost;

    @Override
    public void activate() throws Exception {
        String blogPostReference = getProperties().get("blogPostReference", String.class);

        if (blogPostReference != null) {
            Page blogPostPage = getPageManager().getPage(blogPostReference);

            if (blogPostPage != null) {
                blogPost = blogPostPage.adaptTo(BlogPostModel.class);
            }
        }
    }

    /**
     * @return the blogPost
     */
    public BlogPostModel getBlogPost() {
        return blogPost;
    }
}