package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.OfferPriorityModel;

import java.util.List;
import java.util.Map;

public class FindYourCruiseOfferHelper extends AbstractSilverUse {

    @Override
    public void activate() throws Exception {
        List<CruiseItem> cruises = get("cruises", List.class);
        List<OfferPriorityModel> priorityOffer = get("priorityOffer", List.class);
    }
}
