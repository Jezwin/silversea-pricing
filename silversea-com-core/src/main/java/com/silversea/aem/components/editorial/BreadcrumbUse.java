package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.foundation.Navigation;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;

import java.util.List;

public class BreadcrumbUse extends WCMUsePojo {
    public List<Page> breadCrumbList;

    @Override
    public void activate() throws Exception {

    }

    /**
     * @return the archives
     */
    public List<Page> getBreadCrumbList() {
        return breadCrumbList;
    }
}