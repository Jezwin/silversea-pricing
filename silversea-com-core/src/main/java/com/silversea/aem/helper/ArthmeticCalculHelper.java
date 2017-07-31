package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.adobe.cq.sightly.WCMUsePojo;

public class ArthmeticCalculHelper extends WCMUsePojo {

    public Integer value;

    @Override
    public void activate() throws Exception {
        String paramStr1 = get("param1", String.class);
        String paramStr2 = get("param2", String.class);
        Integer param1 = NumberUtils.createInteger(paramStr1);
        Integer param2 = NumberUtils.createInteger(paramStr2);
        if (param1 != null && param2 != null) {
            value = param1 % param2;
            if (value<0 && param2>0) {
                value = param2;
            }
        }
    }

    public Integer getModuloValue() {
        return value;
    }
}