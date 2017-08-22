package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceHelper extends WCMUsePojo {
    private String valueFormated ;

    @Override
    public void activate() throws Exception {
        final Long value = get("value", Long.class);
        Locale locale = getCurrentPage().getLanguage(false);
        valueFormated = getValue(locale, value);
    }

    public static String getValue(Locale locale, Long value) {
        return NumberFormat.getNumberInstance(locale).format(value);
    }

    /**
     * @return the value
     */
    public String getValueFormated() {
        return valueFormated;
    }
}