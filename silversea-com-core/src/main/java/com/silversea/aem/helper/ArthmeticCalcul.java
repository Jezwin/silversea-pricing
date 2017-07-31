package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;

/**
 * Doc for format date
 * https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
 */
public class ArthmeticCalcul extends WCMUsePojo {

    public Double value;

    @Override
    public void activate() throws Exception {
        Double param1 = get("param1", Double.class);
        Double param2 = get("param2", Double.class);
        if (param1 != null && param2 != null) {
            value = param1 % param2;
        }
    }

    public Double getModuloValue() {
        return value;
    }
}