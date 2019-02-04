package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.foundation.Navigation;
import com.silversea.aem.models.PageSSCModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;

import java.util.ArrayList;
import java.util.List;

public class BreadcrumbUse extends WCMUsePojo {
    public List<PageSSCModel> breadCrumbList;

    @Override
    public void activate() throws Exception {
        Page currentPage = getCurrentPage();
        breadCrumbList = new ArrayList<>();
        breadCrumbList.add(new PageSSCModel(currentPage));
        Integer currentPageLevel = currentPage.getDepth();
        while(currentPageLevel > 3) {
            currentPage = currentPage.getParent();
            currentPageLevel = currentPage.getDepth();
            if(!currentPage.isHideInNav()) {
                breadCrumbList.add(0, new PageSSCModel(currentPage));
            }
        }

    }

    /**
     * @return the archives
     */
    public List<PageSSCModel> getBreadCrumbList() {
        return breadCrumbList;
    }
}