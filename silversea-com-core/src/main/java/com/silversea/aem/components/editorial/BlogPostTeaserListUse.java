package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.filter.BlogPostPageFilter;
import com.silversea.aem.models.BlogPostModel;

import java.text.SimpleDateFormat;
import java.util.*;

public class BlogPostTeaserListUse extends WCMUsePojo {

    private List<BlogPostModel> blogPostList = new ArrayList<>();

    private Integer lastDay;

    @Override
    public void activate() throws Exception {
        final String blogPostReference = getProperties().get("blogPostReference", getCurrentPage().getPath());
        final Page pageParent = getPageManager().getPage(blogPostReference);

        final Iterator<Page> blogPostPages = pageParent.listChildren(new BlogPostPageFilter(), true);

        while (blogPostPages.hasNext()) {
            Page blogPost = blogPostPages.next();

            if (blogPost != null) {
                blogPostList.add(blogPost.adaptTo(BlogPostModel.class));
            }
        }

        blogPostList.sort((o1, o2) -> -o1.getPublicationDate().compareTo(o2.getPublicationDate()));

        // Test if the current page is a month page blog
        if (getCurrentPage().getName().length() <= 2) {
            final String month = getCurrentPage().getName(); // month format : MM
            final String year = getCurrentPage().getParent().getName(); // month format : yyyy

            final Date convertedDate = new SimpleDateFormat("MMyyyy").parse(month + year);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(convertedDate);

            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * @return the blogPostList
     */
    public List<BlogPostModel> getBlogPostList() {
        return blogPostList.size() > 15 ? blogPostList.subList(0, 15) : blogPostList;
    }

    /**
     * @return the lastDay
     */
    public Integer getLastDay() {
        return lastDay;
    }
}