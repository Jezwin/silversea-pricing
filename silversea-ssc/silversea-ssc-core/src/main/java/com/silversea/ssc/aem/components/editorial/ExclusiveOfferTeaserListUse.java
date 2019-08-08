package com.silversea.ssc.aem.components.editorial;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.models.ExclusiveOfferVariedModel;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.attribute.standard.MediaSizeName;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExclusiveOfferTeaserListUse extends EoHelper {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOfferTeaserListUse.class);

    private List<ExclusiveOfferVariedModel> EOs;

    @Override
    public void activate() throws Exception {
    	super.activate();
        final String EOsPath = getProperties().get("folderReference", "");
        String offerOrder = "offerOrder" + super.geomarket.toUpperCase();

        String[] offerOrderProp = Optional.ofNullable(getProperties().get(offerOrder, String[].class)).orElse(Optional.ofNullable(getCurrentStyle().get(offerOrder, String[].class)).orElse
                (null));
        String[] categoryEO = getProperties().get("categoryEO", String[].class);

        Map<String,String> mapCategoryEo = null;
        if(ArrayUtils.isNotEmpty(categoryEO) ) {
        	mapCategoryEo = new HashedMap();
        	for(String c : categoryEO) {
        		mapCategoryEo.put(c, c);
        	}
        }

        final Map<String,Integer> mapOrderEo = new HashedMap();
        if(ArrayUtils.isNotEmpty(offerOrderProp) ) {
            int i =0;
            for(String eo : offerOrderProp) {
                mapOrderEo.put(eo, i++);
            }
        }

        LOGGER.debug("Searching EOs with market: {}", geomarket);

        // Searching for EOs
        EOs = new ArrayList<>();
        // get all EOs listed in order
        Page EOsRoot = getPageManager().getPage(EOsPath);

        // filter EOs by localization
        Iterator<Page> pages = EOsRoot.listChildren();
        filterEOs(pages, mapCategoryEo);

        if (!mapOrderEo.isEmpty()) {
            int sizeMap  = mapOrderEo.size() + 1;
            EOs.sort((e1,e2) -> mapOrderEo.getOrDefault(e1.getPath(),EOs.indexOf(e1)+ sizeMap).compareTo(mapOrderEo.getOrDefault(e2.getPath(),EOs.indexOf(e2)+ sizeMap)));
    }
    }

    public void filterEOs(Iterator<Page> pages,Map<String,String> mapCategoryEo) {
    	//Select only available EOs in current geoMarket and not ghost
        while (pages.hasNext()) {
            Page page = pages.next();
            if(page != null){
	            String currentTemplate = page.getProperties().get("cq:template", "");
				if (currentTemplate.equals("/apps/silversea/silversea-com/templates/exclusiveoffer")) {
					ExclusiveOfferVariedModel EO = page.adaptTo(ExclusiveOfferVariedModel.class);
					if(EO != null){
	                    if(EO.getGeomarkets().contains(geomarket)){
	                    	EO.setVariedDescription(EO.getDescription());
	                    	EO.setVariedTitle(EO.getTitle());
	                    	String[] category = EO.getCategoryEO();
	                    	boolean toInsert = (mapCategoryEo == null);
	                    	boolean isNotGhost = !EO.isGhostOffer();

	                    	if (ArrayUtils.isNotEmpty(category) && mapCategoryEo != null) {
	                    		for (String c : category) {
	                    			if (mapCategoryEo.containsKey(c)) {
	                    				toInsert = true;
	                    				break;
	                    			}
	                    		}
	                    	}
	                    	if (toInsert && isNotGhost) {
	                    		EOs.add(EO);
	                    	}
	                    }
					}
	            }
            }
        }

        List<ExclusiveOfferVariedModel> EOsTemp = new ArrayList<>(); ;
        //Update current EOModel with information from good variation (if one)
        for (ExclusiveOfferVariedModel exclusiveOfferModel : EOs) {
        	if(exclusiveOfferModel.getActiveSystem() == false){ //Still rely on the good old system
				//Check if there is a variation that apply
	        	List<ExclusiveOfferModel> availableEOVariation = exclusiveOfferModel.getVariations();
	        	for (ExclusiveOfferModel eoVar : availableEOVariation) {
	        		String fullGeo = String.join("||", eoVar.getGeomarkets());

					if(fullGeo.contains("/" + countryCode)){
						if(!eoVar.getDescription().isEmpty()){
							exclusiveOfferModel.setVariedDescription(eoVar.getDescription());
							exclusiveOfferModel.setVariedTitle(eoVar.getTitle());
						}
					}
				}
        	}else { //rely on the brand new system
        		EoConfigurationBean eoConfig = new EoConfigurationBean();
				eoConfig.setShortTitleMain(true);
				eoConfig.setShortDescriptionMain(true);
				eoConfig.setActiveSystem(exclusiveOfferModel.getActiveSystem());

				EoBean tncEntry = super.parseExclusiveOffer(eoConfig, exclusiveOfferModel);
				if (tncEntry != null) {
					exclusiveOfferModel.setVariedDescription(tncEntry.getShortDescription());
					exclusiveOfferModel.setVariedTitle(tncEntry.getShortTitle());
				}
        	}
        	EOsTemp.add(exclusiveOfferModel);
		}

        EOs = EOsTemp;
    }

    public List<ExclusiveOfferVariedModel> getEOs() {
        return EOs;
    }
}