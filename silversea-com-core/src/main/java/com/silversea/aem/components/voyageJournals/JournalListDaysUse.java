package com.silversea.aem.components.voyageJournals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.JournalListDaysModel;

/**
 * Created by mbennabi on 20/02/2017.
 */
public class JournalListDaysUse extends WCMUsePojo {

    private List<JournalListDaysModel> listDays;
    private JournalListDaysModel currentDay;

    @Override
    public void activate() throws Exception {
        listDays = new ArrayList<>();
        currentDay = getCurrentPage().adaptTo(JournalListDaysModel.class);
        Page parentPage = getCurrentPage().getParent();
        Iterator<Page> childs = parentPage.listChildren();
        while (childs.hasNext()) {
            listDays.add(childs.next().adaptTo(JournalListDaysModel.class));
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
        int currentIndex = listDays.indexOf(currentDay);
        String path;
        if(currentIndex !=-1){
            path = listDays.get(currentIndex+1).getPath();
            return path;
        }else{
            return "";
        }
       
    }

    public String getPreviousPath() {
        int currentIndex = listDays.indexOf(currentDay);
        String path;
        if(currentIndex !=-1){
            path = listDays.get(currentIndex-1).getPath();
            return path;
        }else{
            return "";
        }
       
    }
}
