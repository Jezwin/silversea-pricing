package com.silversea.aem.importers.services;

import java.io.IOException;

import com.day.cq.replication.ReplicationException;

/**
 * @author mbennabi
 */
public interface CitiesUpdateImporter {

    void importUpdateCities() throws IOException, ReplicationException;

    void importUpdateCity(final String cityId);
}
