package com.silversea.aem.components.included.lightboxes;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.ShipModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

public class LightboxDeckUse extends WCMUsePojo {

    private ShipModel ship;

    @Override
    public void activate() throws Exception {
        ship = retrieveShipModel();
    }

    private ShipModel retrieveShipModel() {
        Page currentPage = getCurrentPage();
        ValueMap propertiesPage = currentPage.getProperties();
        if (propertiesPage != null) {
            String shipReference = propertiesPage.get("shipReference", String.class);
            if (StringUtils.isNotEmpty(shipReference)) {
                Page shipPage = getPageManager().getPage(shipReference);
                if (shipPage != null) {
                    return shipPage.adaptTo(ShipModel.class);
                }
            }
        }
        return null;
    }

    public ShipModel getShip() {
        return ship;
    }
}
