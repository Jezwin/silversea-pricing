package com.silversea.ssc.aem.components.editorial;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.silversea.aem.utils.AssetUtils;

public class HeroBannerDOCUse extends WCMUsePojo {

	static final private Logger LOGGER = LoggerFactory.getLogger(HeroBannerDOCUse.class);




	private String desktopBackground;
	private String mobileBackground;
	private String tabletBackground;
	private String imageDesktopBackgroundPath;
	private String imageTabletBackgroundPath;
	private String imageMobilepBackgroundPath;	






	private String desktopHeight;
	private String tabletHeight;
	private String mobileHeight;




	@Override
	public void activate() throws Exception {


		if (StringUtils.isEmpty(this.desktopBackground)) {
			this.desktopBackground = getProperties().get("desktopAssetReference", String.class);
		}
		if (StringUtils.isEmpty(this.mobileBackground)) {
			this.mobileBackground = getProperties().get("mobileAssetReference", String.class);
		}
		if (StringUtils.isEmpty(this.tabletBackground)) {
			this.tabletBackground = getProperties().get("tabletAssetReference", String.class);
		}		


		if (StringUtils.isEmpty(this.desktopHeight)) {
			this.desktopHeight = getProperties().get("desktopHeight", String.class)  + getProperties().get("heightTypeDesktop", String.class) ;
		}

		if (StringUtils.isEmpty(this.tabletHeight)) {
			this.tabletHeight = getProperties().get("tabletHeight", String.class) + getProperties().get("heightTypeTablet", String.class) ;
		}

		if (StringUtils.isEmpty(this.mobileHeight)) {
			this.mobileHeight = getProperties().get("mobileHeight", String.class) + getProperties().get("heightTypeMobile", String.class) ;
		}
		
		this.imageDesktopBackgroundPath = getImagePath(this.desktopBackground);
		this.imageTabletBackgroundPath = getImagePath(this.tabletBackground);
		this.imageMobilepBackgroundPath = getImagePath(this.mobileBackground);

	}

	private String getImagePath(String imgPath) {
		Resource resourceAsset = getResourceResolver().getResource(imgPath);

		if (resourceAsset != null) {
			Asset asset = resourceAsset.adaptTo(Asset.class);
			if (asset != null) {
				if (!DamUtil.isImage(asset)) {
					String dcFormat = asset.getMetadata().get(DamConstants.DC_FORMAT) != null
							? asset.getMetadata().get(DamConstants.DC_FORMAT).toString()
									: null;
							if (dcFormat.contains(S7damConstants.S7_MIXED_MEDIA_SET)) {
								List<Asset> assetlist = AssetUtils.buildAssetList(imgPath, getResourceResolver());
								if (assetlist.size() > 0) {
									return assetlist.get(0).getPath();							
								}
							}
				} else {
					return imgPath;					
				}

			}
		}
		return "";
	}


	public String getImageDesktopBackgroundPath() {
		return imageDesktopBackgroundPath;
	}




	public String getTabletBackground() {
		return tabletBackground;
	}

	public void setTabletBackground(String tabletBackground) {
		this.tabletBackground = tabletBackground;
	}


	public String getBackground() {
		return desktopBackground;
	}


	public String getImageTabletBackgroundPath() {
		return imageTabletBackgroundPath;
	}

	public void setImageTabletBackgroundPath(String imageTabletBackgroundPath) {
		this.imageTabletBackgroundPath = imageTabletBackgroundPath;
	}

	public String getImageMobilepBackgroundPath() {
		return imageMobilepBackgroundPath;
	}

	public void setImageMobilepBackgroundPath(String imageMobilepBackgroundPath) {
		this.imageMobilepBackgroundPath = imageMobilepBackgroundPath;
	}

	public String getDesktopHeight() {
		return desktopHeight;
	}

	public void setDesktopHeight(String desktopHeight) {
		this.desktopHeight = desktopHeight;
	}

	public String getTabletHeight() {
		return tabletHeight;
	}

	public void setTabletHeight(String tabletHeight) {
		this.tabletHeight = tabletHeight;
	}

	public String getMobileHeight() {
		return mobileHeight;
	}

	public void setMobileHeight(String mobileHeight) {
		this.mobileHeight = mobileHeight;
	}




}