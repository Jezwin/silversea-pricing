package com.silversea.aem.helper;

import com.silversea.aem.helper.crx.CrxQuerier;

import java.util.Arrays;

public class NewCruiseDisclaimerChecker {
    public static final String CRX_NODE_PATH = "/content/silversea-com/en/destinations/jcr:content/newItinerariesDisclaimer";

    private CrxQuerier crxQuerier;

    public NewCruiseDisclaimerChecker(CrxQuerier crxQuerier) {
        this.crxQuerier = crxQuerier;
    }

    public boolean needsDisclaimer(String cruiseCode) throws Exception {
        NewCruiseDisclaimerNode node = this.crxQuerier.get(CRX_NODE_PATH, NewCruiseDisclaimerNode.class);
        String[] cruiseCodes = splitCsv(node.getCruiseCodes());
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