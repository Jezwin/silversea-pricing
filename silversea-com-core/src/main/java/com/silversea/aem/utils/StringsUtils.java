package com.silversea.aem.utils;

import org.apache.commons.lang3.StringUtils;

public class StringsUtils {

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
