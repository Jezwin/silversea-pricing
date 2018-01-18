package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.xss.XSSAPI;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrUtil;

public class StringHelper extends WCMUsePojo {

    private String textTruncate;
    private String textValidName;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);

        if (StringUtils.isNotEmpty(text) && limit != null) {
            // Clean / encode for HTML
            XSSAPI xssAPI = getResourceResolver().adaptTo(XSSAPI.class);
            text = xssAPI.filterHTML(text);

            // Remove HTML tag
            String textHtmlStrip = text.replaceAll("\\<[^>]*>", "");
            textTruncate = (textHtmlStrip.length() > limit) ? textHtmlStrip.substring(0, limit).trim() : textHtmlStrip;

            // Return truncate text
            if (textHtmlStrip.length() > limit) {
                textTruncate = textHtmlStrip.substring(0, limit + 1);
                if (textHtmlStrip.charAt(limit) != '.') {
                    textTruncate = textTruncate.substring(0, textTruncate.lastIndexOf(".") + 1);
                }
            } else {
                textTruncate = textHtmlStrip;
            }
        }

        if (StringUtils.isNotEmpty(text)) {
            textValidName = JcrUtil.createValidName(text, JcrUtil.HYPHEN_LABEL_CHAR_MAPPING);
        }
    }

    /**
     * @return the textTruncate
     */
    public String getTextTruncate() {
        return textTruncate;
    }

    /**
     * @return the textValidName
     */
    public String getTextValidName() {
        return textValidName;
    }
}