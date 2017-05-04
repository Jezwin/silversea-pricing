package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;

public class StringHelper extends WCMUsePojo {

    private String textTruncate;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);

        if(StringUtils.isNotEmpty(text)) {
            textTruncate = text.substring(0, limit).trim();
        }

    }

    /**
     * @return the page for a given path
     */
    public String getTextTruncate() {
        return textTruncate;
    }
    
    public static String getFormatWithoutSpecialCharcters(String format){
        format = format.replaceAll("[^a-zA-Z0-9 ]+","").replaceAll(" ", "-");
        return format;
    }
}