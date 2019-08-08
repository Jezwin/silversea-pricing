package com.silversea.ssc.aem.bean;

public class PortGlossaryBean {
	private String name;
	private String path;
	private String destinations;

	public PortGlossaryBean(String name, String path, String destinations) {
		this.name = name;
		this.path = path;
		this.destinations = destinations;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDestinations() {
		return destinations;
	}

	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}

}
