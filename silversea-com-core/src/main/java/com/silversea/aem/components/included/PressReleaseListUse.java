package com.silversea.aem.components.included;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import com.silversea.aem.filter.PressReleasesPageFilter;
import com.silversea.aem.models.PressReleaseModel;

public class PressReleaseListUse extends WCMUsePojo {
    private List<PressReleaseModel> pressReleasesCurrent;
    private List<List<PressReleaseModel>> pressReleasePartition;
    private Integer currentPageIndex;

    @Override
    public void activate() throws Exception {
        final InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(getResource());
        final Integer limit = inheritanceValueMap.getInherited("paginationLimit", 10);
        
    	String suffix = getRequest().getRequestPathInfo().getSuffix();
    	//press-releases.html/2.html
    	suffix = (suffix != null) ? suffix.replace(".html", "") : null;
		String[] splitSuffix = (suffix != null) ? StringUtils.split(suffix, '/') : null;
    	String currentPageParam = (splitSuffix != null && splitSuffix.length > 0) ? splitSuffix[0] : "1";
        
        currentPageIndex = Integer.parseInt(currentPageParam);

        final Iterator<Page> pressReleasePages = getCurrentPage().listChildren(new PressReleasesPageFilter(), true);

        // Get every press releases
        List<PressReleaseModel> pressReleases = new ArrayList<>();

        while (pressReleasePages.hasNext()) {
            Page pressRelease = pressReleasePages.next();

            if (pressRelease != null) {
                pressReleases.add(pressRelease.adaptTo(PressReleaseModel.class));
            }
        }

        // Get press release for the given page only
        pressReleasePartition = new ArrayList<>();
        pressReleasePartition = Lists.partition(pressReleases, limit);

        if (currentPageIndex - 1 < pressReleasePartition.size()) {
            pressReleasesCurrent = new ArrayList<>();
            pressReleasesCurrent = pressReleasePartition.get(currentPageIndex - 1);
        }
    }

    /**
     * @return the pressReleasesCurrent
     */ 
    public List<PressReleaseModel> getPressReleasesCurrent() {
        return pressReleasesCurrent;
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
    public List<List<PressReleaseModel>> getPressReleasePartition() {
        return pressReleasePartition;
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
        return currentPageIndex < pressReleasePartition.size() ? currentPageIndex + 1 : null;
    }

    /**
     * @return the Pagination
     */
    public List<Integer> getPagination() {
        Integer pageFrom = currentPageIndex > 2 ? currentPageIndex - 2 : 1 ;
        Integer pageTo = currentPageIndex < pressReleasePartition.size() - 2 ? currentPageIndex + 2 : pressReleasePartition.size();

        // Build integer list filled with int between pageFrom and pageTo
        return IntStream.rangeClosed(pageFrom, pageTo).boxed().collect(Collectors.toList());
    }
}