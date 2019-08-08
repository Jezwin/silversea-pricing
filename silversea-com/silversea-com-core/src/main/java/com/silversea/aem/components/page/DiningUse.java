package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.DiningModel;
import com.silversea.aem.models.SilverseaAsset;
import com.silversea.aem.utils.AssetUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO merge with {@link SuiteUse}
 *
 * @author mjedli
 */
public class DiningUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiningUse.class);
    private DiningModel diningModel;
    private List<Page> diningReferenceList = new ArrayList<>();
    private List<Page> diningPagesList = new ArrayList<>();
	private List<SilverseaAsset> diningAssetsList = new ArrayList<>();
    private Map<String, List<SilverseaAsset>> allDiningAsset = new HashMap<String, List<SilverseaAsset>>();

    @Override
    public void activate() throws Exception {
        Iterator<Resource> resources = getResourceResolver().findResources(
                "//element(*, cq:Page)[" +
                        "jcr:content/@sling:resourceType=\"silversea/silversea-com/components/pages/diningvariation\"" +
                        " and jcr:content/@diningReference=\"" + getCurrentPage().getPath() + "\"]",
                "xpath");

        while (resources.hasNext()) {
            Page page = resources.next().adaptTo(Page.class);

            if (page != null && page.getParent(2) != null) {
                LOGGER.debug("Found page {} on ship {}", page.getPath(), page.getParent(2).getPath());

                diningReferenceList.add(page.getParent(2));
                diningPagesList.add(page);
            }
        }
        diningPagesList.add(getCurrentPage());
                
		String assetReference = getProperties().get("assetSelectionReference", String.class);
		Resource resourceAsset = getResourceResolver().getResource(assetReference);
        if (resourceAsset != null) {
        	Asset asset = resourceAsset.adaptTo(Asset.class);
        	if(asset !=null) {
        		if (!DamUtil.isImage(asset)) {
        			String dcFormat = asset.getMetadata().get(DamConstants.DC_FORMAT) != null ? asset.getMetadata().get(DamConstants.DC_FORMAT).toString() : null;
        			if (dcFormat.contains(S7damConstants.S7_MIXED_MEDIA_SET)) {
        				List<Asset> assetlist = AssetUtils.buildAssetList(assetReference, getResourceResolver());
        				for (Asset ass : assetlist) {
        					SilverseaAsset sscAsset = new SilverseaAsset();
        	    			sscAsset.setPath(ass.getPath());
        	    			sscAsset.setName(ass.getName());
        	    			sscAsset.setLabel(ass.getMetadataValue("dc:title"));
        	    			
        	    			diningAssetsList.add(sscAsset);
        	    			
        				}
        				
        			}
        		} 
        	}
        }
        
        
        allDiningAsset.put("DINING", diningAssetsList);
    }

    public List<Page> getDiningReferenceList() {
        return diningReferenceList;
    }
    
    public List<SilverseaAsset> getAllAssetForDining() {
		return diningAssetsList;
	}
    
    public Map<String, List<SilverseaAsset>> getAllMapAssetForDining() {
		return allDiningAsset;
	}
}