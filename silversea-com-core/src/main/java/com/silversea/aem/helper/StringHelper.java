package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.xss.XSSAPI;

import com.adobe.cq.sightly.WCMUsePojo;

public class StringHelper extends WCMUsePojo {

    private String textTruncate;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);

        if (StringUtils.isNotEmpty(text)) {
            // Clean / encode for HTML
            XSSAPI xssAPI = getResourceResolver().adaptTo(XSSAPI.class);
            text = xssAPI.filterHTML(text);

            // Remove HTML tag
            String textHtmlStrip = text.replaceAll("\\<[^>]*>", "");
            textTruncate = (textHtmlStrip.length() > limit) ? textHtmlStrip.substring(0, limit).trim() : textHtmlStrip;

            // Return truncate text
            if (textHtmlStrip.length() > limit) {
                textTruncate = textHtmlStrip.substring(0, limit);
                if (textHtmlStrip.charAt(limit) != ' ') {
                    textTruncate = textTruncate.substring(0, textTruncate.lastIndexOf(" ")) + "...";
                }
            } else {
                textTruncate = textHtmlStrip;
            }
        }

    }

    /**
     * @return the textTruncate
     */
    public String getTextTruncate() {
        return textTruncate;
    }
}