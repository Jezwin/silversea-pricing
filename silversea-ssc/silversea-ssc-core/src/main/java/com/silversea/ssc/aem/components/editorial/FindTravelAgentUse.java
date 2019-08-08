package com.silversea.ssc.aem.components.editorial;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.aem.models.TravelAgencyModel;

public class FindTravelAgentUse extends WCMUsePojo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FindTravelAgentUse.class);

	private Integer heightDivListDesktop;
	private StringBuilder travelAgentList;

	@Override
	public void activate() throws Exception {
		heightDivListDesktop = (getProperties().get("heightMapDesktop") != null) ? getProperties().get(
				"heightMapDesktop", Integer.class) - 135 : 676;
		String reference = getProperties().get("reference", String.class);
		if(StringUtils.isNotEmpty(reference)) {
			travelAgentList = new StringBuilder();
			travelAgentList.append("[");
			Resource res = getResourceResolver().resolve(reference);
			if (res != null) {
				Page rootPage = res.adaptTo(Page.class);
				if(rootPage != null){
					Iterator<Page> it = rootPage.listChildren(new PageFilter());
					while(it.hasNext()) {
						Page currentPage = it.next();
						Iterator<Page> itChild = currentPage.listChildren(new PageFilter());
						while(itChild.hasNext()) {
							TravelAgencyModel travelAgent = itChild.next().adaptTo(TravelAgencyModel.class);
							if(travelAgent != null){
								travelAgentList.append("{");
								travelAgentList.append("'title':" + "'" + travelAgent.getTitle().replaceAll("(\r\n|\n|\r|\\\"|')", "").trim() + "',");
								travelAgentList.append("'agencyId':" + "'" + travelAgent.getAgencyId() + "',");
								travelAgentList.append("'address':" + "'" + travelAgent.getAddress().replaceAll("(\r\n|\n|\r|\\\"|')", "") + "',");
								travelAgentList.append("'city':" + "'" + travelAgent.getCity().replaceAll("(\r\n|\n|\r|\\\"|')", "") + "',");
								travelAgentList.append("'zip':" + "'" + travelAgent.getZip() + "',");
								travelAgentList.append("'zip4':" + "'" + travelAgent.getZip4() + "',");
								travelAgentList.append("'country':" + "'" + travelAgent.getCountry().replaceAll("(\r\n|\n|\r|\\\"|')", "") + "',");
								travelAgentList.append("'stateCode':" + "'" + travelAgent.getStateCode() + "',");
								travelAgentList.append("'phone':" + "'" + travelAgent.getPhone() + "',");
								travelAgentList.append("'latitude':" + "'" + travelAgent.getLatitude() + "',");
								travelAgentList.append("'longitude':" + "'" + travelAgent.getLongitude() + "'");
								travelAgentList.append("},");
							}
						}
					}
				}
			}
			travelAgentList.delete(travelAgentList.length()-1, travelAgentList.length());
			travelAgentList.append("]");
		}
	}
	public String getTravelAgentList() {
		return travelAgentList.toString();
	}
	
	public Integer getHeightDivListDesktop() {
		return heightDivListDesktop;
	}

}
