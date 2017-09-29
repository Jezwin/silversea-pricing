package com.silversea.aem.components.voyageJournals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.silversea.aem.filter.VoyageJournalPageFilter;
import com.silversea.aem.models.VoyageJournalModel;

public class VoyageJournalsListUse extends WCMUsePojo {
    private List<VoyageJournalModel> voyagesCurrent;
    private List<List<VoyageJournalModel>> voyageJournalPartition;
    private Integer currentPageIndex;
    private Integer lastDay;

    @Override
    public void activate() throws Exception {
        final InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(getResource());
        final Integer limit = inheritanceValueMap.getInherited("paginationLimit", 10);

        String currentPageParam = getRequest().getRequestParameter("page") != null ? getRequest().getRequestParameter("page").toString() : "1";
        currentPageIndex = Integer.parseInt(currentPageParam);

        final Iterator<Page> voyageJournalPages = getCurrentPage().listChildren(new VoyageJournalPageFilter(), true);

        // Get every voyages journal
        List<VoyageJournalModel> voyages = new ArrayList<>();

        while (voyageJournalPages.hasNext()) {
            Page voyageJournal = voyageJournalPages.next();

            if (voyageJournal != null) {
                voyages.add(voyageJournal.adaptTo(VoyageJournalModel.class));
            }
        }

        // Get voyage journal for the given page only
        voyageJournalPartition = new ArrayList<>();
        voyageJournalPartition = Lists.partition(voyages, limit);

        if (currentPageIndex - 1 < voyageJournalPartition.size()) {
            voyagesCurrent = new ArrayList<>();
            voyagesCurrent = voyageJournalPartition.get(currentPageIndex - 1);
        }

        // Test if the current page is a month page blog
        if (getCurrentPage().getName().length() <= 2) {
            final String month = getCurrentPage().getName(); // month format : MM
            final String year = getCurrentPage().getParent().getName(); // month format : yyyy

            final Date convertedDate = new SimpleDateFormat("MMyyyy").parse(month + year);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(convertedDate);

            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * @return the voyagesCurrent
     */
    public List<VoyageJournalModel> getVoyagesCurrent() {
        return voyagesCurrent;
    }

    /**
     * @return the currentPageIndex
     */
    public Integer getCurrentPageIndex() {
        return currentPageIndex;
    }

    /**
     * @return the NextPageIndex
     */
    public List<List<VoyageJournalModel>> getVoyageJournalPartition() {
        return voyageJournalPartition;
    }

    /**
     * @return the previousPageIndex
     */
    public Integer getPreviousPageIndex() {
        return currentPageIndex > 1 ? currentPageIndex - 1 : null;
    }

    /**
     * @return the NextPageIndex
     */
    public Integer getNextPageIndex() {
        return currentPageIndex < voyageJournalPartition.size() ? currentPageIndex + 1 : null;
    }

    /**
     * @return the Pagination
     */
    public List<Integer> getPagination() {
        Integer pageFrom = currentPageIndex > 2 ? currentPageIndex - 2 : 1 ;
        Integer pageTo = currentPageIndex < voyageJournalPartition.size() - 2 ? currentPageIndex + 2 : voyageJournalPartition.size();

        // Build integer list filled with int between pageFrom and pageTo
        return IntStream.rangeClosed(pageFrom, pageTo).boxed().collect(Collectors.toList());
    }

    /**
     * @return the lastDay
     */
    public Integer getLastDay() {
        return lastDay;
    }
}