package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.BlogPostTeaserModel;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class BlogPostTeaserUse extends WCMUsePojo {

	private static final String PROPERTY_BLOG_POST_REFERENCE_PARAM = "blogPostReference";

	private BlogPostTeaserModel blogTeaser;

	@Override
	public void activate() throws Exception {
		ValueMap properties = getProperties();

		try {
			// Map the blogPostReference in Dialog
			String blogPostReferenceParam = properties.get(PROPERTY_BLOG_POST_REFERENCE_PARAM, String.class);
			if (null != blogTeaser) {
				Resource res = getResourceResolver().getResource(blogPostReferenceParam);
				if (null != res) {
					Page targetPage = res.adaptTo(Page.class);
					if (null != targetPage) {
						blogTeaser = targetPage.adaptTo(BlogPostTeaserModel.class);
					}
				}
			}

		} finally {
			properties = null;
		}

	}

	public BlogPostTeaserModel getBlogTeaser() {
		return blogTeaser;
	}

}