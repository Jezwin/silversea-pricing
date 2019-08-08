package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SideMenuUse extends WCMUsePojo {

    private List<Page> subPage = new ArrayList<>();

    @Override
    public void activate() throws Exception {
        final String rootPagePath = getProperties().get("reference", getCurrentPage().getParent().getPath());
        final Page rootPage = getPageManager().getPage(rootPagePath);

        if (rootPage != null) {
            final Iterator<Page> it = rootPage.listChildren(new PageFilter());

            while (it.hasNext()) {
                subPage.add(it.next());
            }
        }
    }

    /**
     * @return the subpages
     */
    public List<Page> getSubPage() {
        return subPage;
    }
}