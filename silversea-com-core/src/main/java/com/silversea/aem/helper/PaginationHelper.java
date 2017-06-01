package com.silversea.aem.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

/**
 * Created by asiba on 28/03/2017.
 */
public class PaginationHelper extends WCMUsePojo {

    private Page page;

    private List<Page> listPage;

    public Page next;

    public Page previous;

    @Override
    public void activate() throws Exception {
        page = getCurrentPage().adaptTo(Page.class);
        listPage = new ArrayList<>();
        Iterator<Page> childs = page.getParent().listChildren();
        while (childs.hasNext()) {
            listPage.add(childs.next().adaptTo(Page.class));
        }
        int i = listPage.indexOf(page);

        if (i + 1 < listPage.size() && i > 0) {
            next = listPage.get(i + 1);
            previous = listPage.get(i - 1);
        }

        if (i + 1 >= listPage.size() && i > 0) {
            next = null;
            previous = listPage.get(i - 1);
        }
        if (i + 1 < listPage.size() && i <= 0) {
            next = listPage.get(i + 1);
            previous = null;
        }
    }
}
