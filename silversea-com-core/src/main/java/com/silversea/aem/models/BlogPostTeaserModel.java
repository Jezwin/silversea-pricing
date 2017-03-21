package com.silversea.aem.models;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

/**
 * Created by mbennabi on 20/02/2017.
 */
@Model(adaptables = Page.class)
public class BlogPostTeaserModel {
	@Inject
	@Self
	private Page page;

	@Inject
	@Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
	private String title;

	@Inject
	@Named(JcrConstants.JCR_CONTENT + "/longDescription")
	@Optional
	private String longDescription;

	@Inject
	@Named(JcrConstants.JCR_CONTENT + "/publicationDate")
	@Optional
	private Date publicationDate;

	@Inject
	@Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
	@Optional
	private String assetSelectionReference;

	// @Inject
	// @Named(JcrConstants.JCR_CONTENT + "/blogPostReference")
	// @Optional
	// private String blogPostReference;

	@Inject
	@Self
	private String blogPostReference;

	@PostConstruct
	private void init() {

	}

	public Page getPage() {
		return page;
	}

	public String getTitle() {
		return title;
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

	public String getBlogPostReference() {
		return blogPostReference;
	}

}
