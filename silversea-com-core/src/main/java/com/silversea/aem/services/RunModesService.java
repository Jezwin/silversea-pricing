package com.silversea.aem.services;

import com.silversea.aem.models.BlogPostTeaserModel;

import java.util.List;


public interface RunModesService {

    public boolean isAuthor();
    
    public boolean isPublish();
    
    public String getCurrentRunMode();

}
