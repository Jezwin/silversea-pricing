package com.silversea.aem.services;

import com.silversea.aem.components.beans.SearchParameter;
import com.silversea.aem.components.beans.SearchResultData;

public interface CruiseSearchService {
    
    /**
     * Search cruise by criteria
     * @param searchParameter: list of research filters
     * @return SearchResultData: search result data
     */
    SearchResultData search(SearchParameter searchParameter);
}
