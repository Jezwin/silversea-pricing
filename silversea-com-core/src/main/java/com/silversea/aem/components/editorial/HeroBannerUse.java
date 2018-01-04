package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.Button;
import com.silversea.aem.helper.UrlHelper;

public class HeroBannerUse extends WCMUsePojo {

	private Button btn1;
	private Button btn2;

	private String suffixUrl;
	private String selectorUrl;

	@Override
	public void activate() throws Exception {
		btn1 = getButton("button1");
		btn2 = getButton("button2");
		
		if ((btn1 !=null && btn1.getAnalyticType().equalsIgnoreCase("clic-RAQ")) || (btn2 != null && btn2.getAnalyticType().equalsIgnoreCase("clic-RAQ"))) {			Page currentPage = getCurrentPage();
			String[] selectorSuffixUrl = UrlHelper.createSuffixAndSelectorUrl(currentPage);
			this.selectorUrl = selectorSuffixUrl[0];
			this.suffixUrl = selectorSuffixUrl[1];
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