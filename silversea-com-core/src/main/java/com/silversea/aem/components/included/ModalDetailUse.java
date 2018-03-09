package com.silversea.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.ModalDetailBean;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.DiningModel;
import com.silversea.aem.models.PublicAreaModel;
import com.silversea.aem.models.SuiteVariationModel;
import com.silversea.aem.utils.PathUtils;

public class ModalDetailUse extends WCMUsePojo {

	private ModalDetailBean detail;

	@Override
	public void activate() throws Exception {
		
		if (getCurrentPage().getPath().contains("/suites/")) {
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
		return getCurrentPage().getProperties().get(WcmConstants.PN_SHIP_ID, String.class)
				+ WcmConstants.HTML_SUFFIX;
	}
}
