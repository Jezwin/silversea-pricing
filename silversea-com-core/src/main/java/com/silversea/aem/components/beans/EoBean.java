package com.silversea.aem.components.beans;

import com.silversea.aem.models.ExclusiveOfferFareModel;

public class EoBean {
	
	private String title;
	private String description;
	private String shortDescription;
	private String footnote;
	private String mapOverhead;
	private ExclusiveOfferFareModel[] cruiseFares;
	private String image;
	private boolean greyBoxesSystem;
	
	public EoBean() {}
	
	public EoBean(String title, String description) {
		this.title = title;
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	public String getFootnote() {
		return footnote;
	}
	public void setFootnote(String footnote) {
		this.footnote = footnote;
	}
	
	public String getMapOverhead() {
		return mapOverhead;
	}
	public void setMapOverhead(String mapOverhead) {
		this.mapOverhead = mapOverhead;
	}
	
	public ExclusiveOfferFareModel[] getCruiseFares() {
		return cruiseFares;
	}
	public void setCruiseFares(ExclusiveOfferFareModel[] cruiseFares) {
		this.cruiseFares = cruiseFares;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isGreyBoxesSystem() {
		return greyBoxesSystem;
	}

	public void setGreyBoxesSystem(boolean greyBoxesSystem) {
		this.greyBoxesSystem = greyBoxesSystem;
	}
	
}