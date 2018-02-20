package com.silversea.aem.components.beans;

public class EoConfigurationBean {
	
	private boolean activeSystem;

	private boolean titleMain;
	private boolean descriptionMain;
	private boolean shortDescriptionMain;
	
	private boolean descriptionTnC;

	public boolean isActiveSystem() {
		return activeSystem;
	}

	public void setActiveSystem(boolean activeSystem) {
		this.activeSystem = activeSystem;
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
	

}
