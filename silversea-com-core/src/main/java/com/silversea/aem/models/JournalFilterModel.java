package com.silversea.aem.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

/**
 * Created by mbennabi on 20/02/2017.
 */
@Model(adaptables = Page.class)
public class JournalFilterModel {
    
    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    
    @PostConstruct
    private void init() {
    }

	public String getTitle() {
		return title;
	}


}
