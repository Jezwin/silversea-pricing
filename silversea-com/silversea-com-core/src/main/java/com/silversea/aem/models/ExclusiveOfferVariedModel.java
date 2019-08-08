package com.silversea.aem.models;

import org.apache.sling.models.annotations.Model;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.ExclusiveOfferModel;

@Model(adaptables = Page.class)
public class ExclusiveOfferVariedModel extends ExclusiveOfferModel {
	
	private String variedDescription;
	private String variedTitle;
	
	public void setVariedDescription(String desc){
		variedDescription = desc;
	}
	
	public String getVariedDescription() {
        return variedDescription;
    }

	public void setVariedTitle(String title){
		variedTitle = title;
	}
	
	public String getVariedTitle() {
        return variedTitle;
    }
}
