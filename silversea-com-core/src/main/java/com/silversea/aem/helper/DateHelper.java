package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        Locale locale = getCurrentPage().getLanguage(false);
        SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
        if (date != null) {
            value = formatter.format(date.getTime());
        }
    }

    public String getValue() {
        return value;
    }
}