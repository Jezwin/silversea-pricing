package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;

public class PageHelper extends WCMUsePojo {

    private Page page;
    private String thumbnail;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);
        Resource resource = getResourceResolver().getResource(path);

        if (resource != null) {
            page = resource.adaptTo(Page.class);

            if(page != null) {
                Resource imageRes = page.getContentResource("image");
                if (imageRes != null) {
                    thumbnail = imageRes.getValueMap().get("fileReference", String.class);
                }
            }
        }
    }

    /**
     * @return the page for a given path
     */
    public Page getPage() {
        return page;
    }

    /**
     * @return the homePage for the current language web site
     */
    public Page getHomePage() {
        return getCurrentPage().getAbsoluteParent(2);
    }

    /**
     * @return the templateName of the current page
     */
    public String getTemplateName() {
        String path = getCurrentPage().getProperties().get(NameConstants.NN_TEMPLATE, String.class);

        if (StringUtils.isEmpty(path)) {
            return "";
        }

        return path.substring(path.lastIndexOf('/') + 1);
    }
    

    /**
     * @return the page for a given path
     */
    public String getThumbnail() {
        return thumbnail;
    }
}