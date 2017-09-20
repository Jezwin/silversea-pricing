package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

/**
 * Created by mbennabi on 20/02/2017.
 */
@Model(adaptables = Page.class)
public class BlogPostModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(BlogPostModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION)
    @Optional
    private String description;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/publicationDate")
    @Optional
    private Calendar publicationDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/image/fileReference")
    @Optional
    private String thumbnail;

    private String path;

    private Page next;

    private Page previous;

    List<Page> listBlog;

    @PostConstruct
    private void init() {
        path = page.getPath();

        try {
            listBlog = new ArrayList<>();
            Iterator<Page> childs = page.getParent().listChildren();
            while (childs.hasNext()) {
                listBlog.add(childs.next().adaptTo(Page.class));
            }
            int i = listBlog.indexOf(page);

            if (i + 1 < listBlog.size() && i > 0) {
                next = listBlog.get(i + 1);
                previous = listBlog.get(i - 1);
            }

            if (i + 1 >= listBlog.size() && i > 0) {
                next = null;
                previous = listBlog.get(i - 1);
            }
            if (i + 1 < listBlog.size() && i <= 0) {
                next = listBlog.get(i + 1);
                previous = null;
            }
        } catch (RuntimeException e) {
            LOGGER.error("Error while initializing model {}", e);
        }
    }

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the longDescription
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * @return the publicationDate
     */
    public Calendar getPublicationDate() {
        return publicationDate;
    }

    /**
     * @return the assetSelectionReference
     */
    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    /**
     * @return the thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
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

    /**
     * @return the listBlog
     */
    public List<Page> getListBlog() {
        return listBlog;
    }
}