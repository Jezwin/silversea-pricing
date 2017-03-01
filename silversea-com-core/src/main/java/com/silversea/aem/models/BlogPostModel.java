package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Date;
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
    
    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;
    
    @Inject @Named(JcrConstants.JCR_CONTENT + "/publicationDate") @Optional
    private Date publicationDate;
    
    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String assetSelectionReference;
    
    private Page next;

    private Page previous;

    List<Page> listBlog;

    @PostConstruct
    private void init() {
       
        listBlog = new ArrayList<>();
        Iterator<Page> childs = page.getParent().listChildren();
        while (childs.hasNext()) {
            listBlog.add(childs.next().adaptTo(Page.class));
        }
        int i = listBlog.indexOf(page);
        
        if (i+1 < listBlog.size() && i>0) {
            next = listBlog.get(i + 1);
            previous = listBlog.get(i - 1);
        }
        
        if(i+1 >= listBlog.size() && i>0){
            next = null;
            previous = listBlog.get(i - 1);
        }
        if(i+1 < listBlog.size() && i<=0){
            next = listBlog.get(i + 1);
            previous = null;
            
        }
        
        

    }

    public String getTitle() {
        return title;
    }

    public Page getNext() {
        return next;
    }

    public void setNext(Page next) {
        this.next = next;
    }

    public Page getPrevious() {
        return previous;
    }

    public void setPrevious(Page previous) {
        this.previous = previous;
    }

    public List<Page> getListBlog() {
        return listBlog;
    }

    public void setListBlog(List<Page> listBlog) {
        this.listBlog = listBlog;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }
    
}
