package com.silversea.aem.components.editorial;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.Button;
import com.silversea.aem.helper.UrlHelper;
import com.silversea.aem.utils.AssetUtils;

public class HeroBannerUse extends WCMUsePojo {

	private Button btn1;
	private Button btn2;

	private String suffixUrl;
	private String selectorUrl;

	private boolean isFirstElement;

	private String imageBackgroundPath;

	@Override
	public void activate() throws Exception {
		
		String assetReference = getProperties().get("assetReference", String.class);
		Resource resourceAsset = getResourceResolver().getResource(assetReference);
        if (resourceAsset != null) {
        	Asset asset = resourceAsset.adaptTo(Asset.class);
        	if(asset !=null) {
        		if (!DamUtil.isImage(asset)) {
        			String dcFormat = asset.getMetadata().get(DamConstants.DC_FORMAT) != null ? asset.getMetadata().get(DamConstants.DC_FORMAT).toString() : null;
        			if (dcFormat.contains(S7damConstants.S7_MIXED_MEDIA_SET)) {
        				List<Asset> assetlist = AssetUtils.buildAssetList(assetReference, getResourceResolver());
        				if (assetlist.size() > 0) {
        					this.imageBackgroundPath = assetlist.get(0).getPath();
        				}
        			}
        		} else {
        			this.imageBackgroundPath = assetReference;
        		}
        	}
        }
		
        this.isFirstElement = true;
		if (isInsideSlider(getResource())) {
			Resource resourceParent = getResource().getParent();
			if (resourceParent.getChildren() != null && resourceParent.getChildren().iterator().hasNext()) {
				Resource firstResourceChild =   resourceParent.getChildren().iterator().next();
				isFirstElement = firstResourceChild.getPath().equalsIgnoreCase(getResource().getPath());
			}
			
		}
		btn1 = getButton("button1");
		btn2 = getButton("button2");

		if ((btn1 != null && btn1.getAnalyticType() != null && btn1.getAnalyticType().equalsIgnoreCase("clic-RAQ"))
				|| (btn2 != null && btn2.getAnalyticType() != null
						&& btn2.getAnalyticType().equalsIgnoreCase("clic-RAQ"))) {
			Page currentPage = getCurrentPage();
			String[] selectorSuffixUrl = UrlHelper.createSuffixAndSelectorUrl(currentPage);
			this.selectorUrl = selectorSuffixUrl[0];
			this.suffixUrl = selectorSuffixUrl[1];
		}

	}

	private boolean isInsideSlider(Resource resource) {
		if (resource != null) {
			Resource resourceParent = resource.getParent();
			if (resourceParent != null) {
				if (resourceParent.getResourceType()
						.equalsIgnoreCase("silversea/silversea-com/components/editorial/slider")) {
					return true;
				} else if (resourceParent.getResourceType()
						.equalsIgnoreCase("wcm/foundation/components/responsivegrid")) { // security check to not iterate until the root
					return false;
				}
			}
			return isInsideSlider(resourceParent);
		}
		return false;
	}

	/**
	 * @return Button
	 */
	private Button getButton(String path) {
		final Resource res = getResource().getChild(path);

		if (res != null) {
			final ValueMap properties = res.getValueMap();

			return new Button(properties.get("titleDesktop", String.class), properties.get("titleTablet", String.class),
					properties.get("reference", String.class), properties.get("color", String.class),
					properties.get("analyticType", String.class), properties.get("size", String.class));
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

	public boolean getIsFirstElement() {
		return isFirstElement;
	}

	public String getImageBackgroundPath() {
		return imageBackgroundPath;
	}
}