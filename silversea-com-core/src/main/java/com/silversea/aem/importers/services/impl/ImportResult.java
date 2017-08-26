package com.silversea.aem.importers.services.impl;

/**
 * Object representing the result of an import
 */
public class ImportResult {

    private int successNumber = 0;

    private int errorNumber = 0;

    public ImportResult() {
    }

    public ImportResult(int successNumber, int errorNumber) {
        this.successNumber = successNumber;
        this.errorNumber = errorNumber;
    }

    public int getSuccessNumber() {
        return successNumber;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public void incrementSuccessNumber() {
        successNumber++;
    }

    public void incrementErrorNumber() {
        errorNumber++;
    }

    public void incrementSuccessOf(final int value) {
        successNumber += value;
    }

    public void incrementErrorOf(final int value) {
        errorNumber += value;
    }
}
