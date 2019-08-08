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
import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.ssc.aem.bean.EoBoxesBean;

public class EoBoxesUse extends EoHelper {

	private Map<String, ValueTypeBean> tokensAndStyles;
	private Map<String, EoBoxesBean> boxes;
	private ValueMap properties;
	private boolean showComponent;

	@Override
	public void activate() throws Exception {
		super.activate();
		boxes = new HashMap<>();
		boolean fromLightbox  = false; 
		
		
		if (getRequest().getRequestPathInfo().getSelectors().length > 0 && ("modalcontent").equals(getRequest().getRequestPathInfo().getSelectors()[0])) {
			fromLightbox = true;
			 Map<String, String> map = new HashMap<String, String>();
			 map.put("path", getResource().getPath());
			 map.put("1_property", "sling:resourceType");
			 map.put("1_property.operation", "equals");
			 map.put("1_property.value", "silversea/silversea-ssc/components/editorial/eoBoxes");

			QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
			
			Query query = queryBuilder
					.createQuery(PredicateGroup.create(map), getResourceResolver().adaptTo(Session.class));
			
			SearchResult result = query.getResult();
			
			if (result.getTotalMatches() > 0) {
				Iterator<Resource> resources = result.getResources();
				while(resources.hasNext()){
				    Resource next = resources.next();
				    properties = next.getValueMap();
					showComponent = showComponentBasedOnTags(); 
				    if(showComponent) {
				    	break;
				    } else {
				    	properties = null;
				    	
				    }
				}
			}
			
		}else{
			properties = getProperties();
			showComponent = showComponentBasedOnTags(); 
			 if(!showComponent) {
				 return;
			 }
		}
		
		if (this.properties != null) {
			
			String rootPath = properties.get("rootPath", String.class);
			String activeBox1 = properties.get("activeBox1", String.class);
			String activeBox2 = properties.get("activeBox2", String.class);
			String activeBox3 = properties.get("activeBox3", String.class);
			String activeBoxSeparator1 = properties.get("activeBoxSeparator1", String.class);
			String activeBoxSeparator2 = properties.get("activeBoxSeparator2", String.class);
			
			if (StringUtils.isEmpty(rootPath)) {
				rootPath = getCurrentPage().getPath();
			}
			
			if (StringUtils.isNotEmpty(rootPath)) {
				Resource res = getResourceResolver().resolve(rootPath);
				
				if (res != null) {
					Page rootPage = res.adaptTo(Page.class);
					
					if (rootPage != null) {
						ExclusiveOfferModel currentEO = rootPage.adaptTo(ExclusiveOfferModel.class);
						if (currentEO != null && currentEO.getActiveSystem()) {
							tokensAndStyles = super.getTokenAnsStyleByTag(currentEO);
							
							if (Boolean.valueOf(activeBox1)) {
								boxes.put("b1", getEoData("1"));
							}
							if (Boolean.valueOf(activeBox1) && Boolean.valueOf(activeBoxSeparator1) && Boolean.valueOf(activeBox2)) {
								boxes.put("sep1", getEoData("Separator1"));
							}
							if (Boolean.valueOf(activeBox1)  && Boolean.valueOf(activeBox2)) {
								boxes.put("b2", getEoData("2"));
							}
							if (Boolean.valueOf(activeBox2) && Boolean.valueOf(activeBoxSeparator2) && Boolean.valueOf(activeBox3)) {
								boxes.put("sep2", getEoData("Separator2"));
							}
							if (Boolean.valueOf(activeBox2) && Boolean.valueOf(activeBox3)) {
								boxes.put("b3", getEoData("3"));
							}
							
							String keyToReplace = null, valueToReplace = null, key = null, endTag = null, type = null, valueStyle = null;
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
										if(splitValue != null && splitValue.length > 0 && StringUtils.isNotEmpty(splitValue[0])) {
											int value = Integer.valueOf(splitValue[0].trim()) - 2;
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
									
									valueBox.setText1(getParsedValue(valueBox.getText1(), type, keyToReplace,valueToReplace, endTag));
									valueBox.setText2(getParsedValue(valueBox.getText2(), type, keyToReplace,valueToReplace, endTag));
									valueBox.setText3(getParsedValue(valueBox.getText3(), type, keyToReplace,valueToReplace, endTag));
									
									valueBox.setSeparator1(getParsedValue(valueBox.getSeparator1(), type, keyToReplace,valueToReplace, endTag));
									valueBox.setSeparator2(getParsedValue(valueBox.getSeparator2(), type, keyToReplace,valueToReplace, endTag));
									
								}
							}
							
							setWidthBoxes();
						}
						
					}
				}
			}
		}
	}
	
	private boolean showComponentBasedOnTags() {
		if (this.properties != null) {
			String[] tags = (String[]) this.properties.get("tags");
			if(tags != null && tags.length > 0) {
				for (String tag : tags) {
					String t = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
					if(super.isBestMatch(t)) {
						return true;
					}
					
				}
			}
		}
		return false;
	}
	
	private void setWidthBoxes() {
		EoBoxesBean box1 = boxes.get("b1");
		EoBoxesBean box2 = boxes.get("b2");
		EoBoxesBean box3 = boxes.get("b3");
		
		EoBoxesBean sep1 = boxes.get("sep1");
		EoBoxesBean sep2 = boxes.get("sep2");
		
		if(box1 != null) {
			if(box1.getSize().equalsIgnoreCase("block2")) {
				box1.setWidthClass("eo-col-md-8");
				
				if(box2 != null) {
					if(box2.getSize().equalsIgnoreCase("block2")) {
						box1.setWidthClass("eo-col-md-6");
						box2.setWidthClass("eo-col-md-6");
						
						if (sep1 != null) {
							box1.setWidthClass("eo-col-md-5");
							sep1.setWidthClass("eo-col-md-1_5");
							box2.setWidthClass("eo-col-md-5");				
						}
					} else if(box2.getSize().equalsIgnoreCase("block1")) {
						box2.setWidthClass("eo-col-md-4");	
						
						if (sep1 != null) {
							box1.setWidthClass("eo-col-md-7");
							sep1.setWidthClass("eo-col-md-1_5");
							box2.setWidthClass("eo-col-md-3");				
						}
					}
				}
				
			} else if(box1.getSize().equalsIgnoreCase("block1")) {
				box1.setWidthClass("eo-col-md-4");
				
				if (sep1 != null) {
					sep1.setWidthClass("eo-col-md-3");
					if(box2 != null && box2.getSize().equalsIgnoreCase("block2")) {
						box1.setWidthClass("eo-col-md-3");
						sep1.setWidthClass("eo-col-md-1_5");
						box2.setWidthClass("eo-col-md-7");
					}
				} else {
					if(box2 != null) {
						if(box2.getSize().equalsIgnoreCase("block2")) {
							box2.setWidthClass("eo-col-md-8");
						}
					}
				}
				
				if (sep2 != null) {
					box1.setWidthClass("eo-col-md-2");
					box2.setWidthClass("eo-col-md-2");

					if(box3 != null) {
						box3.setWidthClass("eo-col-md-2");
					}
				}
			}
		}
		
		if (box3 != null) {
			if(box3.getSize().equalsIgnoreCase("block1")) {
				box1.setWidthClass("eo-col-md-4");
				if (sep2 != null) {
					box1.setWidthClass("eo-col-md-2");
				}
			}
		}
		
		
		if(sep1 != null && sep2 != null) {
			sep1.setWidthClass("eo-col-md-1");
			sep2.setWidthClass("eo-col-md-1");
		}
	}

	private String getParsedValue(String valueToParse, String type, String keyToReplace, String valueToReplace, String endTag) {
		String result = null;
		if (StringUtils.isNotEmpty(valueToParse)) {
			result = valueToParse.replace(keyToReplace, valueToReplace);
			result = result.replace("\n", "<br>");
			if (type.equalsIgnoreCase("style")) {
				result = result.replace(endTag,"</span>");
			}
		}
		return result;
	}
	
	private EoBoxesBean getEoData(String numberBox) {
		EoBoxesBean eoData = new EoBoxesBean();
		
		eoData.setNumber(numberBox);
		
		String value = properties.get("boxIconBox" + numberBox, String.class);
		eoData.setIcon(value);

		value = properties.get("boxSizeBox" + numberBox, String.class);
		if(value.equalsIgnoreCase("block3")) {
			eoData.setWidthClass("eo-col-md-12");
		} else if(value.equalsIgnoreCase("block2")) {
			eoData.setWidthClass("eo-col-md-8");
		} else if(value.equalsIgnoreCase("block1")) {
			eoData.setWidthClass("eo-col-md-4");
		} 
		eoData.setSize(value);

		value = properties.get("text1Box" + numberBox, String.class);
		eoData.setText1(value);

		value = properties.get("text2Box" + numberBox, String.class);
		eoData.setText2(value);

		value = properties.get("text3Box" + numberBox, String.class);
		eoData.setText3(value);

		value = properties.get("text1SeparatorBox" + numberBox, String.class);
		eoData.setSeparator1(value);

		value = properties.get("text2SeparatorBox" + numberBox, String.class);
		eoData.setSeparator2(value);

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

}
