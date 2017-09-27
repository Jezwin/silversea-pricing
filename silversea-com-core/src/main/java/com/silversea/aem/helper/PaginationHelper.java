package com.silversea.aem.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

/**
 * Created by asiba on 28/03/2017.
 */
public class PaginationHelper extends WCMUsePojo {

    private Page nextPage;

    private Page previousPage;

    @Override
    public void activate() throws Exception {
        List<Page> siblingPages = new ArrayList<>();
        Iterator<Page> childs = getCurrentPage().getParent().listChildren(new PageFilter());

        while (childs.hasNext()) {
            siblingPages.add(childs.next().adaptTo(Page.class));
        }

        int currentIndex = siblingPages.indexOf(getCurrentPage());

        previousPage = currentIndex == 0 ? null : siblingPages.get(currentIndex - 1);
        nextPage = currentIndex == siblingPages.size() - 1 ? null : siblingPages.get(currentIndex + 1);
    }

    /**
     * @return the next page
     */
    public Page getNextPage() {
        return nextPage;
    }

    /**
     * @return the previous page
     */
    public Page getPreviousPage() {
        return previousPage;
    }
}