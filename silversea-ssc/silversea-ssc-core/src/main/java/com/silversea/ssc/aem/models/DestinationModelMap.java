package com.silversea.ssc.aem.models;

import java.util.HashMap;
import java.util.Map;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by mbennabi on 17/02/2017.
 */
@Model(adaptables = Page.class)
public class DestinationModelMap {

    static final private Logger LOGGER = LoggerFactory.getLogger(DestinationModelMap.class);
  
    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/mapLabel")
    @Optional
    private String mapLabel;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/excerpt")
    @Optional
    private String excerpt;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/destinationId")
    @Optional
    private Integer destinationId;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/footnote")
    @Optional
    private String footnote;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetselectionreference")
    @Optional
    private String assetselectionreference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/category")
    @Optional
    private String category;


    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customHtml")
    @Optional
    private String customHtml;
    
    public double xPosition;
    
    public double yPosition;
    
    private String path;


    @PostConstruct
    private void init() {
    	//Init of map position - manual - should be included in destinationTemplate after
    	switch (destinationId) {
    	case 3://Africa
    		xPosition =  52.6;
    		yPosition = 57.5;
    		break;
    		
    	case 10://Antarctica
    		xPosition =  28.5;
    		yPosition = 85.7;
    		break;
    		
    	case 9://Alaska
    		xPosition =  6;
    		yPosition = 9;
    		break;

    	case 28://America
    		xPosition =  8;
    		yPosition = 27.7;
    		break;

    	case 11://Arctic
    		xPosition =  38.5;
    		yPosition = 3.2;
    		break;
    		
    	case 13://Asia
    		xPosition =  77.6;
    		yPosition = 41.5;
    		break;
	
    	case 25://Australia
    		xPosition =  82.6;
    		yPosition = 68;
    		break;
        
    	case 5://Canada
    		xPosition =  22.8;
    		yPosition = 15.7;
    		break;
        
    	case 6://Caribbean
    		xPosition =  19;
    		yPosition = 39.8;
    		break;
        	
    	case 23://Galapagos
    		xPosition =  8.7;
    		yPosition = 53.7;
    		break;
           
    	case 1://Med
    		xPosition =  47.2;
    		yPosition = 31.5;
    		break;
           
    	case 26://South Pacific
    		xPosition =  91.5;
    		yPosition = 52;
    		break;
    		
    	case 2://Northern Europe
    		xPosition =  49.8;
    		yPosition = 13.7;
    		break;
    		
    	case 21://Russian
    		xPosition =  84.8;
    		yPosition = 11.3;
    		break;
        
    	case 7://South America
    		xPosition =  25.2;
    		yPosition = 61.7;
    		break;

    	case 8://Transoceanic
    		xPosition =  31;
    		yPosition = 33.8;
    		break;
        
    		
    	default:
    		break;
    	}
    	
    	path = page.getPath();

    }

    public String getTitle() {
        return title;
    }

    public String getMapLabel() {
        return mapLabel;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public String getLongDescription() { return longDescription; }

    public String getFootnote() { return footnote; }

    public String getAssetselectionreference() { return assetselectionreference; }

    public String getCategory() { return category; }

    public String getCustomHtml() { return customHtml; }
    
    public String getXPosition() { return Double.toString(xPosition); }

    public String getYPosition() { return Double.toString(yPosition);  }
    
    public String getPath() {
        return path;
    }
    
    public Page getPage(){
    	return page;
    }
}
