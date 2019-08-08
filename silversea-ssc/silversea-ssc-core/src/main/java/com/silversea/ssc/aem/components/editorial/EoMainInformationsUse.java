package com.silversea.ssc.aem.components.editorial;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.ExclusiveOfferModel;

public class EoMainInformationsUse extends EoHelper {

	private EoBean exclusiveOffer;

	@Override
	public void activate() throws Exception {
		super.activate();
		String rootPath = getProperties().get("rootPath", String.class);
		if (!StringUtils.isEmpty(rootPath)) {
			Resource res = getResourceResolver().resolve(rootPath);

			if (res != null) {
				Page rootPage = res.adaptTo(Page.class);

				if (rootPage != null) {
					EoConfigurationBean eoConfig = new EoConfigurationBean();
					eoConfig.setTitleMain(true);
					eoConfig.setFootnotesMain(true);
					eoConfig.setDescriptionMain(true);

					ExclusiveOfferModel currentEO = rootPage.adaptTo(ExclusiveOfferModel.class);
					eoConfig.setActiveSystem(currentEO.getActiveSystem());
					exclusiveOffer = super.parseExclusiveOffer(eoConfig, currentEO);
				}
			}
		}
	}

	public EoBean getExclusiveOffer() {
		return exclusiveOffer;
	}

}
