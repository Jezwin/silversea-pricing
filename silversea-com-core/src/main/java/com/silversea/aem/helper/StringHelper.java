package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.commons.lang3.StringUtils;

public class StringHelper extends WCMUsePojo {

    private String textTruncate;
    private String textTruncateDot;
    private String textTruncateDotEnd;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);

        if (StringUtils.isNotEmpty(text)) {
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

    /**
     * @return the textTruncateDot
     */
    public String getTextTruncateDot() {
        return textTruncateDot;
    }

    /**
     * @return the textTruncateDotEnd
     */
    public String getTextTruncateDotEnd() {
        return textTruncateDotEnd;
    }
}