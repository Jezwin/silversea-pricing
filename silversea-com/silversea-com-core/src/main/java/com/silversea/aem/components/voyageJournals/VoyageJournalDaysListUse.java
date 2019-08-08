package com.silversea.aem.components.voyageJournals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.aem.models.VoyageJournalDayModel;
import com.silversea.aem.models.VoyageJournalModel;

public class VoyageJournalDaysListUse extends WCMUsePojo {

    private List<VoyageJournalDayModel> voyageJournalDays = new ArrayList<>();
    private VoyageJournalModel voyageJournalDay;

    @Override
    public void activate() throws Exception {
        Iterator<Page> it = getCurrentPage().listChildren(new PageFilter());

        while (it.hasNext()) {
            Page page = it.next();
            voyageJournalDays.add(page.adaptTo(VoyageJournalDayModel.class));
        }

        voyageJournalDay = getCurrentPage().adaptTo(VoyageJournalModel.class);
    }

    /**
     * @return the voyageJournalDays
     */
    public List<VoyageJournalDayModel> getVoyageJournalDays() {
        return voyageJournalDays;
    }

    /**
     * @return the voyageJournalDay
     */
    public VoyageJournalModel getVoyageJournalDay() {
        return voyageJournalDay;
    }
}