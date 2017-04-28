package com.silversea.aem.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

@Model(adaptables = Page.class)
public class DiningModel {
    
    @Inject
    @Self
    private Page page;
    
    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;
    
    private ResourceResolver resourceResolver;
    
    @PostConstruct
    private void init() {
        resourceResolver = page.getContentResource().getResourceResolver();
        longDescription = initDescription("diningReference", "longDescription","longDescription");
    }

    private String initDescription(String reference, String property,String referenceProperty){    
        if(StringUtils.isEmpty(longDescription)){
            Page page = getPageReference(reference);
            if(page != null){
                longDescription = page.getProperties().get(referenceProperty,String.class);
            }
             
        }
        return longDescription;
    }

    private Page getPageReference(String reference){
        Page pageReference = null;
        String path = page.getProperties().get(reference, String.class);
        Resource resource = resourceResolver.resolve(path);
        if(resource!=null){
            pageReference = resource.adaptTo(Page.class);
        }
        
        return pageReference;
    }


    public String getLongDescription() {
        return longDescription;
    }

    public Page getPage() {
        return page;
    }
}
