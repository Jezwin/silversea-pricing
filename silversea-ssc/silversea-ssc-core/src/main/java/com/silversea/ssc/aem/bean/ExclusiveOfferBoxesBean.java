package com.silversea.ssc.aem.bean;

import java.util.Map;

import org.apache.sling.api.resource.ValueMap;

import com.silversea.aem.components.beans.ValueTypeBean;

public class ExclusiveOfferBoxesBean {

	private Map<String, ValueTypeBean> tokensAndStyles;
	private Map<String, EoBoxesBean> boxes;
	private ValueMap properties;
	private ValueMap styles;
	private boolean showComponent;
	private String title;
	private String cssDesktop;
	public Map<String, ValueTypeBean> getTokensAndStyles() {
		return tokensAndStyles;
	}

	public void setTokensAndStyles(Map<String, ValueTypeBean> tokensAndStyles) {
		this.tokensAndStyles = tokensAndStyles;
	}

	public ValueMap getProperties() {
		return properties;
	}

	public void setProperties(ValueMap properties) {
		this.properties = properties;
	}

	public ValueMap getStyles() {
		return styles;
	}

	public void setStyles(ValueMap styles) {
		this.styles = styles;
	}

	public void setBoxes(Map<String, EoBoxesBean> boxes) {
		this.boxes = boxes;
	}

	public void setShowComponent(boolean showComponent) {
		this.showComponent = showComponent;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCssDesktop(String cssDesktop) {
		this.cssDesktop = cssDesktop;
	}

	public void setCssTablet(String cssTablet) {
		this.cssTablet = cssTablet;
	}

	public void setCssMobile(String cssMobile) {
		this.cssMobile = cssMobile;
	}

	private String cssTablet;
	private String cssMobile;

	public Map<String, EoBoxesBean> getBoxes() {
		return boxes;
	}

	public ValueMap getPropertiesAdapted() {
		return properties;
	}

	public boolean getShowComponent() {
		return showComponent;

	}

	public String getTitle() {
		return title;
	}

	public String getCssDesktop() {
		return cssDesktop;
	}

	public String getCssTablet() {
		return cssTablet;
	}

	public String getCssMobile() {
		return cssMobile;
	}

}