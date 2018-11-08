package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.silversea.aem.models.CruiseModelLight;

import java.util.Comparator;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.lang.Integer.parseInt;

public final class DurationFilter extends AbstractFilter<Range<Integer>> {

    private static final RangeSet<Integer> DURATIONS = TreeRangeSet.create();

    static {
        DURATIONS.add(Range.closed(1, 8));
        DURATIONS.add(Range.closed(9, 12));
        DURATIONS.add(Range.closed(13, 18));
        DURATIONS.add(Range.atLeast(19));
    }

    DurationFilter() {
        super("duration");
    }

    @Override
    protected Stream<FilterRow<Range<Integer>>> projection(CruiseModelLight cruise) {
        Range<Integer> integerRange = DURATIONS.rangeContaining(parseInt(cruise.getDuration()));
        return Stream.of(new FilterRow<>(integerRange, DurationFilter::rangeToString, integerRange.lowerEndpoint().toString(), ENABLED));
    }

    private static String rangeToString(Range<Integer> range) {
        if (range.hasUpperBound()) {
            return range.lowerEndpoint() + " - " + range.upperEndpoint();
        } else {
            return range.lowerEndpoint() + "+";
        }
    }

    protected Comparator<FilterRow<Range<Integer>>> comparator() {
        return Comparator.comparing(FilterRow::getKey);
    }
}
