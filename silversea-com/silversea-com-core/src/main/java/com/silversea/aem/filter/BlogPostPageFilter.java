package com.silversea.aem.filter;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.aem.constants.WcmConstants;

public class BlogPostPageFilter extends PageFilter {

    @Override
    public boolean includes(Page page) {
        boolean included = super.includes(page);

        return included && page.getContentResource().getResourceType().equals(WcmConstants.RT_BLOG_POST);
    }
}