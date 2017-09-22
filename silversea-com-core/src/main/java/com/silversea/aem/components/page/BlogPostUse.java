package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.BlogPostModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlogPostUse extends WCMUsePojo {

    private BlogPostModel post;

    private Page next;

    private Page previous;

    private List<Page> posts = new ArrayList<>();

    @Override
    public void activate() throws Exception {
        post = getCurrentPage().adaptTo(BlogPostModel.class);

        final Iterator<Page> childs = getCurrentPage().getParent().listChildren();
        while (childs.hasNext()) {
            posts.add(childs.next().adaptTo(Page.class));
        }

        int i = posts.indexOf(getCurrentPage());

        if (i + 1 < posts.size() && i > 0) {
            next = posts.get(i + 1);
            previous = posts.get(i - 1);
        }

        if (i + 1 >= posts.size() && i > 0) {
            next = null;
            previous = posts.get(i - 1);
        }

        if (i + 1 < posts.size() && i <= 0) {
            next = posts.get(i + 1);
            previous = null;
        }
    }

    public BlogPostModel getBlog() {
        return post;
    }

    /**
     * @return the next
     */
    public Page getNext() {
        return next;
    }

    /**
     * @return the previous
     */
    public Page getPrevious() {
        return previous;
    }
}