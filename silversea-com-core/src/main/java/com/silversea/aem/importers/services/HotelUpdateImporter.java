package com.silversea.aem.importers.services;

import java.io.IOException;

import com.day.cq.replication.ReplicationException;
import com.silversea.aem.exceptions.UpdateImporterExceptions;

/**
 * Created by mbennabi on 16/03/2017.
 */
@Deprecated
public interface HotelUpdateImporter {

    void updateImporData() throws IOException, ReplicationException, UpdateImporterExceptions;
    
    int getErrorNumber();

    int getSuccesNumber();
}
