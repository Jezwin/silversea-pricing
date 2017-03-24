package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

public class SideMenuUse extends WCMUsePojo {
    private List<Page> subPage;

    @Override
    public void activate() throws Exception {
        String rootPagePath = getProperties().get("reference", getCurrentPage().getParent().getPath());

        Resource res = getResourceResolver().resolve(rootPagePath);
        if (res != null) {
            Page rootPage = res.adaptTo(Page.class);
            subPage = new ArrayList<Page>();
            Iterator<Page> it = rootPage.listChildren(new PageFilter());
            while (it.hasNext()) {
                subPage.add(it.next());
            }
        }
    }

    /**
     * @return the subPage
     */
    public List<Page> getSubPage() {
        return subPage;
    }
}