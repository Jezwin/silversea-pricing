package com.silversea.aem.utils;

import org.apache.commons.lang3.StringUtils;

public class StringsUtils {
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
