package com.silversea.aem.newcruisedisclaimer;

import com.silversea.aem.content.ContentLoader;

import java.util.Arrays;

public class NewCruiseDisclaimerChecker {
    public static final String CRX_NODE_PATH = "/content/silversea-com/en/destinations/jcr:content/newItinerariesDisclaimer";

    private ContentLoader contentLoader;

    public NewCruiseDisclaimerChecker(ContentLoader contentLoader) {
        this.contentLoader = contentLoader;
    }

    public boolean needsDisclaimer(String cruiseCode) throws Exception {
        NewCruiseDisclaimerModel model = this.contentLoader.get(CRX_NODE_PATH, NewCruiseDisclaimerModel.class);
        String[] cruiseCodes = splitCsv(model.getCruiseCodes());
        return containsIgnoreCase(cruiseCode, cruiseCodes);
    }

    private static boolean containsIgnoreCase(String value, String[] array) {
        return Arrays.stream(array)
                .anyMatch(value::equalsIgnoreCase);
    }

    private static String[] splitCsv(String csvString) {
        return csvString
                .trim()
                .split("\\s*,\\s*");

    }
}
