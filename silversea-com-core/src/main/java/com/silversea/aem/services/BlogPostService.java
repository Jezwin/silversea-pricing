package com.silversea.aem.services;

import com.silversea.aem.models.BlogPostTeaserModel;

import java.util.List;

@Deprecated
public interface BlogPostService {

    <T extends BlogPostTeaserModel> List<T> getBlogPostTeaserModelList(String path, String propertyKey,
                                                                       String propertyValue, String sortBy);

}
