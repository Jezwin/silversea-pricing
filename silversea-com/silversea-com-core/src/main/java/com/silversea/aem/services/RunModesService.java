package com.silversea.aem.services;

public interface RunModesService {

    /**
     * @return true if run mode is author
     */
    boolean isAuthor();

    /**
     * @return true if runmode is publish
     */
    boolean isPublish();

    /**
     * @return the current environment run mode
     */
    String getCurrentRunMode();
    String getCurrentRunModeFull();
}
