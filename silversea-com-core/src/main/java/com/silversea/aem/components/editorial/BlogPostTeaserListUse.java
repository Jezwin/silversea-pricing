package com.silversea.aem.components.editorial;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.filter.BlogPostPageFilter;
import com.silversea.aem.models.BlogPostTeaserModel;

public class BlogPostTeaserListUse extends WCMUsePojo {

    private List<BlogPostTeaserModel> blogPostTeaserModelList = new ArrayList<>();

    private BlogPostTeaserModel firstBlogPostTeaser;

    private Boolean highlightFirst;
    
    private Integer year;
    
    private Integer dayInMonth;

    @Override
    public void activate() throws Exception {
        String blogPostReference = getProperties().get("blogPostReference", String.class);
        highlightFirst = getProperties().get("highLightFirst", false);

        Page page = blogPostReference != null ? getPageManager().getPage(blogPostReference) : getCurrentPage();

        if (page != null) {
            Iterator<Page> blogPostPages = page.listChildren(new BlogPostPageFilter(), true);

            int i = 0;
            while (blogPostPages.hasNext() && i < 15) {
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
        
        String parentPageName = getCurrentPage().getParent().getName();
        if(parentPageName.matches("\\d+")){        	
        	year = Integer.parseInt(parentPageName);

        	String pageName = getCurrentPage().getName();
        	if(pageName.matches("\\d+")){          		
        		dayInMonth = YearMonth.of(year, Integer.parseInt(pageName)).lengthOfMonth();
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
    
    public Integer getYear() {
        return year;
    }
    
    public Integer getDayInMonth() {
        return dayInMonth;
    }
}