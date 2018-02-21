package com.silversea.aem.components.beans;

public class EoBean {
	
	private String title;
	private String description;
	private String shortDescription;
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