package com.silversea.ssc.aem.components.editorial;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.jcr.Session;

import com.silversea.aem.config.ConfigurationManager;
import com.silversea.aem.config.CoreConfig;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.models.AppSettingsModel;
import com.silversea.aem.proxies.ExclusiveOfferProxy;
import com.silversea.aem.proxies.OkHttpClientWrapper;
import com.silversea.aem.services.ExclusiveOffer;
import com.silversea.aem.utils.AwsSecretsManager;
import com.silversea.aem.utils.AwsSecretsManagerClientWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Designer;
import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.ssc.aem.bean.EoBoxesBean;

public class ExclusiveOfferBoxesUse extends EoHelper {

	private Map<String, ValueTypeBean> tokensAndStyles;
	private Map<String, EoBoxesBean> boxes;
	private ValueMap properties;
	private ValueMap styles;
	private boolean showComponent;
	private String title;
	private String cssDesktop;
	private String cssTablet;
	private String cssMobile;

	@Override
	public void activate() throws Exception {
		super.activate();
		boxes = new HashMap<>();
		boolean fromLightbox = false;
		
		if (getRequest().getRequestPathInfo().getSelectors().length > 0
				&& ("modalcontent").equals(getRequest().getRequestPathInfo().getSelectors()[0])) {
			fromLightbox = true;
			Map<String, String> map = new HashMap<String, String>();
			map.put("path", getResource().getPath());
			map.put("1_property", "sling:resourceType");
			map.put("1_property.operation", "equals");
			map.put("1_property.value", "silversea/silversea-ssc/components/editorial/exclusiveOfferBoxes");

			QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);

			Query query = queryBuilder.createQuery(PredicateGroup.create(map),
					getResourceResolver().adaptTo(Session.class));

			SearchResult result = query.getResult();

			if (result.getTotalMatches() > 0) {
				Iterator<Resource> resources = result.getResources();
				Designer designer = getResourceResolver().adaptTo(Designer.class);
				while (resources.hasNext()) {
					Resource next = resources.next();
					properties = next.getValueMap();
					showComponent = showComponentBasedOnTags();
					styles = designer.getStyle(next);
					if (showComponent) {
						break;
					} else {
						properties = null;
						styles = null;

					}
				}
			}

		} else {
			properties = getProperties();
			styles = getCurrentStyle();
			showComponent = showComponentBasedOnTags();
			if (!showComponent) {
				return;
			}
		}

		String rootPath = properties.get("rootPath", String.class);
		String numBlocks = properties.get("numBlocks", String.class);
		this.title = properties.get("title", String.class);
		
		if (StringUtils.isEmpty(rootPath)) {
			rootPath = getCurrentPage().getPath();
		}

		if (StringUtils.isNotEmpty(rootPath) && StringUtils.isNotEmpty(numBlocks)) {

			switch (numBlocks) {
			case "block4":
				boxes.put("4", getEoData("4"));
			case "block3":
				boxes.put("3", getEoData("3"));
			case "block2":
				boxes.put("2", getEoData("2"));
			case "block1":
				boxes.put("1", getEoData("1"));
				break;
			}

			Resource res = getResourceResolver().resolve(rootPath);
			String cruiseCode = "";
			String[] selectors = getRequest().getRequestPathInfo().getSelectors();
			for (String selector : selectors) {
				if (selector.startsWith("cruise_code_")) {
					cruiseCode = selector.replace("cruise_code_", "");
				}
			}

			if (res != null) {
				Page rootPage = res.adaptTo(Page.class);

				if (rootPage != null) {
					ExclusiveOfferModel currentEO = rootPage.adaptTo(ExclusiveOfferModel.class);
					if (currentEO != null && currentEO.getActiveSystem()) {
						tokensAndStyles = super.getTokenAnsStyleByTag(currentEO);
						resolveExclusiveOfferTokens(tokensAndStyles, cruiseCode);

						String keyToReplace = null, valueToReplace = null, key = null, endTag = null, type = null,
								valueStyle = null;
						ValueTypeBean valueTS = null;
						for (Map.Entry<String, ValueTypeBean> tS : tokensAndStyles.entrySet()) {
							valueTS = tS.getValue();
							key = tS.getKey();
							type = valueTS.getType();
							valueStyle = valueTS.getValue();
							if (valueStyle.contains("font-size") && fromLightbox) {
								String[] splitStyle = valueStyle.split("font-size:");
								if (splitStyle != null && splitStyle.length > 0) {
									String[] splitValue = splitStyle[1].split("px");
									if (splitValue != null && splitValue.length > 0
											&& StringUtils.isNotEmpty(splitValue[0])) {
										int value = (int) (Float.valueOf(splitValue[0].trim()) - 2);
										valueStyle = splitStyle[0] + "font-size:" + Integer.toString(value) + "px;";

									}
								}
							}

							if (type.equalsIgnoreCase("token")) {
								keyToReplace = "#" + key + "#";
								valueToReplace = valueStyle;
							} else if (type.equalsIgnoreCase("style")) {
								keyToReplace = "<" + key + ">";
								endTag = "</" + key + ">";
								valueToReplace = "<span style='" + valueStyle + "'>";
							}

							for (Map.Entry<String, EoBoxesBean> box : boxes.entrySet()) {
								EoBoxesBean valueBox = box.getValue();
								valueBox.setText1(getParsedValue(valueBox.getText1(), type, keyToReplace,
										valueToReplace, endTag));
							}
							this.title = getParsedValue(this.title,type, keyToReplace,
									valueToReplace, endTag);
						}

					}

				}
				
				getCssStyle();		

			}
		}
	}

	private void getCssStyle() {
		this.cssDesktop = (styles.get("cssDesktop", String.class) != null) ? styles.get("cssDesktop", String.class) : "";
		this.cssDesktop = (properties.get("cssDesktop", String.class) != null) ? this.cssDesktop + " " + properties.get("cssDesktop", String.class) : this.cssDesktop;
		
		this.cssTablet = (styles.get("cssTablet", String.class) != null) ? styles.get("cssTablet", String.class) : "";
		this.cssTablet = (properties.get("cssTablet", String.class) != null) ? this.cssTablet + " " + properties.get("cssTablet", String.class) : this.cssTablet;
		
		this.cssMobile = (styles.get("cssMobile", String.class) != null) ? styles.get("cssMobile", String.class) : "";
		this.cssMobile = (properties.get("cssMobile", String.class) != null) ? this.cssMobile + " " + properties.get("cssMobile", String.class) : this.cssMobile;

	}

	private boolean showComponentBasedOnTags() {
		if (this.properties != null) {
			String[] tags = (String[]) this.properties.get("tags");
			if(tags != null) {
				for (String tag : tags) {
					String t = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
					if (super.isBestMatch(t)) {
						return true;
					}
					
				}
			}
		}
		return false;
	}

	private String getParsedValue(String valueToParse, String type, String keyToReplace, String valueToReplace,
			String endTag) {
		String result = null;
		if (StringUtils.isNotEmpty(valueToParse)) {
			result = valueToParse.replace(keyToReplace, valueToReplace);
			result = result.replace("\n", "<br>");
			if (type.equalsIgnoreCase("style")) {
				result = result.replace(endTag, "</span>");
			}
		}
		return result;
	}

	private EoBoxesBean getEoData(String numberBlock) {
		EoBoxesBean eoData = new EoBoxesBean();
		eoData.setNumber(numberBlock);
		String value = properties.get("textBlock" + numberBlock, String.class);
		eoData.setText1(value);

		value = properties.get("iconBlock" + numberBlock, String.class);
		eoData.setIcon(value);

		return eoData;
	}

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
