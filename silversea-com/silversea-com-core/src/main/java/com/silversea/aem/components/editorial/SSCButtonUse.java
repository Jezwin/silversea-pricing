package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.helper.UrlHelper;

public class SSCButtonUse extends WCMUsePojo {

	private String suffixUrl;
	private String selectorUrl;

	@Override
	public void activate() throws Exception {
		String analyticType = getProperties().get("analyticType", String.class);
		if (analyticType != null && analyticType.equalsIgnoreCase("clic-RAQ")) {
			Page currentPage = getCurrentPage();
			String[] selectorSuffixUrl = UrlHelper.createSuffixAndSelectorUrl(currentPage);
			this.selectorUrl = selectorSuffixUrl[0];
			this.suffixUrl = selectorSuffixUrl[1];
		}
	}

	public String getSelectorUrl() {
		return selectorUrl;
	}

	public String getSuffixUrl() {
		return suffixUrl;
	}
}
