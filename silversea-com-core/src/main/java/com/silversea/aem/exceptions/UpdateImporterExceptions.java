package com.silversea.aem.exceptions;

/**
 * TODO move to importer package
 */
public class UpdateImporterExceptions extends Exception {

    private static final long serialVersionUID = 1L;
    private String message;

    public UpdateImporterExceptions() {
    }

    public UpdateImporterExceptions(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
