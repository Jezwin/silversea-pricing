package com.silversea.aem.importers.services;

import java.io.IOException;

import com.day.cq.replication.ReplicationException;

/**
 * Created by mbennabi on 17/03/2017.
 */
public interface ShoreExcursionsUpdateImporter {

    void updateImporData() throws IOException, ReplicationException;
}
