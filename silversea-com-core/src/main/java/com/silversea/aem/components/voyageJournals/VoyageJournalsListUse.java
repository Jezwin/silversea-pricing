package com.silversea.aem.components.voyageJournals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.silversea.aem.filter.VoyageJournalPageFilter;
import com.silversea.aem.models.VoyageJournalModel;

public class VoyageJournalsListUse extends WCMUsePojo {
    private List<VoyageJournalModel> voyageJournalList;
    private List<List<VoyageJournalModel>> voyageJournalPartition;
    private Integer currentPageIndex;

    @Override
    public void activate() throws Exception {
        final InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(getResource());
        final Integer limit = inheritanceValueMap.getInherited("paginationLimit", 10);

        String currentPageParam = getRequest().getRequestParameter("page") != null ? getRequest().getRequestParameter("page").toString() : "1";
        currentPageIndex = Integer.parseInt(currentPageParam);

        final Iterator<Page> voyageJournalPages = getCurrentPage().listChildren(new VoyageJournalPageFilter(), true);
        List<VoyageJournalModel> voyageJournalListFull = new ArrayList<>();

        while (voyageJournalPages.hasNext()) {
            Page voyageJournal = voyageJournalPages.next();

            if (voyageJournal != null) {
                voyageJournalListFull.add(voyageJournal.adaptTo(VoyageJournalModel.class));
            }
        }

        voyageJournalPartition = new ArrayList<>();
        voyageJournalPartition = Lists.partition(voyageJournalListFull, limit);

        if (currentPageIndex - 1 < voyageJournalPartition.size()) {
            voyageJournalList = new ArrayList<>();
            voyageJournalList = voyageJournalPartition.get(currentPageIndex - 1);
        }
    }

    /**
     * @return the voyageJournalList
     */
    public List<VoyageJournalModel> getVoyageJournalList() {
        return voyageJournalList;
    }

    /**
     * @return the voyageJournalPartition
     */
    public List<List<VoyageJournalModel>> getVoyageJournalPartition() {
        return voyageJournalPartition;
    }

    /**
     * @return the currentPageIndex
     */
    public Integer getCurrentPageIndex() {
        return currentPageIndex;
    }

    /**
     * @return the previousPageIndex
     */
    public Integer getPreviousPageIndex() {
        return currentPageIndex != 1 ? currentPageIndex - 1 : null;
    }

    /**
     * @return the NextPageIndex
     */
    public Integer getNextPageIndex() {
        return currentPageIndex != getVoyageJournalPartition().size() ? currentPageIndex + 1 : null;
    }
}