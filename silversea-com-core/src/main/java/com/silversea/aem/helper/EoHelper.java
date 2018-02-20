package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.ExclusiveOfferModel;

public class EoHelper extends AbstractGeolocationAwareUse {

	public EoBean parseExclusiveOffer(EoConfigurationBean eoConfig, ExclusiveOfferModel eoModel) {
		EoBean eoBean = null;
		if (eoConfig != null && eoConfig.isActiveSystem() && eoModel != null) {
			eoBean = new EoBean();

			if (eoConfig.isTitleMain()) {
				String title = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "title", eoModel.getDefaultTitle());
				eoBean.setTitle(title);
			}
			if (eoConfig.isDescriptionMain()) {
				String description = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "description",eoModel.getDefaultDescription());
				eoBean.setDescription(description);
			}
			if (eoConfig.isShortDescriptionMain()) {
				String shortDescription = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "shortDescription", eoModel.getDefaultShortDescription());
				eoBean.setShortDescription(shortDescription);
			}
			//-----------------------------------------
			if(eoConfig.isDescriptionTnC()) {
				String description = getValueByBesthMatchTag(eoModel.getCustomTnCSettings(), "description", eoModel.getDefaultDescriptionTnC());
				eoBean.setDescription(description);
			}
			//-----------------------------------------
			if(eoConfig.isTitleVoyage()) {
				String title = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "title", eoModel.getCustomMainSettings(), eoModel.getDefaultTitle());
				eoBean.setDescription(title);
			}
			if(eoConfig.isDescriptionVoyage()) {
				String description = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "description", eoModel.getCustomMainSettings(), eoModel.getDefaultDescription());
				eoBean.setDescription(description);
			}
			//-----------------------------------------
			if(eoConfig.isTitleLigthbox()) {
				String title = getValueByBesthMatchTag(eoModel.getCustomLBSettings(), "title", eoModel.getCustomMainSettings(), eoModel.getDefaultTitle());
				eoBean.setDescription(title);
			}
			if(eoConfig.isDescriptionLigthbox()) {
				String description = getValueByBesthMatchTag(eoModel.getCustomLBSettings(), "description", eoModel.getCustomMainSettings(), eoModel.getDefaultTitle());
				eoBean.setDescription(description);
			}
		}

		return eoBean;
	}
	
	private String getValueByBesthMatchTag(String[] customSettings, String type,  String[] defaultValueLevel1, String defaultValueLevel2) {
		String value = getValueByBesthMatchTag(customSettings, type);
		if (StringUtils.isEmpty(value)) {
			return getValueByBesthMatchTag(defaultValueLevel1, type, defaultValueLevel2);
		}
		return value;
	}
	
	private String getValueByBesthMatchTag(String[] customSettings, String type, String defaultValue) {
		String value = getValueByBesthMatchTag(customSettings, type);
		if (StringUtils.isEmpty(value)) {
			value = defaultValue;
		}
		return value;
	}

	private String getValueByBesthMatchTag(String[] customSettings, String type) {
		String value = null;
		if (customSettings != null) {
			Gson gson = new GsonBuilder().create();
			JsonObject eoSettings = null;
			for (int i = 0; i < customSettings.length && (value == null); i++) {
				eoSettings = gson.fromJson(customSettings[i], JsonObject.class); // deserializes json into eoSettings

				if (eoSettings != null) {
					boolean isActive = (eoSettings.get("active") != null) ? Boolean.valueOf(eoSettings.get("active").getAsString()) : false;
					
					if(isActive) {
						boolean typeIsTitle = (eoSettings.get("type") != null) ? eoSettings.get("type").getAsString().equalsIgnoreCase(type) : false;
						if (typeIsTitle) {
							if (eoSettings.get("tags") != null) {
								String[] tags = eoSettings.get("tags").getAsString().split(",");
								for (String tag : tags) {
									tag = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
									if (super.isBestMatch(tag)) {
										value = (eoSettings.get("value") != null) ? eoSettings.get("value").getAsString()
												: null;
										break;
									}
								}
							} else {
								value = (eoSettings.get("value") != null) ? eoSettings.get("value").getAsString() : null;
							}
						}
					}
				}
			}
		}
		return value;
	}

}
