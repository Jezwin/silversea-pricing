package com.silversea.aem.components.beans;

public class EoConfigurationBean {
	
	private boolean activeSystem;

	private boolean titleMain;
	private boolean descriptionMain;
	private boolean shortDescriptionMain;
	
	private boolean activeGreysSystem;
	private boolean imageLightbox;
	private boolean titleLigthbox;
	private boolean descriptionLigthbox;
	
	private boolean titleVoyage;
	private boolean descriptionVoyage;
	
	private boolean descriptionTnC;

	public boolean isActiveSystem() {
		return activeSystem;
	}

	public void setActiveSystem(boolean activeSystem) {
		this.activeSystem = activeSystem;
	}

	public boolean isTitleLigthbox() {
		return titleLigthbox;
	}

	public void setTitleLigthbox(boolean titleLigthbox) {
		this.titleLigthbox = titleLigthbox;
	}

	public boolean isDescriptionLigthbox() {
		return descriptionLigthbox;
	}

	public void setDescriptionLigthbox(boolean descriptionLigthbox) {
		this.descriptionLigthbox = descriptionLigthbox;
	}

	public boolean isTitleVoyage() {
		return titleVoyage;
	}

	public void setTitleVoyage(boolean titleVoyage) {
		this.titleVoyage = titleVoyage;
	}

	public boolean isDescriptionVoyage() {
		return descriptionVoyage;
	}

	public void setDescriptionVoyage(boolean descriptionVoyage) {
		this.descriptionVoyage = descriptionVoyage;
	}

	public boolean isTitleMain() {
		return titleMain;
	}

	public void setTitleMain(boolean titleMain) {
		this.titleMain = titleMain;
	}

	public boolean isDescriptionMain() {
		return descriptionMain;
	}

	public void setDescriptionMain(boolean descriptionMain) {
		this.descriptionMain = descriptionMain;
	}

	public boolean isShortDescriptionMain() {
		return shortDescriptionMain;
	}

	public void setShortDescriptionMain(boolean shortDescriptionMain) {
		this.shortDescriptionMain = shortDescriptionMain;
	}

	public boolean isDescriptionTnC() {
		return descriptionTnC;
	}

	public void setDescriptionTnC(boolean descriptionTnC) {
		this.descriptionTnC = descriptionTnC;
	}
	
	public boolean isActiveGreysSystem() {
		return activeGreysSystem;
	}

	public void setActiveGreysSystem(boolean activeGreysSystem) {
		this.activeGreysSystem = activeGreysSystem;
	}

	public boolean isImageLightbox() {
		return imageLightbox;
	}

	public void setImageLightbox(boolean imageLightbox) {
		this.imageLightbox = imageLightbox;
	}
	

}
