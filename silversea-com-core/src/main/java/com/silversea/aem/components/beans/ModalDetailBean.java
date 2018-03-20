package com.silversea.aem.components.beans;

import com.silversea.aem.models.PriceModel;

public class ModalDetailBean {

	private String title;
	private String longDescription;
	private String assetSelectionReference;
	private String bedroomsInformation;
	private String plan;
	private String locationImage;
	private String virtualTour;
	private String[] features;
	private String fileReference;
	private String shipId;
	private PriceModel lowestPrice;
	private String computedPriceFormated;
	private boolean isWaitList;
	private String currency;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	public String getAssetSelectionReference() {
		return assetSelectionReference;
	}
	public void setAssetSelectionReference(String assetSelectionReference) {
		this.assetSelectionReference = assetSelectionReference;
	}
	public String getBedroomsInformation() {
		return bedroomsInformation;
	}
	public void setBedroomsInformation(String bedroomsInformation) {
		this.bedroomsInformation = bedroomsInformation;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getLocationImage() {
		return locationImage;
	}
	public void setLocationImage(String locationImage) {
		this.locationImage = locationImage;
	}
	public String getVirtualTour() {
		return virtualTour;
	}
	public void setVirtualTour(String virtualTour) {
		this.virtualTour = virtualTour;
	}
	public String[] getFeatures() {
		return features;
	}
	public void setFeatures(String[] features) {
		this.features = features;
	}
	public String getFileReference() {
		return fileReference;
	}
	public void setFileReference(String fileReference) {
		this.fileReference = fileReference;
	}
	public String getShipId() {
		return shipId;
	}
	public void setShipId(String shipId) {
		this.shipId = shipId;
	}
	public PriceModel getLowestPrice() {
		return lowestPrice;
	}
	public void setLowestPrice(PriceModel lowestPrice) {
		this.lowestPrice = lowestPrice;
	}
	public String getComputedPriceFormated() {
		return computedPriceFormated;
	}
	public void setComputedPriceFormated(String computedPriceFormated) {
		this.computedPriceFormated = computedPriceFormated;
	}
	public boolean isWaitList() {
		return isWaitList;
	}
	public void setWaitList(boolean isWaitList) {
		this.isWaitList = isWaitList;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
