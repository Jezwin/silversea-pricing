package com.silversea.ssc.aem.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.commons.json.JSONObject;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.silversea.ssc.aem.bean.SliderBean;
import com.silversea.ssc.aem.bean.SliderItem;

/**
 * Banner Slider for the port page.
 * 
 * @author nikhil
 *
 */
public class BannerSliderUse extends WCMUsePojo {
	private SliderBean bannerBean;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.silversea.aem.components.editorial.FindYourCruiseUse#activate()
	 */
	@Override
	public void activate() throws Exception {
		bannerBean = new SliderBean();
		List<Asset> sliderItems = new ArrayList<>();
		final String[] items = getResource().getValueMap().get("galleryItem",
				String[].class);
		for (String item : items) {
			SliderItem sliderItem = new SliderItem();
			JSONObject jsonObject = new JSONObject(item);
			String reference = jsonObject.getString("assetReference");
			// for initial release, add only images to the slider items
			if (null != getResourceResolver().getResource(reference)
					&& null != getResourceResolver().getResource(reference).adaptTo(Asset.class)
					&& getResourceResolver().getResource(reference).adaptTo(Asset.class).getMimeType()
							.contains("image")) 
			{
				sliderItems.add(getResourceResolver().getResource(reference).adaptTo(Asset.class));
			}
		}
		bannerBean.setSliderItems(sliderItems);
		bannerBean.setShowFirstItemOnly(getResource().getValueMap()
				.get("showFirstItemOnly", Boolean.class));
	}

	public SliderBean getBannerBean() {
		return bannerBean;
	}
}
