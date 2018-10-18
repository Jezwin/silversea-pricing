package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.base.Objects;
import com.silversea.aem.models.CruiseModelLight;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public abstract class AbstractFilter<T> {
    private final String kind;
    private List<FilterRow<T>> rows;

    AbstractFilter(String kind) {
        this.kind = kind;
    }

    protected abstract Collection<ValueLabel<T>> valueLabels(CruiseModelLight cruiseModelLight);


    public Collection<FilterRow<T>> getRows() {
        return rows;
    }

    public String getKind() {
        return kind;
    }

    public final boolean isSelected() {
        return rows.stream().anyMatch(row -> CHOSEN.equals(row.getState()));
    }

    public final FilterRowState retrieveState(String label) {
        return rows.stream().filter(row -> row.getLabel().equals(label)).findAny().map(FilterRow::getState)
                .orElseThrow(() -> new IllegalArgumentException("Could not find " + label));
    }

    final boolean matches(CruiseModelLight cruise) {
        Set<FilterRow<T>> selectedValues = rows.stream().filter(row -> CHOSEN.equals(row.getState())).collect(toSet());
        if (!selectedValues.isEmpty()) {
            return matches(selectedValues, cruise);
        }
        return true;
    }

    void setLabels(Set<String> enabledLabels) {
        rows.forEach(row -> row.setState(enabledLabels.contains(row.getLabel()) ? CHOSEN : ENABLED));
    }

    void disableMissingLabels(Collection<CruiseModelLight> cruises) {
        rows.forEach(row -> row.setState(cruiseMatchesTheRow(cruises, row) ? ENABLED : DISABLED));

    }

    final void initAllValues(List<CruiseModelLight> cruises) {
        this.rows = cruises.stream().flatMap(this::extractRows).collect(toList());
    }

    private boolean matches(Set<FilterRow<T>> selectedRows, CruiseModelLight cruiseModelLight) {
        //cartesian product, could be better
        for (FilterRow<T> selectedRow : selectedRows) {
            String selectedLabel = selectedRow.getLabel();
            for (ValueLabel<T> cruiseRow : valueLabels(cruiseModelLight)) {
                String cruiseLabel = cruiseRow.getLabel();
                if (selectedLabel.equals(cruiseLabel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean cruiseMatchesTheRow(Collection<CruiseModelLight> cruises, FilterRow<T> row) {
        return cruises.stream().flatMap(cruise -> valueLabels(cruise).stream()).map(ValueLabel::getLabel)
                .anyMatch(row.getLabel()::equals);
    }

    private Stream<? extends FilterRow<T>> extractRows(CruiseModelLight cruise) {
        return valueLabels(cruise).stream()
                .map(valueLabel -> new FilterRow<>(valueLabel.getValue(), valueLabel.getLabel(), ENABLED));
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
        return Objects.hashCode(kind);
    }


    static class ValueLabel<T> {
        private final String label;
        private final T value;

        ValueLabel(String label, T value) {
            requireNonNull(value, "value must not be null, label was " + label);
            requireNonNull(label, "Label must not be null,  value was of class " + value.getClass());
            this.label = label;
            this.value = value;
        }

        static <T> Collection<ValueLabel<T>> singleton(String label, T value) {
            return Collections.singletonList(new ValueLabel<>(label, value));
        }

        static Collection<ValueLabel<String>> singleton(String value) {
            return Collections.singletonList(new ValueLabel<>(value, value));
        }

        public String getLabel() {
            return label;
        }

        public T getValue() {
            return value;
        }
    }

}
