package com.silversea.aem.components.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.SilverseaAsset;
import com.silversea.aem.models.SuiteModel;
import com.silversea.aem.utils.AssetUtils;

/**
 * TODO merge with {@link DiningUse}
 *
 * @author mbennabi 14/03/2017
 */
public class SuiteUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuiteUse.class);
    
    private SuiteModel suiteModel;
    private List<Page> suiteReferenceList = new ArrayList<>();
    private List<Page> suitePagesList = new ArrayList<>();
	private List<SilverseaAsset> initialSuitesAssetsList = new ArrayList<>();
	private List<SilverseaAsset> completeSuitesAssetsList = new ArrayList<>();
	private List<SilverseaAsset> mergedSuitesAssetsList = new ArrayList<>();;
	private Map<String, List<SilverseaAsset>> shipSuiteAsset = new HashMap<String, List<SilverseaAsset>>();
	private Map<String, List<SilverseaAsset>> allShipSuiteAsset = new HashMap<String, List<SilverseaAsset>>();;
	
    @Override
    public void activate() throws Exception {
        Iterator<Resource> resources = getResourceResolver().findResources(
                "//element(*, cq:Page)[" +
                        "jcr:content/@sling:resourceType=\"silversea/silversea-com/components/pages/suitevariation\"" +
                        " and jcr:content/@suiteReference=\"" + getCurrentPage().getPath() + "\"]",
                "xpath");

        while (resources.hasNext()) {
            Page page = resources.next().adaptTo(Page.class);

            if (page != null && page.getParent(2) != null) {
                LOGGER.debug("Found page {} on ship {}", page.getPath(), page.getParent(2).getPath());

                suiteReferenceList.add(page.getParent(2));
                suitePagesList.add(page);
            }
        }
        
		String assetReference = getProperties().get("assetSelectionReference", String.class);
		Resource resourceAsset = getResourceResolver().getResource(assetReference);
        if (resourceAsset != null) {
        	Asset asset = resourceAsset.adaptTo(Asset.class);
        	if(asset !=null) {
        		if (!DamUtil.isImage(asset)) {
        			String dcFormat = asset.getMetadata().get(DamConstants.DC_FORMAT) != null ? asset.getMetadata().get(DamConstants.DC_FORMAT).toString() : null;
        			if (dcFormat.contains(S7damConstants.S7_MIXED_MEDIA_SET)) {
        				List<Asset> assetlist = AssetUtils.buildAssetList(assetReference, getResourceResolver());
        			       int i = 0;
        				for (Asset ass : assetlist) {
        					SilverseaAsset sscAsset = new SilverseaAsset();
        	    			sscAsset.setPath(ass.getPath());
        	    			sscAsset.setName(ass.getName());
        	    			sscAsset.setLabel(ass.getMetadataValue("dc:title"));
        	    			if(i < 4)  {
        	    				initialSuitesAssetsList.add(sscAsset);
        	    			}else
        	    			{
        	    				completeSuitesAssetsList.add(sscAsset);
        	    			}
        	    			i++;
        				}
        				
        			}
        		} 
        	}
        }
                    
        mergedSuitesAssetsList.addAll(initialSuitesAssetsList);
        mergedSuitesAssetsList.addAll(completeSuitesAssetsList);
        allShipSuiteAsset.put("SUITES", mergedSuitesAssetsList);
    }

    public List<Page> getSuiteReferenceList() {
        return suiteReferenceList;
    }
    
    public List<Page> getSuitePagesList() {
        return suitePagesList;
    }
    
    public List<SilverseaAsset> getAllAssetForSuites() {
		return completeSuitesAssetsList;
	}
    
    public List<SilverseaAsset> getMergedSuitesAssetsList(){
    	return mergedSuitesAssetsList;
    }
    
    public Map<String, List<SilverseaAsset>> getAllMapAssetForSuites() {
		return allShipSuiteAsset;
	}
    
    public List<SilverseaAsset> getInitialSuitesAssetsList() {
		return initialSuitesAssetsList;
	}
    
}