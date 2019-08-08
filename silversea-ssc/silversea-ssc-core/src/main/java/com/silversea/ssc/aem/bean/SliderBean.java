package com.silversea.ssc.aem.bean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.day.cq.dam.api.Asset;

public class SliderBean {
	private String description;
	private boolean showFirstItemOnly = false;
	private List<Asset> sliderItems = new ArrayList<Asset>();
	private LinkedHashMap<String, List<Asset>> modalItems = new LinkedHashMap<String, List<Asset>>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isShowFirstItemOnly() {
		return showFirstItemOnly;
	}

	public void setShowFirstItemOnly(boolean showFirstItemOnly) {
		this.showFirstItemOnly = showFirstItemOnly;
	}

	public List<Asset> getSliderItems() {
		return sliderItems;
	}

	public void setSliderItems(List<Asset> sliderItems) {
		this.sliderItems = sliderItems;
	}

	public LinkedHashMap<String, List<Asset>> getModalItems() {
		return modalItems;
	}

	public void setModalItems(LinkedHashMap<String, List<Asset>> modalItems) {
		this.modalItems = modalItems;
	}
}
