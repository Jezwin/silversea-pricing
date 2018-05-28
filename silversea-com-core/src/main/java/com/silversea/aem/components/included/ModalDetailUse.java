package com.silversea.aem.components.included;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.ModalDetailBean;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.DiningModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.models.PublicAreaModel;
import com.silversea.aem.models.SuiteVariationModel;
import com.silversea.aem.utils.CruiseUtils;
import com.silversea.aem.utils.PathUtils;

public class ModalDetailUse extends AbstractGeolocationAwareUse {

	private boolean showRaq = false;
	private ModalDetailBean detail;
	private Map<String, String> type = new HashMap<>();
	private String suffixResizeUrl;
	private String suffixUrl;
	private String selectorUrl;

	@Override
	public void activate() throws Exception {
		super.activate();
		String key = null;
		String[] selectors = getRequest().getRequestPathInfo().getSelectors();
		String resourceType = getProperties().get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, String.class);
		
		switch (resourceType) {
		case "silversea/silversea-com/components/pages/combocruise":
			if (selectors != null && selectors.length > 0) {
				String suiteCode = selectors[1];
				this.showRaq = true;
				key = "suite";
				List<PriceModel> prices = new ArrayList<>();
				Resource suitesResource = getCurrentPage().getContentResource().getChild("suites");
				if (suitesResource != null) {
					CruiseUtils.collectPrices(prices, suitesResource);
					prices.sort((o1, o2) -> -o1.getPrice().compareTo(o2.getPrice()));
				}
				SuitePrice suitePriceModel = null;
				Locale locale = getCurrentPage().getLanguage(false);

				for (PriceModel priceModel : prices) {
					if (priceModel.getGeomarket() != null && priceModel.getGeomarket().equals(geomarket)
							&& priceModel.getCurrency().equals(currency)) {

						if (priceModel.getSuiteCategory().equalsIgnoreCase(suiteCode)) {
							suitePriceModel = new SuitePrice(priceModel.getSuite(), priceModel, locale,
									priceModel.getSuiteCategory());
						}
						if (suitePriceModel != null && suitePriceModel.getSuite().equals(priceModel.getSuite())) {
							suitePriceModel.add(priceModel);
						}

					}
				}
				if (suitePriceModel != null) {
					detail = new ModalDetailBean();
					detail.setTitle(suitePriceModel.getSuite().getTitle());
					detail.setLongDescription(suitePriceModel.getSuite().getLongDescription());
					detail.setBedroomsInformation(suitePriceModel.getSuite().getBedroomsInformation());
					detail.setAssetSelectionReference(suitePriceModel.getSuite().getAssetSelectionReference());
					detail.setPlan(suitePriceModel.getSuite().getPlan());
					detail.setLocationImage(suitePriceModel.getSuite().getLocationImage());
					detail.setVirtualTour(suitePriceModel.getSuite().getVirtualTour());
					detail.setFileReference(suitePriceModel.getSuite().getSuiteReference());
					detail.setFeatures(suitePriceModel.getSuite().getFeatures());
					detail.setCurrency(suitePriceModel.getLowestPrice().getCurrency());
					detail.setComputedPriceFormated(suitePriceModel.getComputedPriceFormated());
					detail.setWaitList(suitePriceModel.isWaitList());
					suffixResizeUrl = suiteCode;
					suffixUrl = suitePriceModel.getSuite().getName() + WcmConstants.HTML_SUFFIX;
					selectorUrl = WcmConstants.SELECTOR_FYC_RESULT;
				}
			}
			break;
		case "silversea/silversea-com/components/pages/suitevariation":
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
			break;
		case "silversea/silversea-com/components/pages/publicareavariation":
			key = "public-areas";
			PublicAreaModel publicArea = getCurrentPage().adaptTo(PublicAreaModel.class);
			if (publicArea != null) {
				detail = new ModalDetailBean();
				String title = (StringUtils.isNotEmpty(publicArea.getNavigationTitle())) ? publicArea.getNavigationTitle() : publicArea.getTitle();
				detail.setTitle(title);
				detail.setLongDescription(publicArea.getLongDescription());
				detail.setAssetSelectionReference(publicArea.getAssetSelectionReference());
				detail.setVirtualTour(publicArea.getVirtualTour());
				detail.setFileReference(publicArea.getThumbnail());
				detail.setShipId(publicArea.getShipId());
			}
			break;
		case "silversea/silversea-com/components/pages/diningvariation":
			key = "dining";
			DiningModel dining = getCurrentPage().adaptTo(DiningModel.class);
			if (dining != null) {
				detail = new ModalDetailBean();
				String title = (StringUtils.isNotEmpty(dining.getNavigationTitle())) ? dining.getNavigationTitle() : dining.getTitle();
				detail.setTitle(title);
				detail.setLongDescription(dining.getLongDescription());
				detail.setAssetSelectionReference(dining.getAssetSelectionReference());
				detail.setVirtualTour(dining.getVirtualTour());
				detail.setFileReference(dining.getThumbnail());
				detail.setShipId(dining.getShipId());
			}
			break;
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
		return selectorUrl;
	}

	public String getSuffix() {
		return suffixUrl;
	}

	public Map<String, String> getType() {
		return type;
	}

	public boolean showLastMobileRaq() {
			return showRaq() && (detail != null) && (detail.getPlan() != null || detail.getFeatures() != null || detail.getLocationImage() != null);
	}

	public String getSuffixResizeUrl() {
		return suffixResizeUrl;
	}

	public boolean showRaq() {
		return showRaq;
	}
}
