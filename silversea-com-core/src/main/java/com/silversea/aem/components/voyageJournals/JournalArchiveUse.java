package com.silversea.aem.components.voyageJournals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.JournalArchiveModel;
import com.silversea.aem.models.JournalArchiveMonthModel;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class JournalArchiveUse extends WCMUsePojo {

    private JournalArchiveModel archive;
    private List<JournalArchiveModel> archiveYear;
    private List<JournalArchiveMonthModel> archiveMonth;

    @Override
    public void activate() throws Exception {
        archiveYear = new ArrayList<>();
        Resource resource = getResourceResolver().getResource(getProperties().get("archivePath", String.class));
        Page parentPage = resource.adaptTo(Page.class);
        Iterator<Page> childs = parentPage.listChildren();
        while (childs.hasNext()) {
            Page child = childs.next();
            archive = new JournalArchiveModel();
            archive.setTitle(child.getTitle());
            archive.setPath(child.getPath());
            Iterator<Page> childs1 = child.listChildren();
            archiveMonth = new ArrayList<>();
            while (childs1.hasNext()) {
                archiveMonth.add(childs1.next().adaptTo(JournalArchiveMonthModel.class));
            }
            archive.setArchiveMonth(archiveMonth);
            archiveYear.add(archive);
        }
    }

    public List<JournalArchiveModel> getArchiveYear() {
        return archiveYear;
    }
}