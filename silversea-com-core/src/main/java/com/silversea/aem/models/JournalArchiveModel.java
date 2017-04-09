package com.silversea.aem.models;

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
public class JournalArchiveModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(JournalArchiveModel.class);

    @Inject
    @Self
    private Page page;
    
    @Inject
    @Self
    private String archivePath;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    private String path;

    private List<JournalArchiveMonthModel> archiveMonth;
    
    @PostConstruct
    private void init() {
        path = page.getPath();
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return (path != null && path.startsWith("/content") && !path.endsWith(".html") ? path + ".html" : path);
    }

    public List<JournalArchiveMonthModel> getArchiveMonth() {
        return archiveMonth;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setArchiveMonth(List<JournalArchiveMonthModel> archiveMonth) {
        this.archiveMonth = archiveMonth;
    }

    public String getArchivePath() {
        return archivePath;
    }

}
