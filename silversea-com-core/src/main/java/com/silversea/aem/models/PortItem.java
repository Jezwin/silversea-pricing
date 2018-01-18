package com.silversea.aem.models;

public class PortItem {

	private String name;

	private String title;

	private String cityId;
	public PortItem(String name, String title) {
		this.name = name;
		this.title = title;
	}

	public PortItem(String name, String title, String cityId) {
		this.name = name;
		this.title = title;
		this.cityId = cityId;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getCityId() {
		return cityId;
	}

}