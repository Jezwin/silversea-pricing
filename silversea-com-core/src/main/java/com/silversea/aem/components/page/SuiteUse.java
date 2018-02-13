package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.SilverseaAsset;
import com.silversea.aem.models.SuiteModel;
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
	private List<SilverseaAsset> completeSuitesAssetsList = new ArrayList<>();;
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
        
        for(Page suitePage : suitePagesList) {
        	String shipLabel = suitePage.getParent(2).getNavigationTitle().toUpperCase();
        	suiteModel = suitePage.adaptTo(SuiteModel.class);
        	List<Asset> suiteAssets = new ArrayList<>();
        	List<SilverseaAsset> suitesAssetsList = new ArrayList<>();

    		if (StringUtils.isNotBlank(suiteModel.getAssetSelectionReference())) {
    			suiteAssets
    					.addAll(AssetUtils.buildAssetList(suiteModel.getAssetSelectionReference(), getResourceResolver()));

	    		for (Asset asset : suiteAssets) {
	    			SilverseaAsset sscAsset = new SilverseaAsset();
	    			sscAsset.setPath(asset.getPath());
	    			sscAsset.setName(asset.getName());
	    			sscAsset.setLabel(shipLabel);
	    			suitesAssetsList.add(sscAsset);
	    		}
	    		shipSuiteAsset.put(shipLabel, suitesAssetsList);
    		}
        }
        
        int i = 0;
        for (Map.Entry<String, List<SilverseaAsset>> entry : shipSuiteAsset.entrySet()) {
        	if(i < 4) {
        		List<SilverseaAsset> value = entry.getValue();
        		if(!value.isEmpty()) {
                    initialSuitesAssetsList.add(value.get(0));
                    value.remove(0);
                    i++;
        		}
        	}
        }
        
        for (Map.Entry<String, List<SilverseaAsset>> entry : shipSuiteAsset.entrySet()) {
        		List<SilverseaAsset> value = entry.getValue();
        		if(!value.isEmpty()) {
        			completeSuitesAssetsList.addAll(0, value);
        		}
        }
        
        List<SilverseaAsset> mergedSuitesAssetsList = new ArrayList<>();
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
    
    public Map<String, List<SilverseaAsset>> getAllMapAssetForSuites() {
		return allShipSuiteAsset;
	}
    
    public List<SilverseaAsset> getInitialSuitesAssetsList() {
		return initialSuitesAssetsList;
	}
}