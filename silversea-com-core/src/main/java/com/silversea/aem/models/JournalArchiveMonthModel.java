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
public class JournalArchiveMonthModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(JournalArchiveMonthModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    private String path;
    
    private int monthNumber;

    @PostConstruct
    private void init() {
        path = page.getPath();
        Iterator<Page> contMonth = page.listChildren();
        monthNumber = 0;
        while (contMonth.hasNext()) {
            monthNumber = monthNumber+1;
            contMonth.next();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return (path != null && path.startsWith("/content") && !path.endsWith(".html") ? path + ".html" : path);
    }

    public Page getPage() {
        return page;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

}
