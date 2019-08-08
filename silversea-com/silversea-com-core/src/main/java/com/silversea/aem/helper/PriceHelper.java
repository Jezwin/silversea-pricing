package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceHelper extends WCMUsePojo {

    private String valueFormatted;

    @Override
    public void activate() throws Exception {
        final Long value = get("value", Long.class);
        Locale locale = getCurrentPage().getLanguage(false);
        valueFormatted = getValue(locale, value);
    }

    public static String getValue(Locale locale, Long value) {
        return value!=null ? NumberFormat.getNumberInstance(locale).format(value) : "";
    }

    /**
     * TODO typo
     * @return the value
     */
    public String getValueFormatted() {
        return valueFormatted;
    }
}