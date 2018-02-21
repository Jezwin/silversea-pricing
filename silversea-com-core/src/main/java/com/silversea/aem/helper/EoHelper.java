package com.silversea.aem.helper;

import java.util.HashMap;
import java.util.Map;

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
	
	private Gson gson = new GsonBuilder().create();

	public EoBean parseExclusiveOffer(EoConfigurationBean eoConfig, ExclusiveOfferModel eoModel) {
		EoBean eoBean = null;
		if (eoConfig != null && eoConfig.isActiveSystem() && eoModel != null) {
			eoBean = new EoBean();
			String title = null, description = null, shortDescription = null;
			
			Map<String, EoValueToReplace> tokensAndStyle = getTokensByBesthMatchTag(eoModel.getCustomTokenValuesSettings());
			EoValueToReplace eoValue = new EoValueToReplace(eoModel.getExpirationDate().toString(), "token");
			tokensAndStyle.put("expiration_date",eoValue);
			
			if (eoConfig.isTitleMain()) {
				title = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "title", eoModel.getDefaultTitle());
			}
			if (eoConfig.isDescriptionMain()) {
				description = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "description",eoModel.getDefaultDescription());
			}
			if (eoConfig.isShortDescriptionMain()) {
				shortDescription = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "shortDescription", eoModel.getDefaultShortDescription());
			}
			//-----------------------------------------
			if(eoConfig.isDescriptionTnC()) {
				description = getValueByBesthMatchTag(eoModel.getCustomTnCSettings(), "description", eoModel.getDefaultDescriptionTnC());
			}
			//-----------------------------------------
			if(eoConfig.isTitleVoyage()) {
				title = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "title", eoModel.getCustomMainSettings(), eoModel.getDefaultTitle());
			}
			if(eoConfig.isDescriptionVoyage()) {
				description = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "description", eoModel.getCustomMainSettings(), eoModel.getDefaultDescription());
			}
			//-----------------------------------------
			if(eoConfig.isTitleLigthbox()) {
				title = getValueByBesthMatchTag(eoModel.getCustomLBSettings(), "title", eoModel.getCustomMainSettings(), eoModel.getDefaultTitle());
			}
			if(eoConfig.isDescriptionLigthbox()) {
				description = getValueByBesthMatchTag(eoModel.getCustomLBSettings(), "description", eoModel.getCustomMainSettings(), eoModel.getDefaultTitle());
			}
			if (eoConfig.isActiveGreysSystem()) {
				eoBean.setGreyBoxesSystem(eoModel.getActiveGreysBoxes());
			}
			if(eoConfig.isImageLightbox()) {
				eoBean.setImage(eoModel.getPathImageLB());
			}
			
			//replace token and style
			for (String key : tokensAndStyle.keySet()) {
				eoValue = tokensAndStyle.get(key);
				if (eoValue.getType().equalsIgnoreCase("token")) {
					key = "#" + key + "#";
				} else if (eoValue.getType().equalsIgnoreCase("style")) {
					key = "<" + key + ">";
				}
				if (StringUtils.isNotEmpty(title)) {
					title = title.replaceAll(key, eoValue.getValue());
					if (eoValue.getType().equalsIgnoreCase("style")) {
						String endStyle = "</" + key + ">";
						title = title.replaceAll(endStyle,"");
					}
				}
				if (StringUtils.isNotEmpty(description)) {
					description = description.replaceAll(key, eoValue.getValue());
					if (eoValue.getType().equalsIgnoreCase("style")) {
						String endStyle = "</" + key + ">";
						description = description.replaceAll(endStyle,"");
					}
				}
				if (StringUtils.isNotEmpty(shortDescription)) {
					shortDescription = shortDescription.replaceAll(key, eoValue.getValue());
					if (eoValue.getType().equalsIgnoreCase("style")) {
						String endStyle = "</" + key + ">";
						shortDescription = shortDescription.replaceAll(endStyle,"");
					}
				}
			}
			if (StringUtils.isNotEmpty(title)) {
				eoBean.setTitle(title);
			}
			if (StringUtils.isNotEmpty(description)) {
				eoBean.setDescription(description);
			}
			if (StringUtils.isNotEmpty(shortDescription)) {
				eoBean.setShortDescription(shortDescription);
			}
		}
		

		return eoBean;
	}
	
	private class EoValueToReplace {
		private String value;
		private String type;
		
		public EoValueToReplace(String value, String type) {
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
	
	private Map<String, EoValueToReplace> getTokensByBesthMatchTag(String[] customTokens) {
		Map<String, EoValueToReplace> tokenByTag = new HashMap<String, EoValueToReplace>();
		if (customTokens != null) {
			JsonObject eoSettings = null;
			String value = null, token = null;
			for (int i = 0; i < customTokens.length; i++) {
				eoSettings = gson.fromJson(customTokens[i], JsonObject.class); 
				if (eoSettings != null) {
					if (eoSettings.get("tags") != null) {
						String[] tags = eoSettings.get("tags").getAsString().split(",");
						for (String tag : tags) {
							tag = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
							if (super.isBestMatch(tag)) {
								value = (eoSettings.get("value") != null) ? eoSettings.get("value").getAsString() : null;
								token = (eoSettings.get("token") != null) ? eoSettings.get("token").getAsString() : null;
								EoValueToReplace eoValue = new EoValueToReplace(value, "token");
								tokenByTag.put(token, eoValue);
								break;
							}
						}
					} 
				}
			}
		}
		return tokenByTag;
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
			JsonObject eoSettings = null;
			for (int i = 0; i < customSettings.length && (value == null); i++) {
				eoSettings = gson.fromJson(customSettings[i], JsonObject.class); 

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
