package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.models.CruiseModelLight;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.CHOSEN;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.util.stream.Collectors.toCollection;

public class DepartureFilter extends AbstractFilter<YearMonth> {
    private static final Calendar GREGORIAN_CALENDAR = new GregorianCalendar();
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    DepartureFilter() {
        super("departure");
    }

    @Override
    protected Stream<FilterRow<YearMonth>> projection(CruiseModelLight cruise) {
        //we receive the """"correct"""" time but with the wrong timezone. Just use UTC always.
        YearMonth yearMonth = toLocaleYearMonth(cruise.getStartDate());
        return Stream.of(new DepartureRow(yearMonth, cruise.getLang()));
    }

    private synchronized YearMonth toLocaleYearMonth(Calendar cruiseDate) {
        GREGORIAN_CALENDAR.setTime(cruiseDate.getTime());
        GREGORIAN_CALENDAR.setTimeZone(UTC);
        return YearMonth.of(GREGORIAN_CALENDAR.get(Calendar.YEAR), GREGORIAN_CALENDAR.get(Calendar.MONTH) + 1);
    }

    private static DateTimeFormatter EN_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
    private static DateTimeFormatter ES_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es"));
    private static DateTimeFormatter DE_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMANY);
    private static DateTimeFormatter PT_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR"));
    private static DateTimeFormatter FR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRANCE);

    private static DateTimeFormatter getLocale(String lang) {
        switch (lang) {
            case "fr":
                return FR_FORMATTER;
            case "pt-br":
                return PT_FORMATTER;
            case "de":
                return DE_FORMATTER;
            case "es":
                return ES_FORMATTER;
            case "en":
            default:
                return EN_FORMATTER;
        }
    }

    public static class DepartureRow extends FilterRow<YearMonth> {
        private String[] split;

        public DepartureRow(YearMonth value, String lang) {
            super(value, date -> date.format(DepartureFilter.getLocale(lang)), value.toString(), ENABLED);
        }

        public String getYear() {
            if (split == null) {
                split = getLabel().split(" ");
            }
            return split[0];
        }

        public String getMonth() {
            if (split == null) {
                split = getLabel().split(" ");
            }
            return split[1];
        }
    }

}
