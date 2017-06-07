package com.silversea.aem.components.beans;

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
