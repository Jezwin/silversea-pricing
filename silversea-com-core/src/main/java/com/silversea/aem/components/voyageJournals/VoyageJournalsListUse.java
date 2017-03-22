package com.silversea.aem.components.voyageJournals;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class VoyageJournalsListUse extends WCMUsePojo {

    private List<Page> voyageJournalList;
    private Page currentDay;

    static final private Logger LOGGER = LoggerFactory.getLogger(VoyageJournalsListUse.class);
    //Map<Integer, VoyageJournalModel> map = new HashMap<Integer, VoyageJournalModel>();

    @Override
    public void activate() throws Exception {
        voyageJournalList = new ArrayList<>();
        currentDay = getCurrentPage(); //.adaptTo(VoyageJournalModel.class);
        Page parentPage = getCurrentPage().getParent();
        Iterator<Page> childs = getCurrentPage().listChildren();
        while (childs.hasNext()) {
            voyageJournalList.add(childs.next().adaptTo(Page.class));
        }
        LOGGER.debug("test loop {}", voyageJournalList.size());

        /*Iterator<Page> childs1 = getCurrentPage().listChildren();
        int i = 1;
        while (childs1.hasNext()) {
            map.put(i++, childs1.next().adaptTo(VoyageJournalModel.class));
        }*/
    }

    public List<Page> getVoyageJournalList() {
        return voyageJournalList;
    }

    public Page getCurrentDay() {
        return currentDay;
    }

    /*public boolean isFirst() {
        if (voyageJournalList.get(0).getDayNumber() == currentDay.getDayNumber()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLast() {
        if (voyageJournalList.get(voyageJournalList.size() - 1).getDayNumber() == currentDay.getDayNumber()) {
            return true;
        } else {
            return false;
        }
    }

    public String getNext() {
        Integer s = Integer.valueOf(currentDay.getDayNumber()) + 1;
        return s.toString();
    }

    public String getPrevious() {
        Integer s = Integer.valueOf(currentDay.getDayNumber()) - 1;
        return s.toString();
    }

    public String getNextPath() {
        String path = map.get(Integer.valueOf(currentDay.getDayNumber()) + 1).getPath();
        if (StringUtils.isNotEmpty(path)) {
            return path;
        } else {
            return "";
        }
    }

    public String getPreviousPath() {
        String path = map.get(Integer.valueOf(currentDay.getDayNumber()) - 1).getPath();
        if (StringUtils.isNotEmpty(path)) {
            return path;
        } else {
            return "";
        }
    }*/

}
