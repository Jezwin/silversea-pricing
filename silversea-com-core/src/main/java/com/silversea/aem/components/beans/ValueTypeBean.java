package com.silversea.aem.components.beans;

public class ValueTypeBean {
	private String value;
	private String type;
	
	public ValueTypeBean(String value, String type) {
		this.value = value;
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getType() {
		return type;
	}

}
