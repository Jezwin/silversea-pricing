package com.silversea.ssc.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.ExclusiveOfferModel;

public class EoTnCUse extends EoHelper {

	private List<EoBean> exclusiveOfferList;

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
					eoConfig.setShortTitleMain(true);
					eoConfig.setDescriptionTnC(true);

					exclusiveOfferList = new ArrayList();

					Iterator<Page> it = rootPage.listChildren(new PageFilter());
					while (it.hasNext()) {
						ExclusiveOfferModel currentEO = it.next().adaptTo(ExclusiveOfferModel.class);
						 if(currentEO.getGeomarkets().contains(geomarket)){
							eoConfig.setActiveSystem(currentEO.getActiveSystem());
	
							EoBean tncEntry = super.parseExclusiveOffer(eoConfig, currentEO);
							if (tncEntry != null) {
								exclusiveOfferList.add(tncEntry);
							}
						 }
					}
				}
			}
		}
	}

	public List<EoBean> getExclusiveOfferList() {
		return exclusiveOfferList;
	}

}
