package com.silversea.aem.components.editorial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.filter.BlogPostPageFilter;
import com.silversea.aem.models.BlogPostModel;

public class BlogPostTeaserListUse extends WCMUsePojo {
    private List<BlogPostModel> blogPostList = new ArrayList<>();
    private Integer lastDay;

    @Override
    public void activate() throws Exception {
        String blogPostReference = getProperties().get("blogPostReference", getCurrentPage().getPath());
        Page pageParent = getPageManager().getPage(blogPostReference);

        Iterator<Page> blogPostPages = pageParent.listChildren(new BlogPostPageFilter(), true);

        int i = 0;
        while (blogPostPages.hasNext() && i < 15) {
            Page blogPost = blogPostPages.next();

            if (blogPost != null) {
                blogPostList.add(blogPost.adaptTo(BlogPostModel.class));
                String path = blogPost.adaptTo(BlogPostModel.class).getThumbnail();
                i++;
            }
        }

        // Test if the current page is a month page blog
        if (getCurrentPage().getName().length() <= 2) {
            String month = getCurrentPage().getName(); // month format : MM
            String year = getCurrentPage().getParent().getName(); // month format : yyyy

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyy");
            Date convertedDate = dateFormat.parse(month + year);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(convertedDate);
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * @return the blogPostList
     */
    public List<BlogPostModel> getBlogPostList() {
        return blogPostList;
    }

    /**
     * @return the lastDay
     */
    public Integer getLastDay() {
        return lastDay;
    }
}