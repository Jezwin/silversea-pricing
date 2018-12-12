package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.silversea.aem.models.CruiseModelLight;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

public abstract class AbstractFilter<T> {

    protected enum Sorting {
        ASC(id -> id), DESC(Comparator::reversed), NONE(id -> id), HIDDEN(id -> id);


        private UnaryOperator<Comparator<CruiseModelLight>> operator;

        Sorting(UnaryOperator<Comparator<CruiseModelLight>> operator) {
            this.operator = operator;
        }

        public UnaryOperator<Comparator<CruiseModelLight>> getOperator() {
            return operator;
        }


    }

    private final String kind;
    private final BiFunction<FindYourCruise2018Use, Sorting, Comparator<CruiseModelLight>> sortedBy;
    private Set<FilterRow<T>> rows;
    private Set<FilterRow<T>> selectedRows;
    private Sorting sorting;
    private boolean visible;
    private boolean open;

    AbstractFilter(String kind, Comparator<CruiseModelLight> sortedBy, Sorting sortingGiven) {
        this.kind = kind;
        this.sortedBy = (use, sorting) -> sorting.getOperator().apply(sortedBy);
        this.sorting = sortingGiven;
    }

    AbstractFilter(String kind, BiFunction<FindYourCruise2018Use, Sorting, Comparator<CruiseModelLight>> sortedBy, Sorting sorting) {
        this.kind = kind;
        this.sortedBy = sortedBy;
        this.sorting = sorting;
    }


    protected abstract Stream<FilterRow<T>> projection(CruiseModelLight cruiseModelLight);

    public Collection<FilterRow<T>> getRows() {
        return rows;
    }

    public String getKind() {
        return kind;
    }

    public final boolean isSelected() {
        return !selectedRows.isEmpty();
    }

    public final FilterRowState retrieveState(String key) {
        return rows.stream().filter(row -> row.getKey().equals(key)).findAny().map(FilterRow::getState)
                .orElseThrow(() -> new IllegalArgumentException("Could not find " + key));
    }

    final boolean matches(CruiseModelLight cruise) {
        return selectedRows.isEmpty() || matches(selectedRows, cruise);
    }

    void disableMissingLabels(Collection<CruiseModelLight> cruises) {
        rows.forEach(row -> row.setState(rowShouldBeEnabled(cruises, row) ? ENABLED : DISABLED));
    }

    final void initAllValues(FindYourCruise2018Use use, String[] selectedKeys, Collection<CruiseModelLight> allCruises) {
        this.selectedRows = new HashSet<>();
        this.rows = retrieveAllValues(use, selectedKeys, this.selectedRows::add, allCruises);
    }

    protected Set<FilterRow<T>> retrieveAllValues(FindYourCruise2018Use use, String[] selectedKeys,
                                                  Consumer<FilterRow<T>> addToChosen, Collection<CruiseModelLight> allCruises) {
        Comparator<FilterRow<T>> comparator = this.comparator();
        Set<String> selected = new HashSet<>(Arrays.asList(selectedKeys));
        return allCruises.stream().flatMap(this::projection).distinct().peek(row -> {
            if (selected.contains(row.getKey())) {
                row.setState(CHOSEN);
                addToChosen.accept(row);
            }
        }).collect(toCollection(() -> new TreeSet<>(comparator)));
    }


    protected String[] selectedKeys(Map<String, String[]> properties, Map<String, String[]> httpRequest) {
        Optional<String[]> valueFromProperty = ofNullable(properties.get(getKind() + "Id"));
        if (valueFromProperty.isPresent()) {
            setVisible(false);
            return valueFromProperty.get();
        }
        setVisible(true);
        Optional<String[]> sortBy = ofNullable(httpRequest.get("sortby"));
        if (sortBy.isPresent()) {
            for (String sort : sortBy.get()) {
                //duration-asc or duration-desc
                String[] value = sort.split("-");
                if (value.length > 1 && value[0].equalsIgnoreCase(getKind())) {
                    setSorting(Sorting.valueOf(value[1].toUpperCase()));
                } else if (value.length > 1 && getSorting().equals(Sorting.ASC) || getSorting().equals(Sorting.DESC)) {
                    setSorting(Sorting.NONE);
                }
            }
        }
        return httpRequest.getOrDefault(getKind(), ArrayUtils.EMPTY_STRING_ARRAY);
    }

    protected Comparator<FilterRow<T>> comparator() {//this is overridden when needed
        return Comparator.naturalOrder();
    }

    /**
     * Matching a single Filter
     *
     * @param selectedFilterRows The selected rows of a filter.
     * @param cruiseModelLight   A cruise.
     * @return True if the cruise matches that filter.
     */
    private boolean matches(Set<FilterRow<T>> selectedFilterRows, CruiseModelLight cruiseModelLight) {
        return projection(cruiseModelLight).anyMatch(selectedFilterRows::contains);
    }

    /**
     * Should the row be enabled?
     *
     * @param cruises All the cruises left.
     * @param row     The row.
     * @return True is any cruise matches the row.
     */
    private boolean rowShouldBeEnabled(Collection<CruiseModelLight> cruises, FilterRow<T> row) {
        return cruises.parallelStream().flatMap(this::projection).anyMatch(row::equals);
    }

    public int getSelectedCount() {
        return selectedRows.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFilter<?> that = (AbstractFilter<?>) o;
        return kind.equals(that.kind);
    }

    @Override
    public int hashCode() {
        return kind.hashCode();
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JsonElement toJson() {
        JsonArray array = new JsonArray();
        rows.forEach(row -> array.add(row.toJson()));
        return array;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {

        this.open = open;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Apply post filtering, such as removing not enabled lines.
     */
    protected void postFilter() {

    }

    protected void setRows(Set<FilterRow<T>> rows) {
        this.rows = rows;
    }

    public Sorting getSorting() {
        return sorting;
    }

    public void setSorting(Sorting sorting) {
        this.sorting = sorting;
    }

    public Comparator<CruiseModelLight> getSortedBy(FindYourCruise2018Use findYourCruise2018Use) {
        return sortedBy.apply(findYourCruise2018Use, getSorting());
    }
}
