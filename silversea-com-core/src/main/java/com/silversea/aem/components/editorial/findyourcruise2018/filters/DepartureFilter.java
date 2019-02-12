package com.silversea.aem.components.editorial.findyourcruise2018.filters;

import com.silversea.aem.models.CruiseModelLight;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;
import static java.util.stream.Collectors.toList;

public class DepartureFilter extends AbstractFilter<YearMonth> {
    private final Calendar GREGORIAN_CALENDAR = new GregorianCalendar();
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final DateFormat PREFILTER_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    public DepartureFilter() {
        super("departure");
    }

    @Override
    public Stream<FilterRow<YearMonth>> projection(CruiseModelLight cruise) {
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
            return split[1];
        }

        public String getMonth() {
            if (split == null) {
                split = getLabel().split(" ");
            }
            return split[0];
        }
    }

    public static Predicate<CruiseModelLight> prefilterPeriods(String periodsList) {
        //eg 01/01/2019-31/12/2019,01/02/2020-28/02/2020:all 2019 and februrary 2020
        Function<String, Optional<Date>> toDate = source -> {
            try {
                return Optional.of(PREFILTER_FORMAT.parse(source));
            } catch (ParseException e) {
                return Optional.empty();
            }
        };
        List<Date[]> dates = new ArrayList<>();
        for (String period : periodsList.trim().split(",")) {
            String[] split = period.trim().split("-");
            if (split.length == 2) {
                Optional<Date> date1 = toDate.apply(split[0].trim());
                Optional<Date> date2 = toDate.apply(split[1].trim());
                if (date1.isPresent() && date2.isPresent()) {
                    dates.add(new Date[]{date1.get(), date2.get()});
                }
            }
        }
        return (cruise -> inPeriods(cruise, dates));
    }

    private static boolean inPeriods(CruiseModelLight cruise, List<Date[]> dates) {
        Calendar startDate = cruise.getStartDate();
        return dates.stream().anyMatch(date -> date[0].getTime() <= startDate.getTimeInMillis() && startDate.getTimeInMillis() <= date[1].getTime());
    }
}
