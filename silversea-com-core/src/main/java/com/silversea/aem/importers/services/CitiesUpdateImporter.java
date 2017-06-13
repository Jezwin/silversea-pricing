package com.silversea.aem.importers.services;

import java.io.IOException;

import com.day.cq.replication.ReplicationException;
import com.silversea.aem.exceptions.UpdateImporterExceptions;

/**
 * @author mbennabi
 */
public interface CitiesUpdateImporter {

    void updateImporData() throws IOException, ReplicationException,UpdateImporterExceptions;

    void importUpdateCity(final String cityId);
    
    int getErrorNumber();

    int getSuccesNumber();
}
