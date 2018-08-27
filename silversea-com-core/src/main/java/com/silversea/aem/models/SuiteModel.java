package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.utils.AssetUtils;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Page.class)
public class SuiteModel implements ShipAreaModel {

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE) @Optional
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String assetSelectionReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/bedroomsInformation") @Optional
    private String bedroomsInformation;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/plan") @Optional
    private String plan;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/locationImage") @Optional
    private String locationImage;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/virtualTour") @Optional
    private String virtualTour;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/image/fileReference") @Optional
    private String thumbnail;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteFeature") @Optional
    private String[] features;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteReference") @Optional
    private String suiteReference;

	// TODO replace by injector
    private SuiteModel genericSuite;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteSubTitle") @Optional
    private String suiteSubTitle;
    
    @Inject @Named(JcrConstants.JCR_CONTENT + "/suitesubtitleUpTo") @Optional
    private String suitesubtitleUpTo;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteSizeFeet") @Optional
    private String suiteSizeFeet;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteSizeMeter") @Optional
    private String suiteSizeMeter;

    private List<Asset> assets = new ArrayList<>();

    private String name;

    @PostConstruct
    private void init() {
        final PageManager pageManager = page.getPageManager();

        // init reference
        if (suiteReference != null) {
            final Page genericSuitePage = pageManager.getPage(suiteReference);

            if (genericSuitePage != null) {
                genericSuite = genericSuitePage.adaptTo(SuiteModel.class);
            }
        }

        name = page.getName();

        if (assetSelectionReference != null) {
            assets.addAll(AssetUtils.buildAssetList(assetSelectionReference, page.getContentResource().getResourceResolver()));
        }
    }

    public String getTitle() {
        return title;
    }

    public String getSuitesubtitleUpTo() {
		return suitesubtitleUpTo;
	}

    public String getSuiteSizeFeet() {
		return suiteSizeFeet;
	}

    public String getSuiteSizeMeter() {
		return suiteSizeMeter;
	}

	public String getLongDescription() {
        return longDescription != null ? longDescription :
                (genericSuite != null ? genericSuite.getLongDescription() : null);
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference != null ? assetSelectionReference :
                (genericSuite != null ? genericSuite.getAssetSelectionReference() : null);
    }

    public String getBedroomsInformation() {
        return bedroomsInformation;
    }

    public String getPlan() {
        return plan;
    }

    public String[] getFeatures() {
        return features;
    }
    
    public String getFeaturesString() {
    	StringBuilder featuresString = new StringBuilder();
    	for(int i=0; i < features.length; i++) {
    		featuresString.append(features[i]);
    		if (i < features.length -1) {
    			featuresString.append("#next#");
    		}
    	}
        return featuresString.toString();
    }

    public String getLocationImage() {
        return locationImage;
    }

    public String getVirtualTour() {
        return virtualTour;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public Asset getFirstAsset() {
        return assets.size() > 0 ? assets.get(0) : null;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSuiteSubTitle() {
        return suiteSubTitle;
    }

    public Page getPage() {
        return page;
    }

    public String getName() {
        return name;
    }
    
    public String getSuiteReference() {
		return suiteReference;
	}
}
