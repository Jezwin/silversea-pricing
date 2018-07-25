package com.silversea.aem.components.page;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.models.CruiseModel;

public class Cruise2018Use extends AbstractGeolocationAwareUse {

    @Override
    public void activate() throws Exception {
        super.activate();
    }

    private CruiseModel cruiseModel;

    public CruiseModel getCruiseModel() {
        return cruiseModel;
    }
}
