package com.silversea.aem.helper;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.components.beans.CruiseItemFYC;
import com.silversea.aem.models.ExclusiveOfferModelLight;
import com.silversea.aem.models.OfferPriorityModel;
import com.silversea.aem.models.PriorityExclusiveOfferModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindYourCruiseOfferHelper extends AbstractGeolocationAwareUse {

    private List<CruiseItemFYC> cruisesFYC;

    @Override
    public void activate() throws Exception {
        super.activate();
        List<CruiseItem> cruises = get("cruises", List.class);
        List<OfferPriorityModel> priorityOffer = get("priorityOffer", List.class);
        final Map<String, String> priorityOfferMap = priorityOffer.stream().collect(Collectors.toMap(element -> element.getOfferPath(), element -> element.getPriority()));
        cruisesFYC = retrieveCruisesFYC(cruises, priorityOfferMap);
    }

    private List<CruiseItemFYC> retrieveCruisesFYC(List<CruiseItem> cruises, Map<String, String> priorityOfferMap) {
        List<CruiseItemFYC> cruisesFYC = new ArrayList<>();

        for (CruiseItem cruise : cruises) {
            List<ExclusiveOfferModelLight> exclusiveOffers = cruise.getExclusiveOffers();
            Integer priorityWeight = Integer.MIN_VALUE;
            String postPrice = null;
            for (ExclusiveOfferModelLight exclusiveOffer : exclusiveOffers) {
                boolean isCustomPriority = !priorityOfferMap.isEmpty() && priorityOfferMap.get(exclusiveOffer.getPath()) != null;
                Integer priority = Integer.MIN_VALUE;
                if (isCustomPriority) {
                    priority = Integer.valueOf(priorityOfferMap.get(exclusiveOffer.getPath()));
                } else if (exclusiveOffer.getPriorityWeight() != null) {
                    priority = exclusiveOffer.getPriorityWeight();
                }
                if (priority > priorityWeight && (cruise.getPostPriceMap().get(exclusiveOffer.getPath()) !=null)) {
                    priorityWeight = priority;
                    List<PriorityExclusiveOfferModel> priorityExclusiveOfferModelList = cruise.getPostPriceMap().get(exclusiveOffer.getPath());
                    for (PriorityExclusiveOfferModel priorityExclusiveOfferModel : priorityExclusiveOfferModelList) {
                        if (priorityExclusiveOfferModel.getMarket().equalsIgnoreCase("default")) {
                            postPrice = priorityExclusiveOfferModel.getPostPrice();
                        }
                        if(priorityExclusiveOfferModel.getMarket().equalsIgnoreCase(super.geomarket)) {
                            postPrice = priorityExclusiveOfferModel.getPostPrice();
                        }
                    }
                }
            }
             CruiseItemFYC cruiseItemFYC = new CruiseItemFYC(cruise, postPrice);
            cruisesFYC.add(cruiseItemFYC);
        }
        return cruisesFYC;
    }

    public List<CruiseItemFYC> getCruisesFYC() {
        return cruisesFYC;
    }
}
