package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.models.ComboCruiseModel;
import com.silversea.aem.services.GeolocationService;
import com.silversea.aem.utils.PathUtils;

public class ComboCruiseUse extends WCMUsePojo {

    private ComboCruiseModel comboCruiseModel;

    @Override
    public void activate() throws Exception {
        comboCruiseModel = getCurrentPage().adaptTo(ComboCruiseModel.class);
    }

    public ComboCruiseModel getComboCruiseModel() {
        return comboCruiseModel;
    }

    /**
     * Return path for request quote page
     */
    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage().getLanguage(false));
    }

}