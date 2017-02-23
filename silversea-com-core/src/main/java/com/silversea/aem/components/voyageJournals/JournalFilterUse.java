package com.silversea.aem.components.voyageJournals;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.JournalFilterModel;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class JournalFilterUse extends WCMUsePojo {

    private JournalFilterModel journalFilter;

    @Override
    public void activate() throws Exception {
        journalFilter = getCurrentPage().adaptTo(JournalFilterModel.class);
    }

	public JournalFilterModel getVoyageJournalDay() {
		return journalFilter;
	}


}
