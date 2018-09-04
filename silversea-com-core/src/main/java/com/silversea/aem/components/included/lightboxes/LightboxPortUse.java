package com.silversea.aem.components.included.lightboxes;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.utils.CruiseUtils;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LightboxPortUse extends AbstractGeolocationAwareUse {

    private ItineraryModel portItinerary;


    @Override
    public void activate() throws Exception {
        super.activate();
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        portItinerary = retrievePortItineraryModel(selectors);
    }

    private ItineraryModel retrievePortItineraryModel(String[] selectors) {
        if (ArrayUtils.isNotEmpty(selectors)) {
            String itineraryId = selectors[2];
            Resource currentResource = getCurrentPage().getContentResource();
            Resource itiResource = currentResource.hasChildren() ? currentResource.getChild("itineraries") : null;
            if (itiResource != null) {
                Resource selectedIti = itiResource.getChild(itineraryId);
                if(selectedIti != null) {
                    ItineraryModel itiModel = selectedIti.adaptTo(ItineraryModel.class);
                    return itiModel;
                }
            }
        }
        return null;
    }

    public ItineraryModel getPortItinerary() {
        return portItinerary;
    }
}
