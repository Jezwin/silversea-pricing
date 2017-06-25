package com.silversea.aem.importers.services;

import com.day.cq.replication.ReplicationException;
import com.silversea.aem.exceptions.UpdateImporterExceptions;

import java.io.IOException;

/**
 * Use only one service see {@link CitiesImporter}
 * @author mbennabi
 */
@Deprecated
public interface CitiesUpdateImporter {

    void updateImportData() throws IOException, ReplicationException, UpdateImporterExceptions;

    void importUpdateCity(final String cityId);

    int getErrorNumber();

    int getSuccesNumber();
}
