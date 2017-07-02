package com.silversea.aem.components.beans;

/**
 * Deprecated, use {@link com.silversea.aem.importers.services.impl.ImportResult} instead
 */
@Deprecated
public class ImporterStatus {

    private int errorNumber = 0;
    private int succesNumber = 0;

    public ImporterStatus() {

    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public int getSuccesNumber() {
        return succesNumber;
    }

    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    public void setSuccesNumber(int succesNumber) {
        this.succesNumber = succesNumber;
    }
    
    

}
