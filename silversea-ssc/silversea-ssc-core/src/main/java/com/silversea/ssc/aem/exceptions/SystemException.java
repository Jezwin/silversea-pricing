package com.silversea.ssc.aem.exceptions;

/**
 * <p>
 * A simple exception that acts as the base class for any exceptions that an application can throw. This class provides
 * basic implementation with the following best practices:
 * </p>
 * <p>
 * <ul>
 * <li>
 * <b>Unchecked exception classes: </b>Unchecked (or runtime) exceptions free up developers from having to worry about
 * catching all exceptions close to where the exception was thrown. This allows application code to be less cluttered,
 * while at the same time allowing developers to catch and act upon exceptions that must be caught and handled before
 * the application can proceed.</li>
 * <li>
 * </ul>
 * </p>
 * 
 */
public class SystemException extends RuntimeException {
    /**
     * Default Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * holds an error string.
     */
    private final String errorString;

    /**
     * Initialises the exception using the provided error message.
     * 
     * @param message
     *            - the error message
     */
    public SystemException(final String message) {
        super();
        this.errorString = message;
    }

    /**
     * Initialises the exception using the error message and adds another exception in the exception chain.
     * 
     * @param message An error message.
     * @param throwable A @see java.lang.Throwable that needs to be linked to the current exception.
     */
    public SystemException(final String message, final Throwable throwable) {
        super(throwable);
        this.errorString = message;
    }

    /**
     * Gets the error string of exception which was used to initialize the exception.
     * 
     * @return String error string
     */
    public String getErrorString() {
        return errorString;
    }
}
