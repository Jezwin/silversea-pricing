package com.silversea.aem.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.adobe.cq.sightly.WCMUsePojo;

public class DateHelper extends WCMUsePojo {

    public String value;

    @Override
    public void activate() throws Exception {
        Calendar date = get("date", Calendar.class);
        String format = get("format", String.class);
        String local = get("local", String.class);
        Locale l = new Locale(local);
        SimpleDateFormat formatter = new SimpleDateFormat(format,l);
        value = formatter.format(date.getTime());
    }

    public String getValue() {
        return value;
    }

}