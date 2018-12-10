package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.components.editorial.FindYourCruiseUse;

public class Pagination {

    private final int totalResults;
    private final int current;
    private final int end;
    private final int pageSize;
    private final Iterable<Integer> labels;
    private final boolean first;
    private final boolean last;

    public static final Pagination EMPTY = new Pagination(0, 0, 0);

    public Pagination(int totalResults, int current, int pageSize) {
        this.totalResults = totalResults;
        this.current = current;
        this.pageSize = pageSize;
        this.end = (int) Math.ceil((float) totalResults / (float) pageSize);
        this.labels = FindYourCruiseUse.buildPaginationV2(end, current);//to be moved
        this.first = current == 1;
        this.last = current == end;
    }

    public int getCurrent() {
        return current;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getEnd() {
        return end;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Iterable<Integer> getLabels() {
        return labels;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

}
