package com.silversea.ssc.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.ssc.aem.models.DestinationModelMap;

public class DestinationsMapUse extends WCMUsePojo {
	private ArrayList<Page> subPage = new ArrayList<Page>();
	private ArrayList<DestinationModelMap> destinationsList  = new ArrayList<DestinationModelMap>();

	@Override
	public void activate() throws Exception {
		String destPageRoot = (String) getProperties().get("reference");
		String style = (String) getProperties().get("style");
		if (destPageRoot != null) {
			Resource res = getResourceResolver().resolve(destPageRoot);
			if (res != null) {
				Page rootPage = res.adaptTo(Page.class);
				
				if (rootPage != null) {
					Iterator<Page> it = rootPage.listChildren(new PageFilter());
					while (it.hasNext()) {
						Page currentPage = it.next();
						String currentTemplate = currentPage.getProperties().get("cq:template", "");
						if (currentPage.getProperties().get("cq:template", "").equals("/apps/silversea/silversea-com/templates/destination")) {
							subPage.add(currentPage);
							DestinationModelMap tmpModel = currentPage.adaptTo(DestinationModelMap.class);
							if (tmpModel != null) {
								if(style.equals("style2")){
									tmpModel.xPosition = tmpModel.xPosition - 3;
									tmpModel.yPosition = tmpModel.yPosition - 3;
								}
								destinationsList.add(tmpModel);
							}
						}
					}
				}
			}
		}
	}
	
	public List<Page> getSubPage() {
        return subPage;
    }
	
	
	public List<DestinationModelMap> getDestinationsList() {
        return destinationsList;
    }

}
