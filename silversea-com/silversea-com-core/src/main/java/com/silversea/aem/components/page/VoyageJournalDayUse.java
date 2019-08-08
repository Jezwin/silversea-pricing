package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.VoyageJournalDayModel;

/**
 * Created by mbennabi on 17/02/2017.
 */
public class VoyageJournalDayUse extends WCMUsePojo {

    private VoyageJournalDayModel voyageJournalDay;

    @Override
    public void activate() throws Exception {
        voyageJournalDay = getCurrentPage().adaptTo(VoyageJournalDayModel.class);
    }

    public VoyageJournalDayModel getVoyageJournalDay() {
        return voyageJournalDay;
    }
}