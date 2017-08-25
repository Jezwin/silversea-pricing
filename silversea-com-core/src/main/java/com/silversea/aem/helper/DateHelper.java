package com.silversea.aem.helper;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.adobe.cq.sightly.WCMUsePojo;

/**
 * Doc for format date
 * https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
 */
public class DateHelper extends WCMUsePojo {

    public String value;

    @Override
    public void activate() throws Exception {
        Calendar date = get("date", Calendar.class);
        String format = get("format", String.class);
        Integer month = get("month", Integer.class);
        Locale locale = getCurrentPage().getLanguage(false);
        SimpleDateFormat formatter;

        if (date != null && format != null) {
            formatter = new SimpleDateFormat(format, locale);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            value = formatter.format(date.getTime());
        }

        if (month != null) {
            DateFormatSymbols symbols = new DateFormatSymbols(locale);
            String[] monthNames = symbols.getMonths();
            value = monthNames[month - 1];
        }
    }

    public String getValue() {
        return value;
    }
}