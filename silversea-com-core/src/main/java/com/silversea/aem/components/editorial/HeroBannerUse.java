package com.silversea.aem.components.editorial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.Button;
import com.silversea.aem.helper.UrlHelper;
import com.silversea.aem.utils.AssetUtils;

public class HeroBannerUse extends AbstractGeolocationAwareUse {
	
	static final private Logger LOGGER = LoggerFactory.getLogger(HeroBannerUse.class);
	
	private Button btn1;
	private Button btn2;

	private String suffixUrl;
	private String selectorUrl;

	private String imageBackgroundPath;
	private String imageBackgroundPathMobile;
	
	private String title;
	private String description;
	private String background;
	
	private String inlineGalleryID;

	@Override
	public void activate() throws Exception {
		super.activate();
		this.title = getProperties().get("text", String.class);
		this.description = getProperties().get("jcr:description", String.class);
		this.background = getProperties().get("assetReference", String.class);
		Boolean enableInlineGallery = getProperties().get("enableInlineGallery", Boolean.class);
		
		if(geomarket != null) {
			getValueByMarket(geomarket.toUpperCase());
		}
		
		Resource resourceAsset = getResourceResolver().getResource(this.background);
		
		if (resourceAsset != null) {
			Asset asset = resourceAsset.adaptTo(Asset.class);
			if (asset != null) {
				if (!DamUtil.isImage(asset)) {
					String dcFormat = asset.getMetadata().get(DamConstants.DC_FORMAT) != null
							? asset.getMetadata().get(DamConstants.DC_FORMAT).toString()
							: null;
					if (dcFormat.contains(S7damConstants.S7_MIXED_MEDIA_SET)) {
						List<Asset> assetlist = AssetUtils.buildAssetList(this.background, getResourceResolver());
						if (assetlist.size() > 0) {
							this.imageBackgroundPath = assetlist.get(0).getPath();
							this.imageBackgroundPathMobile = assetlist.get(0).getPath();
						}
					}
				} else {
					this.imageBackgroundPath = this.background;
					this.imageBackgroundPathMobile = this.background;
				}
				if (enableInlineGallery!= null && enableInlineGallery) {
					this.inlineGalleryID = searchInlineGalleryID("inlinegallery");
					if(this.inlineGalleryID == null) {
						this.inlineGalleryID = searchInlineGalleryID("inlinegalleryLanding");
					}
				}
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
	
	private void getValueByMarket(String market) {
		String value = getProperties().get("title" + market, String.class);
		if(StringUtils.isNotEmpty(value)) {
			this.title = value;
		}
		value = getProperties().get("description" + market, String.class);
		if(StringUtils.isNotEmpty(value)) {
			this.description = value;
		}
		value = getProperties().get("background" + market, String.class);
		if(StringUtils.isNotEmpty(value)) {
			this.background = value;
		}
	}
	
	private String searchInlineGalleryID(String type) {
		String id = null;
		String path = getCurrentPage().getPath();
		Map<String, String> map = new HashMap<String, String>();

		map.put("path", path);

        map.put("property", "sling:resourceType");
        if(type.equalsIgnoreCase("inlinegallery")) {
        	map.put("property.value", "silversea/silversea-com/components/editorial/inlinegallery");
        } else if(type.equalsIgnoreCase("inlinegalleryLanding")) {
        	map.put("property.value", "silversea/silversea-ssc/components/editorial/inlinegalleryLanding");
        }

        map.put("boolproperty", "enableInlineGalleryHB");
        map.put("boolproperty.value", "true");
        
        // Order By
        map.put("orderby", "@jcr:content/jcr:created");
        map.put("orderby.sort", "asc");
        map.put("orderby.case", "ignore");

        // Offsets and Limits; usually used for pagination
        map.put("p.offset", "0");
        map.put("p.limit", "1");

		QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(map), getResourceResolver().adaptTo(Session.class));

        SearchResult result = query.getResult();

        try {
            for (Hit hit : result.getHits()) {
                ValueMap properties = hit.getProperties();
                if(type.equalsIgnoreCase("inlinegallery")) {
                	id = "#c-inline-gallery-" + properties.get("sscUUID", String.class);
                } else if(type.equalsIgnoreCase("inlinegalleryLanding")) {
                	id = "#c-inline-gallery-landing-" + properties.get("sscUUID", String.class);
                }
                String assetSelectionReference = properties.get("assetSelectionReference", String.class);
                if (StringUtils.isEmpty(assetSelectionReference)) {
                	assetSelectionReference = getCurrentPage().getProperties().get("assetSelectionReference", String.class);
                }
                if (StringUtils.isNotEmpty(assetSelectionReference)) {
                	Resource resourceAsset = getResourceResolver().getResource(assetSelectionReference);

            		if (resourceAsset != null) {
            			Asset asset = resourceAsset.adaptTo(Asset.class);
            			if (asset != null) {
            				if (!DamUtil.isImage(asset)) {
            					String dcFormat = asset.getMetadata().get(DamConstants.DC_FORMAT) != null
            							? asset.getMetadata().get(DamConstants.DC_FORMAT).toString()
            							: null;
            					if (dcFormat.contains(S7damConstants.S7_MIXED_MEDIA_SET)) {
            						List<Asset> assetlist = AssetUtils.buildAssetList(assetSelectionReference, getResourceResolver());
            						if (assetlist.size() > 0) {
            							this.imageBackgroundPathMobile = assetlist.get(0).getPath();
            						}
            					}
            				} else {
            					this.imageBackgroundPathMobile = assetSelectionReference;
            				}
            			}
            		}
                }
	                
            }
        } catch (RepositoryException e) {
        	LOGGER.error(e.getMessage());
        }
		return id;
	}

	/**
	 * @return Button
	 */
	private Button getButton(String path) {
		final Resource res = getResource().getChild(path);

		if (res != null) {
			final ValueMap properties = res.getValueMap();
			String link = properties.get("reference", String.class);
			
			if(geomarket != null) {
				String value = getProperties().get(path + "Link" + geomarket.toUpperCase(), String.class);
				if(StringUtils.isNotEmpty(value)) {
					link = value;
				}
			}
			
			Button btn = new Button(properties.get("titleDesktop", String.class), properties.get("titleTablet", String.class),
					link, properties.get("color", String.class),
					properties.get("analyticType", String.class), properties.get("size", String.class));
			
			return btn;
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

	public String getImageBackgroundPath() {
		return imageBackgroundPath;
	}
	
	public String getImageBackgroundPathMobile() {
		return imageBackgroundPathMobile;
	}

	public String getInlineGalleryID() {
		return inlineGalleryID;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getBackground() {
		return background;
	}

}