package com.silversea.aem.services;

import java.util.List;

import com.silversea.aem.models.BlogPostModel;
import com.silversea.aem.models.BlogPostTeaserModel;

public interface BlogPostService {

	<T extends BlogPostModel> List<T> getBlogPostModel(String parentPath, String propertyKey, String propertyValue,
			String sortBy);

	<T extends BlogPostTeaserModel> List<T> getBlogPostTeaserModel(String parentPath, String propertyKey,
			String propertyValue, String sortBy);

	<T extends BlogPostModel> List<T> getBlogPostModel(String pagePath);

}
