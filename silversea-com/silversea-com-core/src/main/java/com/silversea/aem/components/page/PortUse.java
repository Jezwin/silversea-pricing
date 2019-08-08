package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.PortModel;

/**
 * Created by aurelienolivier on 12/02/2017.
 */
public class PortUse extends WCMUsePojo {

    private PortModel portModel;

    @Override
    public void activate() throws Exception {
        portModel = getCurrentPage().adaptTo(PortModel.class);
    }

    public PortModel getPortModel() {
        return portModel;
    }
}
