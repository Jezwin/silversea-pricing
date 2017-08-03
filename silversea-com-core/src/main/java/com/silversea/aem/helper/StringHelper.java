package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.commons.lang3.StringUtils;

public class StringHelper extends WCMUsePojo {

    private String textTruncate;

    private String textTruncateEnd;

    private String textTruncateDot;

    private String textTruncateDotEnd;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        Integer limit = get("limit", Integer.class);

        if (StringUtils.isNotEmpty(text)) {
            textTruncate = (text.length() > limit) ? text.substring(0, limit).trim() : text;
            textTruncateEnd = (text.length() > limit) ? text.substring(limit, text.length()).trim() : null;

            String[] textSplit = textTruncate.split("\\.");
            textTruncateDot = "";
            for (Integer i = 0; i < textSplit.length - 1; i++){
                textTruncateDot += textSplit[i] + ". ";
            }
            if (textSplit.length > 0)
                textTruncateDotEnd = textSplit[textSplit.length - 1] + textTruncateEnd;
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

    public String getTextTruncateDotEnd() {
        return textTruncateDotEnd;
    }

    public String getTextTruncateDot() {
        return textTruncateDot;
    }

    public String getTextTruncateEnd() {
        return textTruncateEnd;
    }

    /**
     * @see #getFormatWithoutSpecialCharacters(String)
     * @param text
     * @return
     */
    @Deprecated
    public static String getFormatWithoutSpecialCharcters(String text) {
        if (text == null) {
            return null;
        }

        text = StringUtils.stripAccents(text);
        text = text.replaceAll("[^a-zA-Z0-9 ]+", "").trim().replaceAll("\\s+", "-").toLowerCase();

        return text;
    }

    /**
     * @param text initial text
     * @return formatted text
     */
    public static String getFormatWithoutSpecialCharacters(final String text) {
        if (text != null) {
            return StringUtils.stripAccents(text)
                    .replaceAll("[^a-zA-Z0-9 ]+", "")
                    .trim()
                    .replaceAll("\\s+", "-")
                    .toLowerCase();
        }

        return null;
    }
}