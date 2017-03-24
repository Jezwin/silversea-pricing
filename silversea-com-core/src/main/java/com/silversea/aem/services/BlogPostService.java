package com.silversea.aem.services;

import java.util.List;

import com.silversea.aem.models.BlogPostModel;
import com.silversea.aem.models.BlogPostTeaserModel;

public interface BlogPostService {


	<T extends BlogPostTeaserModel> List<T> getBlogPostTeaserModelList(String path, String propertyKey,
			String propertyValue, String sortBy);

}
