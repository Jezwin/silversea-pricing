package com.silversea.aem.helper;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

public class PageHelper extends WCMUsePojo {
    private Page page;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);
        Resource resource = getResourceResolver().getResource(path);
        if (resource != null) {
            page = resource.adaptTo(Page.class);
        }
    }

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    /**
     * @return the homePage
     */
    public Page getHomePage() {
        return getCurrentPage().getAbsoluteParent(2);
    }

}