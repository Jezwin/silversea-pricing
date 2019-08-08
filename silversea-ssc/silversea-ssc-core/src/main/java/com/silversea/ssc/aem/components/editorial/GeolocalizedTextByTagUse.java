package com.silversea.ssc.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;

/**
 * GeolocalizedTextByTagUse class used inside the component
 * geolocalizedTextByTag
 * 
 * @author giuseppes
 *
 */
public class GeolocalizedTextByTagUse extends AbstractGeolocationAwareUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeolocalizedTextByTagUse.class);

	private String geolocalizedText;

	@Override
	public void activate() throws Exception {
		super.activate();
		Node currentNode = getResource().adaptTo(Node.class);
		Map<String, List<String>> geolocalizedTextMap = getGeolocalizedTextByTag(currentNode);

		if (!geolocalizedTextMap.isEmpty()) {
			for (Map.Entry<String, List<String>> entry : geolocalizedTextMap.entrySet()) {
				String text = (String) entry.getKey();
				List<String> tags = (List<String>) entry.getValue();
				for (String tag : tags) {
					if (super.isBestMatch(tag)) {
						geolocalizedText = text;
						break;
					}
				}
				if (geolocalizedText != null) {
					break;
				}
			}
		}

		if (geolocalizedText == null) {
			geolocalizedText = getProperties().get("defaultText", String.class);
		}
	}


	public String geolocalizedText() {
		return geolocalizedText;
	}

	/**
	 * Retrieve all text and relative tags inside the node.
	 * geolocalizedtextbyta_XXXXX/listText/1/listTag/1 tags
	 * geolocalizedtextbyta_XXXXX/listText/2/listTag/1 tags
	 * 
	 * @param currentNode
	 *            Node to start looking all text and tags
	 *            (geolocalizedtextbyta_XXXXX)
	 * @return map with key=text and value=list tags
	 */
	private Map<String, List<String>> getGeolocalizedTextByTag(Node currentNode) {
		Map<String, List<String>> mapResult = new LinkedHashMap<>();
		try {
			// get listText node geolocalizedtextbyta_XXXXX/listText
			Node listTextNode = (currentNode.hasNode("listText") && currentNode.getNodes("listText") != null) ? ((Node) currentNode.getNodes("listText")
					.next()) : null;
			if (listTextNode != null) {
				/*
				 * get all node inside listText node
				 * geolocalizedtextbyta_XXXXX/listText/1 .. /2..
				 */
				Iterator<Node> itListText = listTextNode.getNodes();
				while (itListText.hasNext()) {
					Node textNode = itListText.next();
					// get property text
					if (textNode.hasProperty("text")) {
						Property textProp = textNode.getProperty("text");
						Value textValue = textProp.getValue();
						String text = textValue.toString();
						
						/*
						 * get node listTag
						 * geolocalizedtextbyta_XXXXX/listText/1/listTag /2/listTag
						 */
						if (textNode.hasProperty("tagsList") && textNode.getProperty("tagsList") != null)  {
							Property property= textNode.getProperty("tagsList");
							if (property != null) {
								List<String> geoTagList = new ArrayList<String>();
								String valTags = property.getValue().toString();
								if (StringUtils.isNotEmpty(valTags)) {
									String[] allTags = valTags.split(",");
									for (String s : allTags) {
										// split geotagging:as/far-east/CK
										String[] sTag = s.split(WcmConstants.GEOLOCATION_TAGS_PREFIX);
										geoTagList.add(sTag[1]);
									}
									mapResult.put(text, geoTagList);
								}
							}
							
						} else if(textNode.hasNode("listTag") && textNode.getNodes("listTag") != null) {
							Node listTagNode = (Node) textNode.getNodes("listTag").next();
							
							Iterator<Node> itListTag = listTagNode.getNodes();
							List<String> geoTagList = new ArrayList<String>();
							while (itListTag.hasNext()) {
								Node tagNode = itListTag.next();
								
								// get propery tags
								Property tagProp = tagNode.getProperty("tags");
								Value[] tagValue = tagProp.getValues();
								for (Value v : tagValue) {
									// split geotagging:as/far-east/CK
									String[] vTag = v.getString().split(WcmConstants.GEOLOCATION_TAGS_PREFIX);
									geoTagList.add(vTag[1]);
								}
							}
							mapResult.put(text, geoTagList);
						}
					}
				}
			}
		} catch (PathNotFoundException e) {
			LOGGER.error("Error to get property text or tags", e);
		} catch (ValueFormatException e) {
			LOGGER.error("Error to get value text or tags", e);
		} catch (RepositoryException e) {
			LOGGER.error("Error to get node", e);
		} catch (NoSuchElementException e) {
			LOGGER.error("Error to get node listText", e);
		}
		return mapResult;
	}
}
