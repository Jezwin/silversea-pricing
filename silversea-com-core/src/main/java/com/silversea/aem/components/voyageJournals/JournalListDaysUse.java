package com.silversea.aem.components.voyageJournals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.drew.lang.StringUtil;
import com.silversea.aem.models.JournalListDaysModel;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class JournalListDaysUse extends WCMUsePojo {

    private List<JournalListDaysModel> listDays;
    private JournalListDaysModel currentDay;

    Map<Integer, JournalListDaysModel> map = new HashMap<Integer, JournalListDaysModel>();

    @Override
    public void activate() throws Exception {
        listDays = new ArrayList<>();
        currentDay = getCurrentPage().adaptTo(JournalListDaysModel.class);
        Page parentPage = getCurrentPage().getParent();
        Iterator<Page> childs = parentPage.listChildren();
        while (childs.hasNext()) {
            listDays.add(childs.next().adaptTo(JournalListDaysModel.class));
        }

        Iterator<Page> childs1 = parentPage.listChildren();
        int i = 1;
        while (childs1.hasNext()) {
            map.put(i++, childs1.next().adaptTo(JournalListDaysModel.class));
        }
    }

    public List<JournalListDaysModel> getListDays() {
        return listDays;
    }

    public boolean isFirst() {
        if (listDays.get(0).getDayNumber() == currentDay.getDayNumber()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLast() {
        if (listDays.get(listDays.size() - 1).getDayNumber() == currentDay.getDayNumber()) {
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
    }

}
