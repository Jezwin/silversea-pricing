package com.silversea.aem.helper;

import java.text.NumberFormat;
import java.util.Locale;

import com.adobe.cq.sightly.WCMUsePojo;

public class PriceHelper extends WCMUsePojo {
    private String valueFormated ;

    @Override
    public void activate() throws Exception {
        final String value = get("value", String.class);
        Locale locale = getCurrentPage().getLanguage(false);
        valueFormated = getValue(locale, value);
    }

    public static String getValue(Locale locale, String value) {
        return NumberFormat.getNumberInstance(locale).format(value);
    }

    /**
     * @return the value
     */
    public String getValueFormated() {
        return valueFormated;
    }
}