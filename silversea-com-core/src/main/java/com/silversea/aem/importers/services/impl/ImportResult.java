package com.silversea.aem.importers.services.impl;

/**
 * Object representing the result of an import
 */
public class ImportResult {

    private int successNumber;

    private int errorNumber;

    public ImportResult(final int successNumber, final int errorNumber) {
        this.successNumber = successNumber;
        this.errorNumber = errorNumber;
    }

    public int getSuccessNumber() {
        return successNumber;
    }

    public int getErrorNumber() {
        return errorNumber;
    }
}
