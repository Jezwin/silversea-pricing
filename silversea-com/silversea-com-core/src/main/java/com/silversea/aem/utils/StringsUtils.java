package com.silversea.aem.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

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

    /**
     * @param selectors: [templates,itineraryexcursions,itinerarydetail,papeete-to-sydney-4902] or [templates,itineraryexcursions,itinerarydetail,papeete-to-sydney-4902, country_XZ]
     * @return papeete-to-sydney-4902
     */
    public static Optional<String> filterDinamicSelector(String[] selectors, List<String> fixedSelector) {
        if (ArrayUtils.isNotEmpty(selectors) && fixedSelector != null) {
            for (String selector : selectors) {
                if (!fixedSelector.contains(selector) && !selector.startsWith("country_")) {
                    return Optional.of(selector);
                }
            }
        }
        return Optional.empty();
    }
}
