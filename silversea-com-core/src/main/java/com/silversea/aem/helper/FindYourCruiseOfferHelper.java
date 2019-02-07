package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.xfa.Int;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.components.beans.CruiseItemFYC;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.ExclusiveOfferModelLight;
import com.silversea.aem.models.OfferPriorityModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindYourCruiseOfferHelper extends AbstractSilverUse {

    private List<CruiseItemFYC> cruisesFYC;

    @Override
    public void activate() throws Exception {
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
                Integer priority;
                if (isCustomPriority) {
                    priority = Integer.valueOf(priorityOfferMap.get(exclusiveOffer.getPath()));
                } else {
                    priority = exclusiveOffer.getPriorityWeight();
                }
                if (priority > priorityWeight) {
                    priorityWeight = priority;
                    postPrice = cruise.getPostPriceMap().get(exclusiveOffer.getPath());
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
