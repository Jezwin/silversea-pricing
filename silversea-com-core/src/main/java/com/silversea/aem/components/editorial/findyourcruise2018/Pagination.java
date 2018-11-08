package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.components.editorial.FindYourCruiseUse;

public class Pagination {

    private final int totalResults;
    private final int currentPage;
    private final int totalPages;
    private final int pageSize;
    private final Iterable<Integer> pages;
    private final boolean isFirstPage;
    private final boolean isLastPage;

    public Pagination(int totalResults, int currentPage, int pageSize) {
        this.totalResults = totalResults;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((float) totalResults / (float) pageSize);
        this.pages = FindYourCruiseUse.buildPaginationV2(totalPages, currentPage);//to be moved
        this.isFirstPage = currentPage == 1;
        this.isLastPage = currentPage == totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Iterable<Integer> getPages() {
        return pages;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

}
