package com.silversea.aem.filter;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

/**
 * TODO constants
 *
 * Created by aurelienolivier on 17/04/2017.
 */
public class BlogPostPageFilter extends PageFilter {

    @Override
    public boolean includes(Page page) {
        boolean included = super.includes(page);

        return included && page.getContentResource().getResourceType()
                .equals("silversea/silversea-com/components/pages/blogpost");
    }
}
