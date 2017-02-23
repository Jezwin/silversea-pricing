package com.silversea.aem.components.voyageJournals;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.JournalArchiveModel;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class JournalArchiveModelUse extends WCMUsePojo {

    private JournalArchiveModel journalArchive;

    @Override
    public void activate() throws Exception {
        journalArchive = getCurrentPage().adaptTo(JournalArchiveModel.class);
    }

	public JournalArchiveModel getVoyageJournalDay() {
		return journalArchive;
	}


}
