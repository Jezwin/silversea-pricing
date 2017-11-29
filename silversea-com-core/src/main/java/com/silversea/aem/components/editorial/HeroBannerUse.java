package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.Button;
import com.silversea.aem.constants.SscConstants;

public class HeroBannerUse extends WCMUsePojo {

	private Button btn1;
	private Button btn2;

	private String suffixUrl;
	private String selectorUrl;

	@Override
	public void activate() throws Exception {
		btn1 = getButton("button1");
		btn2 = getButton("button2");
		
		if (btn1.getAnalyticType().equalsIgnoreCase("clic-RAQ") || btn2.getAnalyticType().equalsIgnoreCase("clic-RAQ")) {
			String pageCategory = getPageProperties().get(SscConstants.PAGE_CATEGORY.toString(), String.class);
			switch (pageCategory) {
			case "single ship":
				this.selectorUrl = SscConstants.SINGLE_SHIP_SUFFIX.toString();
				this.suffixUrl = getPageProperties().get(SscConstants.SINGLE_SHIP_ID.toString(), String.class) + ".html";
				break;
			case "single destination":
				this.selectorUrl = SscConstants.SINGLE_DESTINATION_SUFFIX.toString();
				this.suffixUrl = getPageProperties().get(SscConstants.SINGLE_DESTINATION_ID.toString(),String.class) + ".html";
				break;
			}
		} 

	}

	/**
	 * @return Button
	 */
	private Button getButton(String path) {
		final Resource res = getResource().getChild(path);

		if (res != null) {
			final ValueMap properties = res.getValueMap();

			return new Button(properties.get("titleDesktop", String.class),
					properties.get("titleTablet", String.class), properties.get("reference", String.class),
					properties.get("color", String.class), properties.get("analyticType", String.class),
					properties.get("size", String.class));
		}

		return null;
	}

	/**
	 * @return the btn1
	 */
	public Button getBtn1() {
		return btn1;
	}

	/**
	 * @return the btn2
	 */
	public Button getBtn2() {
		return btn2;
	}

	public String getSuffixUrl() {
		return suffixUrl;
	}

	public String getSelectorUrl() {
		return selectorUrl;
	}

}