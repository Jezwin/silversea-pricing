package com.silversea.aem.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.services.StyleCache;
import com.silversea.aem.models.ExclusiveOfferFareModel;
import com.silversea.aem.models.ExclusiveOfferModel;

public class EoHelper extends AbstractGeolocationAwareUse {
	
	private StyleCache styleCache;
	private Gson gson;
	
	@Override
	public void activate() throws Exception {
		super.activate();
		styleCache = getSlingScriptHelper().getService(StyleCache.class);
		gson = new GsonBuilder().create();
	}
	
	
	public EoBean parseExclusiveOffer(EoConfigurationBean eoConfig, ExclusiveOfferModel eoModel) {
		EoBean eoBean = null;
		
		if (eoConfig != null && eoConfig.isActiveSystem() && eoModel != null) {
			eoBean = new EoBean();
			String title = null, description = null, shortDescription = null, mapOverhead = null, footnote = null, shortTitle = null;
			ExclusiveOfferFareModel[] cruiseFares = null;
			Map<String, ValueTypeBean> styles = styleCache.getStyles();
			
			Map<String, ValueTypeBean> tokensAndStyle = getTokensByBesthMatchTag(eoModel.getCustomTokenValuesSettings());
			ValueTypeBean eoValue = null;
			if(eoModel.getExpirationDate() != null) {
				Date expirationDate = eoModel.getExpirationDate();
				SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", getCurrentPage().getLanguage(false));
				formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
				eoValue = new ValueTypeBean(formatter.format(expirationDate.getTime()), "token");
				tokensAndStyle.put("expiration_date",eoValue);
				
				SimpleDateFormat formatterShort = new SimpleDateFormat("dd MMMM", getCurrentPage().getLanguage(false));
				formatterShort.setTimeZone(TimeZone.getTimeZone("GMT"));
				eoValue = new ValueTypeBean(formatterShort.format(expirationDate.getTime()), "token");
				tokensAndStyle.put("expiration_date_short",eoValue);
				
				
			}
			
			if (styles != null && !styles.isEmpty()) {
				tokensAndStyle.putAll(styles);
			}
			
			if (eoModel.getGeomarkets() != null && eoModel.getGeomarkets().contains(geomarket)) {
				eoBean.setAvailable(true);
			}
			
			if (eoConfig.isTitleMain()) {
				title = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "title", eoModel.getDefaultTitle());
			}
			
			if (eoConfig.isShortTitleMain()) {
				shortTitle = getValueByBesthMatchTag(eoModel.getCustomMainSettings(), "shortTitle", eoModel.getDefaultShortTitle());
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
			if(eoConfig.isShortDescriptionVoyage()) {
				shortDescription = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "shortDescription", eoModel.getCustomMainSettings(), eoModel.getDefaultShortDescription());
			}
			if(eoConfig.isMapOverheadVoyage()) {
				mapOverhead = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "mapOverhead", eoModel.getCustomMainSettings(), eoModel.getDefaultMapOverhead());
			}
			if(eoConfig.isFootnoteVoyage()) {
				footnote = getValueByBesthMatchTag(eoModel.getCustomVoyageSettings(), "footnote", eoModel.getCustomMainSettings(), eoModel.getDefaultFootnote());
			}
			if(eoConfig.isCruiseFareVoyage()){
				cruiseFares = getCruiseFaresValuesByBesthMatchTag(eoModel.getCustomVoyageFaresSettings());
			}
			//-----------------------------------------
			if(eoConfig.isTitleLigthbox()) {
				title = getValueByBesthMatchTag(eoModel.getCustomLBSettings(), "title", eoModel.getCustomMainSettings(), eoModel.getDefaultTitle());
			}
			if(eoConfig.isDescriptionLigthbox()) {
				description = getValueByBesthMatchTag(eoModel.getCustomLBSettings(), "description", eoModel.getCustomMainSettings(), eoModel.getDefaultDescription());
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
				String valueToReplace = null,
						keyToReplace = null,
						endTag = null;
				if (eoValue.getType().equalsIgnoreCase("token")) {
					keyToReplace = "#" + key + "#";
					valueToReplace= "\\" + eoValue.getValue();
				} else if (eoValue.getType().equalsIgnoreCase("style")) {
					keyToReplace = "<" + key + ">"; 
					endTag = "</" + key + ">";
					valueToReplace= "<span style='" + eoValue.getValue() + "'>";
				}
				if (StringUtils.isNotEmpty(title)) {
					title = title.replaceAll(keyToReplace, valueToReplace);
					title = title.replaceAll("\n", "<br>");
					if (eoValue.getType().equalsIgnoreCase("style")) {
						title = title.replaceAll(endTag,"</span>");
					}
				}
				if (StringUtils.isNotEmpty(shortTitle)) {
					shortTitle = shortTitle.replaceAll(keyToReplace, valueToReplace);
					shortTitle = shortTitle.replaceAll("\n", "<br>");
					if (eoValue.getType().equalsIgnoreCase("style")) {
						shortTitle = shortTitle.replaceAll(endTag,"</span>");
					}
				}
				if (StringUtils.isNotEmpty(description)) {
					description = description.replaceAll(keyToReplace, valueToReplace);
					description = description.replaceAll("\n", "<br>");
					if (eoValue.getType().equalsIgnoreCase("style")) {
						description = description.replaceAll(endTag,"</span>");
					}
				}
				if (StringUtils.isNotEmpty(shortDescription)) {
					shortDescription = shortDescription.replaceAll(keyToReplace, valueToReplace);
					shortDescription = shortDescription.replaceAll("\n", "<br>");
					if (eoValue.getType().equalsIgnoreCase("style")) {
						shortDescription = shortDescription.replaceAll(endTag,"</span>");
					}
				}
				if (StringUtils.isNotEmpty(mapOverhead)) {
					mapOverhead = mapOverhead.replaceAll(keyToReplace, valueToReplace);
					mapOverhead = mapOverhead.replaceAll("\n", "<br>");
					if (eoValue.getType().equalsIgnoreCase("style")) {
						mapOverhead = mapOverhead.replaceAll(endTag,"</span>");
					}
				}
				if (StringUtils.isNotEmpty(footnote)) {
					footnote = footnote.replaceAll(keyToReplace, valueToReplace);
					footnote = footnote.replaceAll("\n", "<br>");
					if (eoValue.getType().equalsIgnoreCase("style")) {
						footnote = footnote.replaceAll(endTag,"</span>");
					}
				}
				if(cruiseFares != null && cruiseFares.length > 0){
					for (int i = 0; i < cruiseFares.length; i++) {
						if (StringUtils.isNotEmpty(cruiseFares[i].additionalFare)) {
							cruiseFares[i].additionalFare = cruiseFares[i].additionalFare.replaceAll(keyToReplace, valueToReplace);
							if (eoValue.getType().equalsIgnoreCase("style")) {
								cruiseFares[i].additionalFare = cruiseFares[i].additionalFare.replaceAll(endTag,"</span>");
							}
						}
						
						if (StringUtils.isNotEmpty(cruiseFares[i].footnote)) {
							cruiseFares[i].footnote = cruiseFares[i].footnote.replaceAll(keyToReplace, valueToReplace);
							if (eoValue.getType().equalsIgnoreCase("style")) {
								cruiseFares[i].footnote = cruiseFares[i].footnote.replaceAll(endTag,"</span>");
							}
						}
					}
				}
			}
			if (StringUtils.isNotEmpty(title)) {
				eoBean.setTitle(title);
			}
			if (StringUtils.isNotEmpty(shortTitle)) {
				eoBean.setShortTitle(shortTitle);
			}
			if (StringUtils.isNotEmpty(description)) {
				eoBean.setDescription(description);
			}
			if (StringUtils.isNotEmpty(shortDescription)) {
				eoBean.setShortDescription(shortDescription);
			}
			if (StringUtils.isNotEmpty(mapOverhead)) {
				eoBean.setMapOverhead(mapOverhead);
			}
			if (StringUtils.isNotEmpty(footnote)) {
				eoBean.setFootnote(footnote);
			}
			if(cruiseFares != null && cruiseFares.length > 0){
				eoBean.setCruiseFares(cruiseFares);
			}
		}
		

		return eoBean;
	}
	
	protected Map<String, ValueTypeBean> getTokenAnsStyleByTag(ExclusiveOfferModel eoModel) {
		Map<String, ValueTypeBean> tokensAndStyle = getTokensByBesthMatchTag(eoModel.getCustomTokenValuesSettings());
		Map<String, ValueTypeBean> styles = styleCache.getStyles();

		ValueTypeBean eoValue = null;
		if(eoModel.getExpirationDate() != null) {
			Date expirationDate = eoModel.getExpirationDate();
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", getCurrentPage().getLanguage(false));
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			eoValue = new ValueTypeBean(formatter.format(expirationDate.getTime()), "token");
			tokensAndStyle.put("expiration_date",eoValue);
			
			SimpleDateFormat formatterShort = new SimpleDateFormat("dd MMMM", getCurrentPage().getLanguage(false));
			formatterShort.setTimeZone(TimeZone.getTimeZone("GMT"));
			eoValue = new ValueTypeBean(formatterShort.format(expirationDate.getTime()), "token");
			tokensAndStyle.put("expiration_date_short",eoValue);
		}
		
		if (styles != null && !styles.isEmpty()) {
			tokensAndStyle.putAll(styles);
		}
		
		return tokensAndStyle;
	}
	
	
	private Map<String, ValueTypeBean> getTokensByBesthMatchTag(String[] customTokens) {
		Map<String, ValueTypeBean> tokenByTag = new HashMap<String, ValueTypeBean>();
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
								ValueTypeBean eoValue = new ValueTypeBean(value, "token");
								if(!tokenByTag.containsKey(token)) {
									tokenByTag.put(token, eoValue);
								}
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
	
	private ExclusiveOfferFareModel[] getCruiseFaresValuesByBesthMatchTag(String[] customSettings) {
		List<ExclusiveOfferFareModel> value = null;
		if (customSettings != null) {
			JsonObject eoSettings = null;
			for (int i = 0; i < customSettings.length; i++) {
				eoSettings = gson.fromJson(customSettings[i], JsonObject.class); 

				if (eoSettings != null) {
					boolean isActive = (eoSettings.get("active") != null) ? Boolean.valueOf(eoSettings.get("active").getAsString()) : false;
					
					if(isActive) {
						
							if (eoSettings.get("tags") != null) {
								String[] tags = eoSettings.get("tags").getAsString().split(",");
								for (String tag : tags) {
									tag = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
									if (super.isBestMatch(tag)) {
										ExclusiveOfferFareModel newFare = new ExclusiveOfferFareModel();
										if((eoSettings.get("cruisefare") != null)){
											
											newFare.additionalFare = eoSettings.get("cruisefare").getAsString();
										}
										if((eoSettings.get("cruisefarefootnote") != null)){
											newFare.footnote = eoSettings.get("cruisefarefootnote").getAsString();
										}
										if(value == null){
											value = new ArrayList<ExclusiveOfferFareModel>();
										}
										value.add(newFare);
										break;
									}
								}
							} 
						
					}
				}
			}
		}
		if(value != null){
			return value.toArray(new ExclusiveOfferFareModel[value.size()]);
		}else{
			return null;
		}
	}

}
