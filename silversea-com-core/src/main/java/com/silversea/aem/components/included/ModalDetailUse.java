package com.silversea.aem.components.included;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.ModalDetailBean;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.ComboCruiseModel;
import com.silversea.aem.models.DiningModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.models.PublicAreaModel;
import com.silversea.aem.models.SuiteModel;
import com.silversea.aem.models.SuiteVariationModel;
import com.silversea.aem.utils.PathUtils;

public class ModalDetailUse extends WCMUsePojo {

	private ModalDetailBean detail;
	private Map<String, String> type =new HashMap<>();

	@Override
	public void activate() throws Exception {
		String key = null;
		String suffix = getRequest().getRequestPathInfo().getSuffix();
		String selector = getRequest().getRequestPathInfo().getSelectorString();
		
		if (getCurrentPage().getPath().contains("/destinations/")) { 
			key = "suite";
			ComboCruiseModel comboCruiseModel = getCurrentPage().adaptTo(ComboCruiseModel.class);
			SuiteModel suiteModel = null;
			PriceModel priceModel = null;
			Locale locale = getCurrentPage().getLanguage(false);
			for (PriceModel p : comboCruiseModel.getPrices()) {
				if(p.getSuiteCategory().equalsIgnoreCase(suffix)) {
					suiteModel = p.getSuite(); 
					priceModel= p;
				}
			}
			if (suiteModel != null && priceModel != null) {
				detail = new ModalDetailBean();
				detail.setTitle(suiteModel.getTitle());
				detail.setLongDescription(suiteModel.getLongDescription());
				detail.setBedroomsInformation(suiteModel.getBedroomsInformation());
				detail.setAssetSelectionReference(suiteModel.getAssetSelectionReference());
				detail.setPlan(suiteModel.getPlan());
				detail.setLocationImage(suiteModel.getLocationImage());
				detail.setVirtualTour(suiteModel.getVirtualTour());
				detail.setFileReference(suiteModel.getSuiteReference());
				detail.setFeatures(suiteModel.getFeatures());
				detail.setLowestPrice(priceModel);
				String computedPriceFormated = PriceHelper.getValue(locale, priceModel.getComputedPrice());
				detail.setComputedPriceFormated(computedPriceFormated);
			}
		} else if (getCurrentPage().getPath().contains("/suites/")) {
			key = "suite";
			SuiteVariationModel suiteVariation = getCurrentPage().adaptTo(SuiteVariationModel.class);
			if (suiteVariation != null) {
				detail = new ModalDetailBean();
				detail.setTitle(suiteVariation.getTitle());
				detail.setLongDescription(suiteVariation.getLongDescription());
				detail.setBedroomsInformation(suiteVariation.getBedroomsInformation());
				detail.setAssetSelectionReference(suiteVariation.getAssetSelectionReference());
				detail.setPlan(suiteVariation.getPlan());
				detail.setLocationImage(suiteVariation.getLocationImage());
				detail.setVirtualTour(suiteVariation.getVirtualTour());
				detail.setFileReference(suiteVariation.getSuiteReference());
				detail.setFeatures(suiteVariation.getFeatures());
				detail.setShipId(suiteVariation.getShipId());
			}
		} else if (getCurrentPage().getPath().contains("/dinings/")) {
			key = "dining";
			PublicAreaModel publicArea = getCurrentPage().adaptTo(PublicAreaModel.class);
			if (publicArea != null) {
				detail = new ModalDetailBean();
				detail.setTitle(publicArea.getTitle());
				detail.setLongDescription(publicArea.getLongDescription());
				detail.setAssetSelectionReference(publicArea.getAssetSelectionReference());
				detail.setVirtualTour(publicArea.getVirtualTour());
				detail.setFileReference(publicArea.getThumbnail());
				detail.setShipId(publicArea.getShipId());
			}
		} else if (getCurrentPage().getPath().contains("/public-areas/")) {
			key = "public-areas";
			DiningModel dining = getCurrentPage().adaptTo(DiningModel.class);
			if (dining != null) {
				detail = new ModalDetailBean();
				detail.setTitle(dining.getTitle());
				detail.setLongDescription(dining.getLongDescription());
				detail.setAssetSelectionReference(dining.getAssetSelectionReference());
				detail.setVirtualTour(dining.getVirtualTour());
				detail.setFileReference(dining.getThumbnail());
				detail.setShipId(dining.getShipId());
			}
		}
		if (StringUtils.isNotEmpty(key)) {
			type.put("overview", key + "-overview");
			type.put("plan", key + "-plan");
			type.put("features", key + "-features");
			type.put("location", key + "-location");
			type.put("virtual-tour", key + "-virtual-tour");
		}
	}

	public ModalDetailBean getDetail() {
		return detail;
	}

	public String getRaqLink() {
		return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
	}

	public String getSelector() {
		return WcmConstants.SELECTOR_SINGLE_SHIP;
	}

	public String getSuffix() {
		return getCurrentPage().getProperties().get(WcmConstants.PN_SHIP_ID, String.class) + WcmConstants.HTML_SUFFIX;
	}

	public Map<String, String> getType() {
		return type;
	}
	
	public boolean showLastMobileRaq() {
		return (detail != null) && (detail.getPlan() != null || detail.getFeatures() != null || detail.getLocationImage() != null);
	}
}
