package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;

public class StringHelper extends WCMUsePojo {

    private String textTruncate;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);

        textTruncate = text.substring(0, limit).trim();
    }

    /**
     * @return the page for a given path
     */
    public String getTextTruncate() {
        return textTruncate;
    }
}