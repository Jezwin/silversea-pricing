package com.silversea.aem.importers.services;

import java.io.IOException;

/**
 * Created by mbennabi on 08/03/2017.
 */
public interface TravelAgenciesImporter {

    void importData() throws IOException;

    int getErrorNumber();

    int getSuccesNumber();
}
