package com.silversea.aem.components.displayProperties;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

/**
 * Created by mbennabi on 13/03/2017.
 */
public class PublicAreaVariationDisplayPropertiesUse extends WCMUsePojo {

    private Page area;

    public void activate() throws Exception {
        final String pathArea = getPageProperties().get("publicAreaReference", String.class);

        if (StringUtils.isNotEmpty(pathArea)) {
            area = getPageManager().getPage(pathArea);
        }
    }

    public Page getAreaPage() {
        return area;
    }
}
