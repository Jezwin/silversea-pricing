package com.silversea.aem.importers.services;

import java.io.IOException;

/**
 * @author aurelienolivier
 */
public interface CitiesImporter {

    void importCities() throws IOException;

    void importCity(final String cityId);

    int getErrorNumber();

    int getSuccesNumber();
}
