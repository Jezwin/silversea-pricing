package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.models.CruiseModelLight;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRow.singleton;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;

public class DepartureFilter extends AbstractFilter<YearMonth> {

    DepartureFilter() {
        super("departure");
    }

    @Override
    protected Stream<FilterRow<YearMonth>> projection(CruiseModelLight cruise) {
        Calendar startDate = cruise.getStartDate();
        YearMonth yearMonth = YearMonth.of(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH) + 1);
        return Stream.of(new FilterRow<>(yearMonth, date -> date.format(DepartureFilter.getLocale(cruise.getLang())), yearMonth.toString(), ENABLED));
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
}
