package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrUtil;

public class StringHelper extends WCMUsePojo {

    private String textTruncate;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);

        if(StringUtils.isNotEmpty(text)) {
            textTruncate = (text.length() > limit) ? text.substring(0, limit).trim() : text;
        }

    }

    /**
     * @return the page for a given path
     */
    public String getTextTruncate() {
        return textTruncate;
    }
    
    public static String getFormatWithoutSpecialCharcters(String format){
        format = format.replaceAll("[^a-zA-Z0-9 ]+","").trim().replaceAll("\\s+","-").toLowerCase();
        
//        format = JcrUtil.createValidName(format);
//        st.replaceAll("[\\s+]","")
        return format;
    }
}