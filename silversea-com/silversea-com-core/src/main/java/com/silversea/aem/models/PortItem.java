package com.silversea.aem.models;

public class PortItem {

	private String name;

	private String title;


	public PortItem(String name, String title ) {
		this.name = name;
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

}