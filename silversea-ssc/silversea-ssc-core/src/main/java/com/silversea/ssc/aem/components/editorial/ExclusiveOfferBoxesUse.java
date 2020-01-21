package com.silversea.ssc.aem.components.editorial;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
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
import com.silversea.ssc.aem.bean.ExclusiveOfferBoxesBean;

public class ExclusiveOfferBoxesUse extends EoHelper {
	
	private ExclusiveOfferBoxesBean[] exclusiveOfferBoxes;

	@Override
	public void activate() throws Exception {
		super.activate();
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
				exclusiveOfferBoxes = new ExclusiveOfferBoxesBean[(int) result.getTotalMatches()];
				Iterator<Resource> resources = result.getResources();
				Designer designer = getResourceResolver().adaptTo(Designer.class);
				int i=0;
				while (resources.hasNext()) {
					Resource next = resources.next();
					exclusiveOfferBoxes[i] = new ExclusiveOfferBoxesBean();
					exclusiveOfferBoxes[i].setProperties(next.getValueMap());
					exclusiveOfferBoxes[i].setShowComponent(showComponentBasedOnTags(exclusiveOfferBoxes[i]));
					exclusiveOfferBoxes[i].setStyles(designer.getStyle(next));
					i++;
				}
			}

		} else {
			exclusiveOfferBoxes = new ExclusiveOfferBoxesBean[1];
			exclusiveOfferBoxes[0] = new ExclusiveOfferBoxesBean();
			exclusiveOfferBoxes[0].setProperties(getProperties());
			exclusiveOfferBoxes[0].setStyles(getCurrentStyle());
			exclusiveOfferBoxes[0].setShowComponent(showComponentBasedOnTags(exclusiveOfferBoxes[0]));
			if (!exclusiveOfferBoxes[0].getShowComponent()) {
				return;
			}
		}
		
		for(int j=0; j< exclusiveOfferBoxes.length; j++){
			String rootPath = exclusiveOfferBoxes[j].getProperties().get("rootPath", String.class);
			String numBlocks = exclusiveOfferBoxes[j].getProperties().get("numBlocks", String.class);
			exclusiveOfferBoxes[j].setTitle(exclusiveOfferBoxes[j].getProperties().get("title", String.class));
			
			Map<String, ValueTypeBean> tokensAndStyles;
			ValueMap styles;
			
			if (StringUtils.isEmpty(rootPath)) {
				rootPath = getCurrentPage().getPath();
			}

			if (StringUtils.isNotEmpty(rootPath) && StringUtils.isNotEmpty(numBlocks)) {
				Map<String, EoBoxesBean> boxes = new HashMap<>();
				switch (numBlocks) {
				case "block4":
					boxes.put("4", getEoData("4", exclusiveOfferBoxes[j]));
				case "block3":
					boxes.put("3", getEoData("3", exclusiveOfferBoxes[j]));
				case "block2":
					boxes.put("2", getEoData("2", exclusiveOfferBoxes[j]));
				case "block1":
					boxes.put("1", getEoData("1", exclusiveOfferBoxes[j]));
					break;
				}
				exclusiveOfferBoxes[j].setBoxes(boxes);
				Resource res = getResourceResolver().resolve(rootPath);

				if (res != null) {
					Page rootPage = res.adaptTo(Page.class);

					if (rootPage != null) {
						ExclusiveOfferModel currentEO = rootPage.adaptTo(ExclusiveOfferModel.class);
						if (currentEO != null && currentEO.getActiveSystem()) {
							tokensAndStyles = super.getTokenAnsStyleByTag(currentEO);
							resolveExclusiveOfferTokens(currentEO, tokensAndStyles);

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
								exclusiveOfferBoxes[j].setTitle(getParsedValue(exclusiveOfferBoxes[j].getTitle(),type, keyToReplace,
										valueToReplace, endTag));
							}

						}

					}
					
					getCssStyle(exclusiveOfferBoxes[j]);		

				}
			}
		}
		
		
		
	}

	private void getCssStyle(ExclusiveOfferBoxesBean exclusiveOfferBoxesBean) {
		exclusiveOfferBoxesBean.setCssDesktop((exclusiveOfferBoxesBean.getStyles().get("cssDesktop", String.class) != null) ? exclusiveOfferBoxesBean.getStyles().get("cssDesktop", String.class) : "");
		exclusiveOfferBoxesBean.setCssDesktop((exclusiveOfferBoxesBean.getProperties().get("cssDesktop", String.class) != null) ? exclusiveOfferBoxesBean.getCssDesktop() + " " + exclusiveOfferBoxesBean.getProperties().get("cssDesktop", String.class) : exclusiveOfferBoxesBean.getCssDesktop());
		
		exclusiveOfferBoxesBean.setCssTablet((exclusiveOfferBoxesBean.getStyles().get("cssTablet", String.class) != null) ? exclusiveOfferBoxesBean.getStyles().get("cssTablet", String.class) : "");
		exclusiveOfferBoxesBean.setCssTablet((exclusiveOfferBoxesBean.getProperties().get("cssTablet", String.class) != null) ? exclusiveOfferBoxesBean.getCssTablet() + " " + exclusiveOfferBoxesBean.getProperties().get("cssTablet", String.class) : exclusiveOfferBoxesBean.getCssTablet());
		
		exclusiveOfferBoxesBean.setCssMobile((exclusiveOfferBoxesBean.getStyles().get("cssMobile", String.class) != null) ? exclusiveOfferBoxesBean.getStyles().get("cssMobile", String.class) : "");
		exclusiveOfferBoxesBean.setCssMobile((exclusiveOfferBoxesBean.getProperties().get("cssMobile", String.class) != null) ? exclusiveOfferBoxesBean.getCssMobile() + " " + exclusiveOfferBoxesBean.getProperties().get("cssMobile", String.class) : exclusiveOfferBoxesBean.getCssMobile());
	}

	private boolean showComponentBasedOnTags(ExclusiveOfferBoxesBean exclusiveOfferBoxesBean) {
		if (exclusiveOfferBoxesBean.getProperties() != null) {
			String[] tags = (String[]) exclusiveOfferBoxesBean.getProperties().get("tags");
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

	private EoBoxesBean getEoData(String numberBlock, ExclusiveOfferBoxesBean exclusiveOfferBoxesBean) {
		EoBoxesBean eoData = new EoBoxesBean();
		eoData.setNumber(numberBlock);
		String value = exclusiveOfferBoxesBean.getProperties().get("textBlock" + numberBlock, String.class);
		eoData.setText1(value);

		value = exclusiveOfferBoxesBean.getProperties().get("iconBlock" + numberBlock, String.class);
		eoData.setIcon(value);

		return eoData;
	}
	
	public ExclusiveOfferBoxesBean[] getExclusiveOfferBoxes() {
		return exclusiveOfferBoxes;
	}

	public void setExclusiveOfferBoxes(ExclusiveOfferBoxesBean[] exclusiveOfferBoxes) {
		this.exclusiveOfferBoxes = exclusiveOfferBoxes;
	}

}
