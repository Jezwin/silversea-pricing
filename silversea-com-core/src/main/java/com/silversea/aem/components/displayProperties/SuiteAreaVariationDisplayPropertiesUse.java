package com.silversea.aem.components.displayProperties;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

/**
 * Created by mbennabi on 13/03/2017.
 */
public class SuiteAreaVariationDisplayPropertiesUse extends WCMUsePojo {

    Resource area;

    public void activate() throws Exception {

        Page currentPage = getCurrentPage();
        String pathArea = currentPage.getProperties().get("suiteReference", String.class);
        if (StringUtils.isNotEmpty(pathArea)) {
            area = getResourceResolver().getResource(pathArea);
        }
    }

    public Page getAreaPage() {
        return area.adaptTo(Page.class);
    }
}
