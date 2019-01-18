package com.silversea.aem.models;

public class PortItem {

	private String name;

	private String title;


	private String countryISO3;

	public PortItem(String name, String title, String countryISO3 ) {
		this.name = name;
		this.title = title;
		this.countryISO3 = countryISO3;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getCountryISO3() {
		return countryISO3;
	}
}