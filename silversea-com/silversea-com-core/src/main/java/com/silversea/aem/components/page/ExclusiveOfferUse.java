package com.silversea.aem.components.page;

import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.utils.PathUtils;

public class ExclusiveOfferUse extends EoHelper {

    private boolean available;

    private ExclusiveOfferItem exclusiveOfferItem;

    @Override
    public void activate() throws Exception {
        super.activate();
        final ExclusiveOfferModel exclusiveOfferModel = getCurrentPage().adaptTo(ExclusiveOfferModel.class);
        if (exclusiveOfferModel != null && exclusiveOfferModel.getGeomarkets().contains(geomarket)) {
            available = true;
        }

        if (hasSelector("modalcontent")) {
            EoConfigurationBean eoConfig = new EoConfigurationBean();
            eoConfig.setTitleLigthbox(true);
            eoConfig.setDescriptionLigthbox(true);
            eoConfig.setFootnotesMain(true);
            eoConfig.setFootnoteVoyage(true);
            eoConfig.setActiveSystem(exclusiveOfferModel.getActiveSystem());
            EoBean result = super.parseExclusiveOffer(eoConfig, exclusiveOfferModel);
            exclusiveOfferItem = new ExclusiveOfferItem(exclusiveOfferModel, countryCode, null, result);
        } else {
            exclusiveOfferItem = new ExclusiveOfferItem(exclusiveOfferModel, countryCode, null, null);
        }
    }

    public boolean isAvailable() {
        return available;
    }

    /**
     * @return the exclusiveOfferItem
     */
    public ExclusiveOfferItem getExclusiveOfferItem() {
        return exclusiveOfferItem;
    }

    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
    }
}
