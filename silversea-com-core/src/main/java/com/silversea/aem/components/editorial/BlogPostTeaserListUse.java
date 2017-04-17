package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.filter.BlogPostPageFilter;
import com.silversea.aem.models.BlogPostTeaserModel;
import com.silversea.aem.services.BlogPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlogPostTeaserListUse extends WCMUsePojo {

    private List<BlogPostTeaserModel> blogPostTeaserModelList = new ArrayList<>();

    private BlogPostTeaserModel firstBlogPostTeaser;

    private Boolean highlightFirst;

    @Override
    public void activate() throws Exception {
        String blogPostReference = getProperties().get("blogPostReference", String.class);
        highlightFirst = getProperties().get("highLightFirst", false);

        Page page = blogPostReference != null ? getPageManager().getPage(blogPostReference) : getCurrentPage();

        if (page != null) {
            Iterator<Page> blogPostPages = page.listChildren(new BlogPostPageFilter(), true);

            int i = 0;
            while (blogPostPages.hasNext() && i < 16) {
                Page blogPostPage = blogPostPages.next();

                BlogPostTeaserModel blogPost = blogPostPage.adaptTo(BlogPostTeaserModel.class);

                if (blogPost != null) {
                    blogPostTeaserModelList.add(blogPost);
                    i++;
                }
            }

            if (highlightFirst && blogPostTeaserModelList.size() > 0) {
                firstBlogPostTeaser = blogPostTeaserModelList.get(0);
                blogPostTeaserModelList.remove(0);
            }
        }
    }

    public Boolean getHighLightFirst() {
        return highlightFirst;
    }

    public List<BlogPostTeaserModel> getBlogPostTeaserModelList() {
        return blogPostTeaserModelList;
    }

    public BlogPostTeaserModel getFirstBlogPostTeaser() {
        return firstBlogPostTeaser;
    }

}
