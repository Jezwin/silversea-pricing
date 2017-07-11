package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.commons.lang3.StringUtils;

public class StringHelper extends WCMUsePojo {

    private String textTruncate;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);

        if (StringUtils.isNotEmpty(text)) {
            textTruncate = (text.length() > limit) ? text.substring(0, limit).trim() : text;
        }

    }

    /**
     * TODO typo
     *
     * @return the page for a given path
     */
    public String getTextTruncate() {
        return textTruncate;
    }

    public static String getFormatWithoutSpecialCharcters(String text) {
        if (text == null) {
            return null;
        }

        text = StringUtils.stripAccents(text);
        text = text.replaceAll("[^a-zA-Z0-9 ]+", "").trim().replaceAll("\\s+", "-").toLowerCase();

        return text;
    }
}