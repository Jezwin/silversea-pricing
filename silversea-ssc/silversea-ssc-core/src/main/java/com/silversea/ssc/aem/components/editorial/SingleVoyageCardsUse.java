package com.silversea.ssc.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;

public class SingleVoyageCardsUse extends AbstractGeolocationAwareUse {

	private List<CruiseItem> voyages;
	private CruisesCacheService cruisesCacheService;

	@Override
	public void activate() throws Exception {
		super.activate();
		cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);
		String propertyKey = "voyage" + super.geomarket.toUpperCase();

		String listVoyages = getCurrentStyle().get(propertyKey, String.class);

		listVoyages = getProperties().get(propertyKey, String.class) != null
				? getProperties().get(propertyKey, String.class)
				: listVoyages;

		Locale locale = getCurrentPage().getLanguage(false);
		if (StringUtils.isNotEmpty(listVoyages)) {
			String[] splitList = listVoyages.split(",");
			Map<String, CruiseItem> mapVoyages = new HashMap<>();
			for (String key : splitList) {
				mapVoyages.put(key, null);
			}
			String lang = LanguageHelper.getLanguage(getCurrentPage());
			List<CruiseModelLight> allCruises = (cruisesCacheService != null) ? cruisesCacheService.getCruises(lang)
					: null;
			CruiseItem cruiseItem = null;
			for (CruiseModelLight cruise : allCruises) {
				if (mapVoyages.containsKey(cruise.getCruiseCode())) {
					cruiseItem = new CruiseItem(cruise, super.geomarket, super.currency, locale);
					mapVoyages.put(cruiseItem.getCruiseModel().getCruiseCode(), cruiseItem);
				}
			}
			voyages = new ArrayList();
			// we need to preserv the order

			for (String key : splitList) {
				if (mapVoyages.get(key) != null) {
					voyages.add(mapVoyages.get(key));

				}
			}

		}
	}

	public List<CruiseItem> getVoyages() {
		return voyages;
	}

	public CruiseItem getFirstElement() {
		return voyages.size() > 0 ? voyages.get(0) : null;
	}

}
